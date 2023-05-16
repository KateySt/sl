package starlight.backend.email.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import starlight.backend.advice.config.AdviceConfiguration;
import starlight.backend.advice.model.entity.DelayedDeleteEntity;
import starlight.backend.advice.repository.DelayDeleteRepository;
import starlight.backend.email.model.ChangePassword;
import starlight.backend.email.model.Email;
import starlight.backend.email.model.EmailProps;
import starlight.backend.email.service.EmailService;
import starlight.backend.exception.user.UserNotFoundWithUUIDException;
import starlight.backend.exception.user.sponsor.SponsorCanNotSeeAnotherSponsor;
import starlight.backend.exception.user.sponsor.SponsorNotFoundException;
import starlight.backend.security.service.SecurityServiceInterface;
import starlight.backend.sponsor.SponsorRepository;
import starlight.backend.sponsor.model.entity.SponsorEntity;
import starlight.backend.sponsor.model.enums.SponsorStatus;

import java.io.File;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {
    private EmailProps emailProps;

    private JavaMailSender emailSender;

    private SponsorRepository sponsorRepository;
    private SecurityServiceInterface securityService;
    private DelayDeleteRepository delayDeleteRepository;

    private PasswordEncoder passwordEncoder;
    private AdviceConfiguration adviceConfiguration;

    @Override
    @Transactional
    public void recoverySponsorAccount(UUID uuid) {
        DelayedDeleteEntity delayedDeleteEntity = delayDeleteRepository.findByUserDeletingProcessUuid(uuid)

                .orElseThrow(() ->  new UserNotFoundWithUUIDException(String.valueOf(uuid)));

        long sponsorId = delayedDeleteEntity.getEntityId();
        SponsorEntity sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new SponsorNotFoundException(sponsorId));
        sponsor.setStatus(SponsorStatus.ACTIVE);
        sponsorRepository.save(sponsor);
//        delayedDeleteEntity.setDeleteDate(null);
        delayDeleteRepository.delete(delayedDeleteEntity);

    }

    @Override
    public void sendMail(Email email,long sponsorId, Authentication auth) {
        if (!securityService.checkingLoggedAndToken(sponsorId, auth)) {
            throw new SponsorCanNotSeeAnotherSponsor();
        }
        var sponsor = sponsorRepository.findById(sponsorId)
                .orElseThrow(() -> new SponsorNotFoundException(sponsorId));
        if (email.pathToAttachment() == null) {
            sendSimpleMessage(sponsor.getEmail(), email.subject(), email.text());
        } else {
            sendMessageWithAttachment(sponsor.getEmail(), email.subject(), email.text(), email.pathToAttachment());
        }
    }

    @Override
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailProps.username());
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
        MimeMessage message = emailSender.createMimeMessage();

        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true);

            helper.setFrom(emailProps.username());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment(Objects.requireNonNull(file.getFilename()), file);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        emailSender.send(message);
    }

    @Override
    @Transactional
    public void forgotPassword(HttpServletRequest request, String email) {
        var sponsor = sponsorRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(sponsor, token);
        sendSimpleMessage(email, "Password recovery",
                constructResetTokenEmail(getAppUrl(request), token));
    }

    public void sendRecoveryMessageSponsorAccount(String email, UUID uuid){
        sendSimpleMessage(email, "Recovery Account",
                constructSponsorRecoveryAccount(uuid.toString()));
    }

    @Override
    @Transactional
    public void recoveryPassword(String token, ChangePassword changePassword) {
        var sponsor = sponsorRepository.findByActivationCode(token).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        if (!sponsorRepository.existsByActivationCode(sponsor.getActivationCode())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid token");
        }
        sponsor.setPassword(passwordEncoder.encode(changePassword.password()));
        sponsor.setActivationCode(null);
        sponsorRepository.save(sponsor);
    }

    @Transactional
    public void createPasswordResetTokenForUser(SponsorEntity sponsor, String token) {
        sponsor.setActivationCode(token);
        sponsor.setExpiryDate(calculateExpiryDate(10));
        sponsorRepository.save(sponsor);
    }

    private Instant calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return cal.toInstant();
    }

    private String constructResetTokenEmail(String appUrl, String token) {
        return String.format("You received this email about a password recovery request. " +
                        "The link will be invalid after 10 minutes.\n" +
                        "If you haven't done so, please ignore this email and change your " +
                        "password on your account!\n%s\n",
                appUrl + "/api/v1/sponsors/recovery-password?token=" + token);
    }
    private String constructSponsorRecoveryAccount(String uuid){
        return String.format("This is your request to recovery your account in Starlight project.\n" +
                        "If you haven't done so, please dont ignore this email.\n" +
                        "If you want to reactivate your account, please click on the link below:\n" +
                        "%s\n" +
                        "The link will be invalid after " + adviceConfiguration.delayDays() + " days.\n",

                "http://dev.starlight.pepega.cloud/recovery?uuid=" + uuid);
    }

    private String getAppUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getHeader("host");
    }
}
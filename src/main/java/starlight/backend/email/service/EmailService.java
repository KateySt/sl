package starlight.backend.email.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import starlight.backend.email.model.ChangePassword;
import starlight.backend.email.model.Email;

import java.util.UUID;

public interface EmailService {

    void sendMail(Email email, long sponsorId, Authentication auth);

    void sendSimpleMessage(String to, String subject, String text);

    void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment);

    void forgotPassword(HttpServletRequest request, String email);

    void recoveryPassword(String token, ChangePassword changePassword);

    void recoverySponsorAccount(UUID uuid) throws Exception;
    void sendRecoveryMessageSponsorAccount(String email, UUID uuid);
}

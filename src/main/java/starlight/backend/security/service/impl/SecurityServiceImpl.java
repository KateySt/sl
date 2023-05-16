package starlight.backend.security.service.impl;


import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import starlight.backend.exception.EmailAlreadyOccupiedException;
import starlight.backend.exception.user.sponsor.SponsorNotFoundException;
import starlight.backend.exception.user.talent.TalentNotFoundException;
import starlight.backend.security.MapperSecurity;
import starlight.backend.security.model.UserDetailsImpl;
import starlight.backend.security.model.enums.Role;
import starlight.backend.security.model.request.NewUser;
import starlight.backend.security.model.response.SessionInfo;
import starlight.backend.security.service.SecurityServiceInterface;
import starlight.backend.sponsor.SponsorRepository;
import starlight.backend.sponsor.model.entity.SponsorEntity;
import starlight.backend.sponsor.model.enums.SponsorStatus;
import starlight.backend.user.model.entity.UserEntity;
import starlight.backend.user.repository.UserRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.MINUTES;

@AllArgsConstructor
@Service
@Transactional
public class SecurityServiceImpl implements SecurityServiceInterface {
    private final JwtEncoder jwtEncoder;
    private UserRepository repository;
    private SponsorRepository sponsorRepository;
    private MapperSecurity mapperSecurity;
    private PasswordEncoder passwordEncoder;

    @Override
    public SessionInfo loginInfo(Authentication auth) {
        var user = repository.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException(auth.getName() + " not found user by email"));
        var token = getJWTToken(mapperSecurity.toUserDetailsImpl(user), user.getUserId());
        return mapperSecurity.toSessionInfo(token);
    }

    @Override
    public boolean isSponsorActive(Authentication auth) {
        for (GrantedAuthority grantedAuthority : auth.getAuthorities()) {
            if (grantedAuthority.getAuthority().equals(Role.SPONSOR.getAuthority())) {
                var sponsor = sponsorRepository.findById(Long.valueOf(auth.getName()))
                        .orElseThrow(() -> new SponsorNotFoundException(Long.valueOf(auth.getName())));
                return sponsor.getStatus().equals(SponsorStatus.ACTIVE);
            }
        }
        return true;
    }
    @Override
    public SessionInfo register(NewUser newUser) {
        var user = saveNewUser(newUser);
        var token = getJWTToken(mapperSecurity.toUserDetailsImpl(user), user.getUserId());
        return mapperSecurity.toSessionInfo(token);
    }

    UserEntity saveNewUser(NewUser newUser) {
        if (repository.existsByEmail(newUser.email())) {
            throw new EmailAlreadyOccupiedException(newUser.email());
        }
        if (sponsorRepository.existsByEmail(newUser.email())) {
            throw new EmailAlreadyOccupiedException(newUser.email());
        }
        return repository.save(UserEntity.builder()
                .fullName(newUser.fullName())
                .email(newUser.email())
                .password(passwordEncoder.encode(newUser.password()))
                .authorities(Collections.singleton(Role.TALENT.getAuthority()))
                .build());
    }

    @Override
    public SessionInfo loginSponsor(Authentication auth) {
        var user = sponsorRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException(auth.getName() + " not found user by email"));
        var token = getJWTToken(mapperSecurity.toUserDetailsImplForSponsor(user), user.getSponsorId());
        return mapperSecurity.toSessionInfo(token);
    }

    @Override
    public SessionInfo registerSponsor(NewUser newUser) {
        var user = saveNewSponsor(newUser);
        var token = getJWTToken(mapperSecurity.toUserDetailsImplForSponsor(user), user.getSponsorId());
        return mapperSecurity.toSessionInfo(token);
    }

    SponsorEntity saveNewSponsor(NewUser newUser) {
        if (sponsorRepository.existsByEmail(newUser.email())) {
            throw new EmailAlreadyOccupiedException(newUser.email());
        }
        if (repository.existsByEmail(newUser.email())) {
            throw new EmailAlreadyOccupiedException(newUser.email());
        }
        return sponsorRepository.save(SponsorEntity.builder()
                .fullName(newUser.fullName())
                .email(newUser.email())
                .password(passwordEncoder.encode(newUser.password()))
                .authorities(Collections.singleton(Role.SPONSOR.getAuthority()))
                .unusedKudos(100) //TODO не хадкодить
                .status(SponsorStatus.ACTIVE)
                .build());
    }

    @Override
    @Transactional(readOnly = true)
    public String getJWTToken(UserDetailsImpl authentication, long id) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(180, MINUTES))
                .subject(String.valueOf(id))
                .claim("scope", createScope(authentication))
                .claim("status", authentication.getStatus())
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    @Transactional(readOnly = true)
    public String createScope(UserDetailsImpl authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }
}

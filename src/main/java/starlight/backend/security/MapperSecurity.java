package starlight.backend.security;

import org.mapstruct.Mapper;
import starlight.backend.security.model.UserDetailsImpl;
import starlight.backend.security.model.enums.Role;
import starlight.backend.security.model.response.SessionInfo;
import starlight.backend.sponsor.model.entity.SponsorEntity;
import starlight.backend.user.model.entity.UserEntity;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface MapperSecurity {
    default SessionInfo toSessionInfo(String token) {
        return SessionInfo.builder()
                .token(token)
                .build();
    }

    default UserDetailsImpl toUserDetailsImpl(UserEntity user) {
        return new UserDetailsImpl(
                user.getEmail(),
                user.getPassword());
    }

    default UserDetailsImpl toUserDetailsImplForSponsor(SponsorEntity sponsor) {
        return new UserDetailsImpl(
                sponsor.getEmail(),
                sponsor.getPassword(),
                Role.SPONSOR,
                sponsor.getStatus());
    }
}

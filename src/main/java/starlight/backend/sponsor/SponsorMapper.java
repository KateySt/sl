package starlight.backend.sponsor;

import org.mapstruct.Mapper;
import starlight.backend.kudos.model.entity.KudosEntity;
import starlight.backend.sponsor.model.entity.SponsorEntity;
import starlight.backend.sponsor.model.response.KudosWithProofId;
import starlight.backend.sponsor.model.response.SponsorFullInfo;

import static org.mapstruct.ReportingPolicy.IGNORE;

@Mapper(componentModel = "spring", unmappedTargetPolicy = IGNORE)
public interface SponsorMapper {
    default KudosWithProofId toKudosWithProofId(KudosEntity kudos) {
        return KudosWithProofId.builder()
                .kudosId(kudos.getKudosId())
                .proofId(kudos.getProof().getProofId())
                .talentId(kudos.getFollowerId())
                .countKudos(kudos.getCountKudos())
                .updateData(kudos.getUpdateData())
                .createData(kudos.getCreateData())
                .build();
    }

    default SponsorFullInfo toSponsorFullInfo(SponsorEntity sponsor) {
        return SponsorFullInfo.builder()
                .fullName(sponsor.getFullName())
                .avatar(sponsor.getAvatar())
                .company(sponsor.getCompany())
                .unusedKudos(sponsor.getUnusedKudos())
                .build();
    }
}

package starlight.backend.talent.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record TalentFullInfo(
        String fullName,
        String email,
        @JsonFormat(pattern="yyyy-MM-dd")
        LocalDate birthday,
        String avatar,
        String education,
        String experience,
        List<String> positions
) {
}

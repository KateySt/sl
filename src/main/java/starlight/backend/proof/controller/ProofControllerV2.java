package starlight.backend.proof.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import starlight.backend.proof.model.response.ProofPagePagination;
import starlight.backend.proof.service.ProofServiceInterface;
import starlight.backend.talent.model.response.TalentPagePagination;

@Slf4j
@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/v2")
@Tag(name = "Proof V2", description = "Proof API V2")
public class ProofControllerV2 {
    private ProofServiceInterface proofService;

    @Operation(summary = "Return list of all proofs for talent by talent_id",
            description = "Return list of all proofs for talent with sponsors who placed kudos on proofs"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Success",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = TalentPagePagination.class
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/talents/{talent-id}/proofs")
    public ProofPagePagination getTalentProofs(@PathVariable("talent-id") long talentId,
                                               Authentication auth,
                                               @RequestParam(defaultValue = "0") @Min(0) int page,
                                               @RequestParam(defaultValue = "5") @Positive int size,
                                               @RequestParam(defaultValue = "true") boolean sort,
                                               @RequestParam(defaultValue = "ALL") String status) {
        log.info("@GetMapping(\"/talents/{talent-id}/proofs\")");
        return proofService.getTalentAllProofsWithKudoses(auth, talentId, page, size, sort, status);
    }
}

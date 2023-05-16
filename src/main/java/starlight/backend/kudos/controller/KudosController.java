package starlight.backend.kudos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import starlight.backend.exception.proof.ProofNotFoundException;
import starlight.backend.kudos.KudosService;
import starlight.backend.kudos.model.entity.KudosEntity;
import starlight.backend.kudos.model.response.KudosOnProof;

@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
@Validated
@Tag(name = "Kudos", description = "Kudos API")
@RestController
public class KudosController {
    KudosService kudosService;

    @Operation(
            summary = "Get all kudos on proof",
            description = "Get all kudos on proof and boolean flag if you as a Sponsor already kudosed this proof"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = ProofNotFoundException.class
                            )
                    )
            )
    })
    @GetMapping("/proofs/{proof-id}/kudos")
    public KudosOnProof getKudosOnProof(@PathVariable("proof-id") long proofId,
                                        Authentication auth) {
        log.info("@GetMapping(\"/proofs/{proof-id}/kudos\")");
        log.info("Getting proof-id = {}", proofId);
        return kudosService.getKudosOnProof(proofId, auth);
    }


    @Operation(
            summary = "Add kudos on proof",
            description = "Add kudos from Sponsor on proof"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = Exception.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = Exception.class
                            )
                    )
            )
    })
    @PostMapping("/proofs/{proof-id}/kudos")
    @ResponseStatus(HttpStatus.CREATED)
    public KudosEntity addKudos(@PathVariable("proof-id") long proofId,
                                          @RequestParam int kudos,
                                          Authentication auth
    ) {
        log.info("@PostMapping(\"/proofs/{proof-id}/kudos\")");
        log.info("Getting proof-id = {}", proofId);
        return kudosService.addKudosOnProof(proofId, kudos, auth);
    }
}

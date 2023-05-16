package starlight.backend.exception.proof;

public class UserCanNotEditProofNotInDraftException extends RuntimeException {
    public UserCanNotEditProofNotInDraftException() {
        super("You cannot change proof with status not a DRAFT.");
    }
}

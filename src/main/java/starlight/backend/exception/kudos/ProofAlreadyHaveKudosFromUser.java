package starlight.backend.exception.kudos;

public class ProofAlreadyHaveKudosFromUser extends RuntimeException {
    public ProofAlreadyHaveKudosFromUser() {
        super("Proof already have kudos from user");
    }
}

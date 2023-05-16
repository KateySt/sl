package starlight.backend.exception.proof;

public class UserAccesDeniedToProofException extends RuntimeException{
    public UserAccesDeniedToProofException() {
        super("You do not have access to change this proof because your id does not match the id of the account to which it is linked.");
    }
}
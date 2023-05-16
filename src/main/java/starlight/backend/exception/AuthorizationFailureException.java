package starlight.backend.exception;

public class AuthorizationFailureException extends RuntimeException{
    public AuthorizationFailureException() {
        super("Authorization failure");
    }
}

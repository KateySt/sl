package starlight.backend.exception;

public class EmailAlreadyOccupiedException extends RuntimeException{
    public EmailAlreadyOccupiedException(String email) {
        super("Email '" + email + "' is already occupied");
    }
}

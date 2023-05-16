package starlight.backend.exception.kudos;

public class NotEnoughKudosException extends RuntimeException{
    public NotEnoughKudosException() {
        super("Not enough kudos on your account");
    }
}

package starlight.backend.exception.kudos;

public class KudosRequestMustBeNotZeroException extends RuntimeException{
    public KudosRequestMustBeNotZeroException() {
        super("Kudos request must be not zero");
    }
}

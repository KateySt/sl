package starlight.backend.exception.kudos;

public class YouCanNotReturnMoreKudosThanGaveException extends RuntimeException{
    public YouCanNotReturnMoreKudosThanGaveException() {
        super("You can't return more kudos than gave");
    }
}

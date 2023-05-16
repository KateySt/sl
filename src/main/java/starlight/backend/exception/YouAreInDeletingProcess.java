package starlight.backend.exception;

public class YouAreInDeletingProcess extends RuntimeException{
    public YouAreInDeletingProcess() {
        super("You are in deleting process");
    }
}

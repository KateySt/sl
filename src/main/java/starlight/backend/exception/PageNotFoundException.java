package starlight.backend.exception;

public class PageNotFoundException extends RuntimeException{
    public PageNotFoundException(int page) {
        super("No such page "+page);
    }
}

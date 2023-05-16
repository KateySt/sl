package starlight.backend.exception.kudos;

public class KudosNotFound extends RuntimeException {
    public KudosNotFound(long kudosId) {
        super("Kudos with id " + kudosId + "not found");
    }
}

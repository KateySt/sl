package starlight.backend.exception.user;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserNotFoundInDelayedDeleteRepository extends RuntimeException{
    public UserNotFoundInDelayedDeleteRepository(long id) {
        super("User with id " + id + " not found in delayed delete repository");
        log.warn("User not found in DelayDeleteRepository: {}", id);
    }
    public UserNotFoundInDelayedDeleteRepository(String id) {
        super("User with id " + id + " not found in delayed delete repository");
        log.warn("User not found in DelayDeleteRepository: {}", id);
    }

}

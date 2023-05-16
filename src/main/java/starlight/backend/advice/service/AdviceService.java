package starlight.backend.advice.service;


import java.util.UUID;

public interface AdviceService {
    void deleteAccounts();

    UUID getUUID(long entityId);
}

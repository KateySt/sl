package starlight.backend.proof.model.enums;

public enum Status {
    DRAFT, PUBLISHED, HIDDEN, ALL;

    public String getStatus() {
        return name();
    }
}

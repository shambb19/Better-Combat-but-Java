package exception;

public class CampaignSaveError extends RuntimeException {
    public CampaignSaveError(String message) {
        super(message);
    }
}

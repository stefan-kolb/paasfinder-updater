import java.io.IOException;

/**
 * For UpdateClient Testing
 */
public class Main {
    public static void main(String[] args) throws IOException {
        UpdateClient client = new UpdateClient();
        final String branchName = "updating-" + "test-" + UpdateService.generateUniqueId();

        client.createBranch(branchName);
        client.updateFile("gondor", "Neue Zeile", "update acquia_cloud", branchName);
        client.createPullRequest("title", "message", branchName);
    }
}
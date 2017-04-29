import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        UpdateClient client = new UpdateClient();
        final String branchName = "updating-" + "bla" + "-" + UpdateService.generateUniqueId();

        client.createBranch(branchName);
        client.updateFile("acquia_cloud", "Neue Zeile", "update acquia_cloud", branchName);
        client.createPullRequest("title", "message", branchName);
    }
}
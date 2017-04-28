import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        UpdateClient client = new UpdateClient("vasilpet", "nachos");
        client.createNewBranch("tiptop");
        client.updateFile("README.md", "Geheim", "tiptop");
        client.createPullRequest("new title", "message", "tiptop");
    }
}
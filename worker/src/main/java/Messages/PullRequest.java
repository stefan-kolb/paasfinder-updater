package Messages;

public class PullRequest {

    private final String title;
    private final String head;
    private final String base = "master";
    private final boolean maintainer_can_modify = true;

    public PullRequest(String head){
        this.title = "PR " + head;
        this.head = head;
    }

}
package Messages;

public class Branch {

    private final String ref;
    private final String sha;

    public Branch(String branchName, String masterSHA){
        this.ref = "refs/heads/" + branchName;
        this.sha = masterSHA;
    }

}

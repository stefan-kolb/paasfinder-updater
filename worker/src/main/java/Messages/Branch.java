package Messages;

import java.io.Serializable;

public class Branch implements Serializable{

    private static final transient long serialVersionUID = 1L;
    private final transient String branchName;

    private final String ref;
    private final String sha;

    public Branch(String branchName, String masterSHA){
        super();
        this.branchName = branchName;

        this.ref = "refs/heads/" + branchName;
        this.sha = masterSHA;
    }

    public String getBranchName(){
        return branchName;
    }

}
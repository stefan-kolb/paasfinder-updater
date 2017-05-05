package Messages;

import org.apache.commons.lang.RandomStringUtils;

import java.io.Serializable;

public class Branch implements Serializable{

    private static final transient long serialVersionUID = 1L;
    private final transient int ID_LENGTH = 10;
    private final transient String branchName;

    private final String ref;
    private final String sha;

    public Branch(String vendorKey, String masterSHA){
        super();
        this.branchName = "updating-" + vendorKey + "-" + generateUniqueId();

        this.ref = "refs/heads/" + branchName;
        this.sha = masterSHA;
    }

    private String generateUniqueId() {
        return RandomStringUtils.randomAlphanumeric(ID_LENGTH);
    }

    public String getBranchName(){
        return branchName;
    }

}
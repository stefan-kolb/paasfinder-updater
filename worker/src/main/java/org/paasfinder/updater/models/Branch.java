package org.paasfinder.updater.models;

public class Branch {
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
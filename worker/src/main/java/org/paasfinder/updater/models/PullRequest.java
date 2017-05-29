package org.paasfinder.updater.models;

public class PullRequest {

    private final String title;
    private final String head;
    private final String base = "master";
    private final String body;
    private final boolean maintainerCanModify = true;

    public PullRequest(String head, String message){
        this.title = "PR " + head;
        this.head = head;
        this.body = message;
    }

}
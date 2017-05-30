package org.paasfinder.updater.models;

/**
 * @see https://developer.github.com/v3/pulls/#create-a-pull-request
 */
public class PullRequest {

    private final String title;
    private final String head;
    private final String body;
    private final String base = "master";
    private final boolean maintainerCanModify = true;

    public PullRequest(String head, String title, String message){
        this.title = title;
        this.head = head;
        this.body = message;
    }

}
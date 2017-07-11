package org.paasfinder.updater.models;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @see https://developer.github.com/v3/repos/contents/#update-a-file
 */
public class File {
    private final transient String vendorKey;
    private final transient String contributorName;
    private final transient String contributorEmail;

    private final String message;
    private final String content;
    private final String sha;
    private final String branch;
    private Author author = null;

    public File(JsonObject data, JsonObject metadata, String fileSHA, String branch){
        this.contributorName = metadata.get("contributor_name").getAsString();
        this.contributorEmail = metadata.get("contributor_email").getAsString();
        this.vendorKey = metadata.get("vendor_key").getAsString();

        this.message = String.format("%s Profile Update", data.get("name"));
        this.content = encodeData(data);
        this.sha = fileSHA;
        this.branch = branch;
        Author author = new Author(contributorName, contributorEmail);
        if (!author.getName().isEmpty() && !author.getEmail().isEmpty()) {
            this.author = author;
        }
    }

    private String encodeData(JsonObject profile){
        String content = new GsonBuilder().setPrettyPrinting().create().toJson(profile);
        String encodedContent = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
        return encodedContent;
    }

    public String getVendorKey(){
        return vendorKey;
    }
}

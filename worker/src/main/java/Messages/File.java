package Messages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class File {

    private final String message;
    private final String content;
    private final String sha;
    private final String branch;

    public File(JsonObject vendorJSON, String fileSHA, String branch){
        this.message = prepareMessage(vendorJSON);
        this.content = prepareContent(vendorJSON);
        this.sha = fileSHA;
        this.branch = branch;
    }

    private String prepareMessage(JsonObject vendorJSON){
        return "Updating " + vendorJSON.get("vendorKey").getAsString() + ". " + vendorJSON.get("contributorMessage").getAsString();
    }

    private String prepareContent(JsonObject vendorJSON){
        vendorJSON.remove("contributorName");
        vendorJSON.remove("contributorEmail");
        vendorJSON.remove("contributorMessage");
        vendorJSON.remove("vendorKey");
        String content = new GsonBuilder().setPrettyPrinting().create().toJson(vendorJSON);
        String encodedContent = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));

        return encodedContent;
    }
}
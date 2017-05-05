package Messages;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class File implements Serializable{

    // will not be included in the result JSON
    private static final transient long serialVersionUID = 1L;
    private final transient String vendorKey;
    private final transient String contributorName;
    private final transient String contributorEmail;
    private final transient String contributorMessage;

    private final String message;
    private final String content;
    private final String sha;
    private final String branch;

    public File(JsonObject vendor, String fileSHA, String branch){
        super();
        this.contributorName = getAndRemoveProperty(vendor, "contributorName");
        this.contributorEmail = getAndRemoveProperty(vendor, "contributorEmail");
        this.contributorMessage = getAndRemoveProperty(vendor, "contributorMessage");
        this.vendorKey = getAndRemoveProperty(vendor, "vendorKey");

        this.message = "Updating " + vendorKey + ". " + contributorMessage;
        this.content = prepareContent(vendor);
        this.sha = fileSHA;
        this.branch = branch;
    }

    private String prepareContent(JsonObject vendor){
        String content = new GsonBuilder().setPrettyPrinting().create().toJson(vendor);
        String encodedContent = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
        return encodedContent;
    }

    private String getAndRemoveProperty(JsonObject json, String property){
        String result = json.get(property).getAsString();
        json.remove(property);
        return result;
    }

    public String getVendorKey(){
        return vendorKey;
    }

}
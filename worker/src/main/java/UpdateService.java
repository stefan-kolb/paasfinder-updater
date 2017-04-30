import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang.RandomStringUtils;
import java.io.IOException;
import static spark.Spark.*;

public class UpdateService {

    private static final int ID_LENGTH = 10;
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static JsonParser jp = new JsonParser();

    public static String generateUniqueId() {
        return RandomStringUtils.randomAlphanumeric(ID_LENGTH);
    }

    public static void main( String[] args) {
        port(9090);

        before((request,response)-> {
            response.header("Access-Control-Allow-Origin", "*");
        });

        options("/*", (request,response)-> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if(accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        post("/vendor", "application/json", (request, response) -> {
            JsonObject jo = (JsonObject) jp.parse(request.body());
            final String vendorKey =  jo.get("vendorKey").getAsString();
            final String branchName = "updating-" + vendorKey + "-" + generateUniqueId();
            final String updateMessage = "Updating " + vendorKey + ". " + jo.get("contributorMessage").getAsString();
            final String titlePR = "PR " + vendorKey;
            final String messagePR = "";

            jo.remove("contributorName");
            jo.remove("contributorEmail");
            jo.remove("contributorMessage");
            jo.remove("vendorKey");
            final String vendorJson = gson.toJson(jo);

            try {
                UpdateClient client = new UpdateClient();
                client.createBranch(branchName);
                client.updateFile(vendorKey, vendorJson, updateMessage, branchName);
                client.createPullRequest(titlePR, messagePR, branchName);
            } catch (IOException e){
            }
            response.status(200);
            return "OK";
        });

    }
}
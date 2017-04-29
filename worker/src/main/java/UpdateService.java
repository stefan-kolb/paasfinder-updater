import org.apache.commons.lang.RandomStringUtils;
import org.json.JSONObject;
import static spark.Spark.*;

public class UpdateService {

    public static final int ID_LENGTH = 10;

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
            JSONObject json = new JSONObject(request.body());

            final String fileName =  json.get("vendorAPIKey").toString();
            final String branchName = "updating-" + fileName + "-" + generateUniqueId();

            UpdateClient client = new UpdateClient();
            client.createBranch(branchName);
            client.updateFile(fileName, json.toString(), "update message", branchName);
            client.createPullRequest("title", "message", branchName);

            response.status(200);
            return "OK";
        });

    }
}
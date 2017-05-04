import Messages.Branch;
import Messages.File;
import Messages.PullRequest;
import com.google.gson.JsonObject;
import org.apache.commons.lang.RandomStringUtils;
import java.io.IOException;
import static spark.Spark.*;

public class UpdateService {

    private static final int ID_LENGTH = 10;
    private static final UpdateClient client = new UpdateClient();

    public static String generateUniqueId() {
        return RandomStringUtils.randomAlphanumeric(ID_LENGTH);
    }

    public static void main(String[] args) {
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
            JsonObject jo = client.jsonParser.parse(request.body()).getAsJsonObject();
            final String vendorKey =  jo.get("vendorKey").getAsString();
            final String branchName = "updating-" + vendorKey + "-" + generateUniqueId();

            try {
                final Branch branch = new Branch(branchName, client.getMasterSHA());
                client.postBranch(branch);
            } catch (IOException e){
            }

            try {
                final File file = new File(jo, client.getFileSHA(vendorKey), branchName);
                client.putFile(file, vendorKey);
            } catch (IOException e){
            }

            try {
                PullRequest pullRequest = new PullRequest(branchName);
                client.postPullRequest(pullRequest);
            } catch (IOException e){
            }

            response.status(200);
            return "OK";
        });

    }
}
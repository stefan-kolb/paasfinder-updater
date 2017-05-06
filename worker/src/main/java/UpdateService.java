import Messages.*;
import com.google.gson.JsonObject;
import java.io.IOException;

import static spark.Spark.*;

public class UpdateService {

    private static final UpdateClient client = new UpdateClient();

    public static void main(String[] args) {
        port(9090);

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
        });

        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        post("/vendor", "application/json", (request, response) -> {
            JsonObject data = client.jsonParser.parse(request.body()).getAsJsonObject();
            final String vendorKey = data.get("vendorKey").getAsString();

            final Branch branch = new Branch(vendorKey, client.getMasterSHA());
            try {
                client.postBranch(branch);
            } catch (IOException e) {
                response.status(422);
                return "Error while posting branch";
            }

            try {
                final File file = new File(data, client.getFileSHA(vendorKey), branch.getBranchName());
                client.putFile(file);
            } catch (IOException e) {
                response.status(422);
                return "Error while updating vendor";
            }

            try {
                final PullRequest pullRequest = new PullRequest(branch.getBranchName());
                client.postPullRequest(pullRequest);
            } catch (IOException e) {
                response.status(422);
                return "Error while sending pull request";
            }

            response.status(200);
            return "OK";
        });

    }
}
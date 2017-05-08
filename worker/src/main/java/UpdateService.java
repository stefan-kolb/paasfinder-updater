import Messages.*;
import com.google.gson.JsonObject;
import org.apache.commons.lang.RandomStringUtils;

import java.io.IOException;

import static spark.Spark.*;

public class UpdateService {

    private static final UpdateClient client = new UpdateClient();
    private static final int BRANCH_ID_LENGTH = 10;

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
            final String branchName = "updating-" + vendorKey + "-" + RandomStringUtils.randomAlphanumeric(BRANCH_ID_LENGTH);

            // create branch
            try {
                final Branch branch = new Branch(branchName, client.getMasterSHA());
                client.postBranch(branch);
            } catch (IOException e) {
                response.status(422);
                return "Error while posting branch";
            }

            // update file
            try {
                final File file = new File(data, client.getFileSHA(vendorKey), branchName);
                client.putFile(file);
            } catch (IOException e) {
                response.status(422);
                return "Error while updating vendor";
            }

            // create pull request
            try {
                final PullRequest pullRequest = new PullRequest(branchName);
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
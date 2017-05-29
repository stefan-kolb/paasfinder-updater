package org.paasfinder.updater;

import java.io.IOException;
import java.time.LocalDateTime;

import com.google.gson.JsonObject;
import org.paasfinder.updater.models.Branch;
import org.paasfinder.updater.models.File;
import org.paasfinder.updater.models.PullRequest;

import static spark.Spark.before;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;

public class UpdateService {

    private static final GithubClient client = new GithubClient();

    public UpdateService() {
        port(getAssignedPort());

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
            final String branchName = String.format("update-%s-%s", vendorKey, LocalDateTime.now());
            final String message = data.get("contributorMessage").getAsString();

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
                final PullRequest pullRequest = new PullRequest(branchName, message);
                client.postPullRequest(pullRequest);
            } catch (IOException e) {
                response.status(422);
                return "Error while sending pull request";
            }

            response.status(200);
            return "OK";
        });
    }

    public static void main(String[] args) {
        new UpdateService();
    }

    private static int getAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567;
    }
}
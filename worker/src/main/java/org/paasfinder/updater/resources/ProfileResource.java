package org.paasfinder.updater.resources;

import java.io.IOException;
import java.time.LocalDateTime;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.paasfinder.updater.GithubClient;
import org.paasfinder.updater.UpdateService;
import org.paasfinder.updater.models.Branch;
import org.paasfinder.updater.models.File;
import org.paasfinder.updater.models.PullRequest;

import static spark.Spark.before;
import static spark.Spark.options;
import static spark.Spark.port;
import static spark.Spark.post;

public class ProfileResource {
    private static final GithubClient client = new GithubClient();
    private static final JsonParser jsonParser = new JsonParser();

    public ProfileResource() {
        port(UpdateService.getAssignedPort());

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
            JsonObject data = jsonParser.parse(request.body()).getAsJsonObject();

            final String vendorKey = data.get("vendorKey").getAsString();
            LocalDateTime time = LocalDateTime.now();
            String timestamp = String.format("%d%02d%02d-%02d%02d", time.getYear(), time.getMonthValue(), time.getDayOfMonth(), time.getHour(), time.getMinute());
            final String branchName = String.format("update-%s-%s", vendorKey, timestamp);
            final String message = data.get("contributorMessage").getAsString();
            final String title = String.format("%s Profile Update", data.get("name"));

            // create branch
            try {
                final Branch branch = new Branch(branchName, client.getLatestMasterSHA());
                client.createBranch(branch);
            } catch (IOException e) {
                response.status(422);
                return "Error while posting branch";
            }

            // update file
            try {
                final File file = new File(data, client.getLatestFileSHA(vendorKey), branchName);
                client.updateFile(file);
            } catch (IOException e) {
                // TODO retry? delete branch
                response.status(422);
                return "Error while updating vendor";
            }

            // create pull request
            try {
                final PullRequest pullRequest = new PullRequest(branchName, title, message);
                client.createPullRequest(pullRequest);
            } catch (IOException e) {
                // TODO retry? delete branch
                response.status(422);
                return "Error while sending pull request";
            }

            response.status(200);
            return "OK";
        });
    }
}

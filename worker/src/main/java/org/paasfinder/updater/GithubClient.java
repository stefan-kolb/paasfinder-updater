package org.paasfinder.updater;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.paasfinder.updater.models.Branch;
import org.paasfinder.updater.models.File;
import org.paasfinder.updater.models.PullRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GithubClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(GithubClient.class);

    private static JsonParser jsonParser = new JsonParser();
    private static Gson gson = new GsonBuilder().create();

    private final String repository = "stefan-kolb/worker";
    private final String oauthToken = System.getenv("GITHUB_OAUTH_TOKEN");
    private final String baseURL = "https://api.github.com/repos/" + repository;

    private final OkHttpClient client = new OkHttpClient();
    private final MediaType mediaType = MediaType.parse("application/json");

    public boolean createBranch(Branch branch) throws IOException {
        RequestBody requestBody = RequestBody.create(mediaType, gson.toJson(branch));
        Request request = new Request.Builder()
                .url(baseURL + "/git/refs")
                .post(requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "token " + oauthToken)
                .build();

        Response response = client.newCall(request).execute();
        LOGGER.info("Create Branch " + response);
        return response.isSuccessful();
    }

    public boolean deleteBranch(Branch branch) throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + "/git/refs/heads/" + branch.getBranchName())
                .delete()
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "token " + oauthToken)
                .build();

        Response response = client.newCall(request).execute();
        LOGGER.info("Deleted Branch " + response);
        return response.isSuccessful();
    }

    public boolean updateFile(File file) throws IOException {
        RequestBody requestBody = RequestBody.create(mediaType, gson.toJson(file));
        Request request = new Request.Builder()
                .url(baseURL + "/contents/profiles/" + file.getVendorKey() + ".json")
                .put(requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "token " + oauthToken)
                .build();

        Response response = client.newCall(request).execute();
        LOGGER.info("Update File " + response);
        return response.isSuccessful();
    }

    public boolean createPullRequest(PullRequest pullRequest) throws IOException {
        RequestBody body = RequestBody.create(mediaType, gson.toJson(pullRequest));
        Request request = new Request.Builder()
                .url(baseURL + "/pulls")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "token " + oauthToken)
                .build();

        Response response = client.newCall(request).execute();
        LOGGER.info("Create Pull Request " + response);
        return response.isSuccessful();
    }

    public String getLatestMasterSHA() throws IOException{
        Request request = new Request.Builder()
                .url(baseURL + "/git/refs/heads/master")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        JsonObject jo = jsonParser.parse(response.body().string()).getAsJsonObject();
        return jo.get("object").getAsJsonObject().get("sha").getAsString();
    }

    public String getLatestFileSHA(String fileName) throws IOException{
        Request request = new Request.Builder()
                .url(baseURL + "/contents/profiles/" + fileName + ".json")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        JsonObject jo = jsonParser.parse(response.body().string()).getAsJsonObject();
        return jo.get("sha").getAsString();
    }
}

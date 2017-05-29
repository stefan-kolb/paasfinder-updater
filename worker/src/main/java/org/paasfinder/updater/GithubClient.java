package org.paasfinder.updater;

import java.io.IOException;
import java.util.logging.Logger;

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

public class GithubClient {
    private static final Logger LOGGER = Logger.getLogger(GithubClient.class.getName());

    public static JsonParser jsonParser = new JsonParser();
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final String repository = "stefan-kolb/worker";
    // Authorization: token OAUTH-TOKEN
    private final String credentials = "49948254492ab32b3a2993fe96f0b1a3826d7c72";
    private final String baseURL = "https://api.github.com/repos/" + repository;

    private final OkHttpClient client = new OkHttpClient();
    private final MediaType mediaType = MediaType.parse("application/json");

    void postBranch(Branch branch) throws IOException {
        RequestBody requestBody = RequestBody.create(mediaType, gson.toJson(branch));
        Request request = new Request.Builder()
                .url(baseURL + "/git/refs")
                .post(requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "token " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        LOGGER.info("Create Branch " + response);
    }

    void putFile(File file) throws IOException {
        RequestBody requestBody = RequestBody.create(mediaType, gson.toJson(file));
        Request request = new Request.Builder()
                .url(baseURL + "/contents/profiles/" + file.getVendorKey() + ".json")
                .put(requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "token " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        LOGGER.info("Update File " + response);
    }

    void postPullRequest(PullRequest pullRequest) throws IOException {
        RequestBody body = RequestBody.create(mediaType, gson.toJson(pullRequest));
        Request request = new Request.Builder()
                .url(baseURL + "/pulls")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "token " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        LOGGER.info("Create Pull Request " + response);
    }

    String getMasterSHA() throws IOException{
        Request request = new Request.Builder()
                .url(baseURL + "/git/refs/heads/master")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        JsonObject jo = jsonParser.parse(response.body().string()).getAsJsonObject();
        return jo.get("object").getAsJsonObject().get("sha").getAsString();
    }

    String getFileSHA(String fileName) throws IOException{
        Request request = new Request.Builder()
                .url(baseURL + "/contents/profiles/" + fileName + ".json")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        JsonObject jo = jsonParser.parse(response.body().string()).getAsJsonObject();
        return jo.get("sha").getAsString();
    }
}
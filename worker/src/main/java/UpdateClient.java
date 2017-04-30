import com.google.gson.*;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UpdateClient {

    private final OkHttpClient client;
    private final String baseURL = "https://api.github.com/repos/update-bot/paas-profiles";
    private final String credentials = Base64.getEncoder().encodeToString("update-bot:ds457hrsf3".getBytes(StandardCharsets.UTF_8));
    private final MediaType mediaType = MediaType.parse("application/json");
    private static JsonParser jsonParser = new JsonParser();

    public UpdateClient() {
        super();
        this.client = new OkHttpClient();
    }

    public void createBranch(String branchName) throws IOException {
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("ref", "refs/heads/" + branchName);
        requestJson.addProperty("sha", getMasterSHA());

        RequestBody requestBody = RequestBody.create(mediaType, requestJson.toString());
        Request request = new Request.Builder()
                .url(baseURL + "/git/refs")
                .post(requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Create Branch " + response);
    }

    public void updateFile(String vendorKey, String content, String message, String branchName) throws IOException {
        final String encodedContent = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));

        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("message", message);
        requestJson.addProperty("content", encodedContent);
        requestJson.addProperty("sha", getFileSHA(vendorKey));
        requestJson.addProperty("branch", branchName);

        RequestBody requestBody = RequestBody.create(mediaType, requestJson.toString());
        Request request = new Request.Builder()
                .url(baseURL + "/contents/profiles/" + vendorKey + ".json")
                .put(requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Update File " + response);
    }

    public void createPullRequest(String title, String message, String head) throws IOException {
        JsonObject bodyJson = new JsonObject();
        bodyJson.addProperty("title", title);
        bodyJson.addProperty("body", message);
        bodyJson.addProperty("head", head);
        bodyJson.addProperty("base", "master");
        bodyJson.addProperty("maintainer_can_modify", true);

        RequestBody body = RequestBody.create(mediaType, bodyJson.toString());
        Request request = new Request.Builder()
                .url(baseURL + "/pulls")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Create Pull Request " + response);
    }

    public String getMasterSHA() throws IOException{
        Request request = new Request.Builder()
                .url(baseURL + "/git/refs/heads/master")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        JsonObject jo = jsonParser.parse(response.body().string()).getAsJsonObject();
        String masterSHA = jo.get("object").getAsJsonObject().get("sha").getAsString();

        System.out.println("Master SHA: " + masterSHA);
        return masterSHA;
    }

    public String getFileSHA(String fileName) throws IOException{
        Request request = new Request.Builder()
                .url(baseURL + "/contents/profiles/" + fileName + ".json")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        JsonObject jo = jsonParser.parse(response.body().string()).getAsJsonObject();
        String sha = jo.get("sha").getAsString();

        System.out.println("File SHA: " + sha);
        return sha;
    }

}
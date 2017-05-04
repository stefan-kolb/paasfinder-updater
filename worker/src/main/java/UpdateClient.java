import com.google.gson.*;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class UpdateClient {

    private final String user = System.getenv("UPDATEBOT_LOGIN");
    private final String password = System.getenv("UPDATEBOT_PASSWORD");
    private final String credentials = Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8));
    
    private final OkHttpClient client = new OkHttpClient();
    private final String baseURL = "https://api.github.com/repos/" + user + "/paas-profiles";

    private final MediaType mediaType = MediaType.parse("application/json");
    private static JsonParser jsonParser = new JsonParser();

    void createBranch(String branch) throws IOException {
        JsonObject json = Helper.buildBranchRequestJson(branch, getMasterSHA());
        RequestBody requestBody = RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url(baseURL + "/git/refs")
                .post(requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Create Branch " + response);
    }

    void updateFile(String vendorKey, String content, String message, String branchName) throws IOException {
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

    void createPullRequest(String title, String message, String head) throws IOException {
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

    String getMasterSHA() throws IOException{
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

    String getFileSHA(String fileName) throws IOException{
        Request request = new Request.Builder()
                .url(baseURL + "/contents/profiles/" + fileName + ".json")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        JsonObject jo = jsonParser.parse(response.body().string()).getAsJsonObject();
        System.out.println(jo);
        String sha = jo.get("sha").getAsString();

        System.out.println("File SHA: " + sha);
        return sha;
    }

}
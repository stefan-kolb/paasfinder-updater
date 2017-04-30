import okhttp3.*;
import org.json.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UpdateClient {

    private final OkHttpClient client;
    private final String baseURL = "https://api.github.com/repos/update-bot/paas-profiles";
    private final String credentials = Base64.getEncoder().encodeToString("update-bot:ds457hrsf3".getBytes(StandardCharsets.UTF_8));
    private final MediaType mediaType = MediaType.parse("application/json");

    public UpdateClient() {
        super();
        this.client = new OkHttpClient();
    }

    public void createBranch(String branchName) throws IOException {
        final String masterSHA = getMasterSHA();

        JSONObject requestJson = new JSONObject();
        requestJson.put("ref", "refs/heads/" + branchName);
        requestJson.put("sha", masterSHA);

        RequestBody requestBody = RequestBody.create(mediaType, requestJson.toString());
        Request request = new Request.Builder()
                .url(baseURL + "/git/refs")
                .post(requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Create Branch Response: " + response);
    }

    public void updateFile(String vendorKey, String content, String message, String branchName) throws IOException {
        final String fileSHA = getFileSHA(vendorKey);
        final String encodedContent = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));

        JSONObject requestJson = new JSONObject();
        requestJson.put("message", message);
        requestJson.put("content", encodedContent);
        requestJson.put("sha", fileSHA);
        requestJson.put("branch", branchName);

        RequestBody requestBody = RequestBody.create(mediaType, requestJson.toString());
        Request request = new Request.Builder()
                .url(baseURL + "/contents/profiles/" + vendorKey + ".json")
                .put(requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Update File Response:" + response);
    }

    public void createPullRequest(String title, String message, String head) throws IOException {
        JSONObject bodyJson = new JSONObject();
        bodyJson.put("title", title);
        bodyJson.put("body", message);
        bodyJson.put("head", head);
        bodyJson.put("base", "master");
        bodyJson.put("maintainer_can_modify", true);

        RequestBody body = RequestBody.create(mediaType, bodyJson.toString());
        Request request = new Request.Builder()
                .url(baseURL + "/pulls")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Create Pull Request Response: " + response);
    }

    public String getMasterSHA() throws IOException{
        Request request = new Request.Builder()
                .url(baseURL + "/git/refs/heads/master")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        JSONObject responseJson = new JSONObject(response.body().string());
        JSONObject object = responseJson.getJSONObject("object");

        System.out.println("Master sha: " + object.get("sha").toString());
        return object.get("sha").toString();
    }

    public String getFileSHA(String fileName) throws IOException{
        Request request = new Request.Builder()
                .url(baseURL + "/contents/profiles/" + fileName + ".json")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        JSONObject responseBody = new JSONObject(response.body().string());

        System.out.println("File sha: " + responseBody.get("sha").toString());
        return responseBody.get("sha").toString();
    }

}
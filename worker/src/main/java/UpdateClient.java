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

        JSONObject requestJSON = new JSONObject();
        requestJSON.put("ref", "refs/heads/" + branchName);
        requestJSON.put("sha", masterSHA);

        RequestBody requestBody = RequestBody.create(mediaType, requestJSON.toString());
        Request request = new Request.Builder()
                .url(baseURL + "/git/refs")
                .post(requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Create Branch Response Code: " + response.code());
    }

    public void updateFile(String fileName, String content, String message, String branchName) throws IOException {
        final String fileSHA = getFileSHA(fileName);
        final String encodedContent = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));

        JSONObject requestJSON = new JSONObject();
        requestJSON.put("message", message);
        requestJSON.put("content", encodedContent);
        requestJSON.put("sha", fileSHA);
        requestJSON.put("branch", branchName);

        RequestBody requestBody = RequestBody.create(mediaType, requestJSON.toString());
        Request request = new Request.Builder()
                .url(baseURL + "/contents/profiles/" + fileName + ".json")
                .put(requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Update File Response Code:" + response.code());
    }

    public void createPullRequest(String title, String message, String head) throws IOException {
        JSONObject bodyJSON = new JSONObject();
        bodyJSON.put("title", title);
        bodyJSON.put("body", message);
        bodyJSON.put("head", head);
        bodyJSON.put("base", "master");
        bodyJSON.put("maintainer_can_modify", true);

        RequestBody body = RequestBody.create(mediaType, bodyJSON.toString());
        Request request = new Request.Builder()
                .url(baseURL + "/pulls")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Create Pull Request Response Code: " + response.code());
    }

    public String getMasterSHA() throws IOException{
        Request request = new Request.Builder()
                .url(baseURL + "/git/refs/heads/master")
                .get()
                .build();

        Response response = client.newCall(request).execute();
        JSONObject responseJSON = new JSONObject(response.body().string());
        JSONObject object = responseJSON.getJSONObject("object");

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
        System.out.println("File sha: " +responseBody.get("sha").toString());

        return responseBody.get("sha").toString();
    }

}
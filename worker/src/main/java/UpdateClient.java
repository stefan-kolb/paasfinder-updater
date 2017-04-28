import okhttp3.*;
import org.json.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class UpdateClient {

    private final OkHttpClient client;
    private final String baseURL;
    private final String credentials = Base64.getEncoder().encodeToString("vasilpet:mKsd9045s".getBytes(StandardCharsets.UTF_8));
    private final MediaType mediaType = MediaType.parse("application/json");

    public UpdateClient(String user, String repo) {
        this.client = new OkHttpClient();
        this.baseURL = "https://api.github.com/repos/" + user + "/" + repo;
    }

    public void createNewBranch(String branchName) throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + "/git/refs/heads/master")
                .get()
                .build();

        Response responseMaster = client.newCall(request).execute();
        JSONObject json = new JSONObject(responseMaster.body().string());
        JSONObject object = json.getJSONObject("object");
        String masterSHA = object.get("sha").toString();
        System.out.println(responseMaster);
        System.out.println("Master SHA: " + masterSHA);

        // 2 Create a branch
        JSONObject jsonForBranch = new JSONObject();
        jsonForBranch.put("ref", "refs/heads/" + branchName);
        jsonForBranch.put("sha", masterSHA);

        RequestBody body = RequestBody.create(mediaType, jsonForBranch.toString());
        Request branchRequest = new Request.Builder()
                .url(baseURL + "/git/refs")
                .post(body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response branchResponse = client.newCall(branchRequest).execute();
        System.out.println(branchResponse);
    }

    public void updateFile(String fileName, String content, String branchName) throws IOException {
        // 3 Get File SHA
        Request requestFile = new Request.Builder()
                .url(baseURL + "/contents/" + fileName)
                .get()
                .build();

        Response responseFile = client.newCall(requestFile).execute();

        JSONObject jsonFile = new JSONObject(responseFile.body().string());
        String fileSHA = jsonFile.get("sha").toString();

        System.out.println(responseFile);
        System.out.println("fileSHA: " + fileSHA);

        // 4
        JSONObject updatedFile = new JSONObject();
        updatedFile.put("message", "my message");
        String encodedContent = Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
        updatedFile.put("content", encodedContent);
        updatedFile.put("sha", fileSHA);
        updatedFile.put("branch", branchName);

        RequestBody putFile = RequestBody.create(mediaType, updatedFile.toString());
        Request requestPutFile = new Request.Builder()
                .url(baseURL + "/contents/" + fileName)
                .put(putFile)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response responsePutFile = client.newCall(requestPutFile).execute();
        System.out.println(responsePutFile);
    }

    public void createPullRequest(String title, String message, String head) throws IOException {
        JSONObject jsonPR = new JSONObject();
        jsonPR.put("title", title);
        jsonPR.put("body", message);
        jsonPR.put("head", head);
        jsonPR.put("base", "master");
        jsonPR.put("maintainer_can_modify", true);

        RequestBody bodyPR = RequestBody.create(mediaType, jsonPR.toString());
        Request request = new Request.Builder()
                .url(baseURL + "/pulls")
                .post(bodyPR)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response responsePR = client.newCall(request).execute();
        System.out.println(responsePR);
    }
}
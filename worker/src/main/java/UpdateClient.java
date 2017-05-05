import Messages.*;
import com.google.gson.*;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

class UpdateClient {

    public static JsonParser jsonParser = new JsonParser();
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final String user = System.getenv("UPDATEBOT_LOGIN");
    private final String password = System.getenv("UPDATEBOT_PASSWORD");
    private final String credentials = Base64.getEncoder().encodeToString((user + ":" + password).getBytes(StandardCharsets.UTF_8));
    private final String baseURL = "https://api.github.com/repos/" + user + "/paas-profiles";

    private final OkHttpClient client = new OkHttpClient();
    private final MediaType mediaType = MediaType.parse("application/json");

    void postBranch(Branch branch) throws IOException {
        RequestBody requestBody = RequestBody.create(mediaType, gson.toJson(branch));
        Request request = new Request.Builder()
                .url(baseURL + "/git/refs")
                .post(requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Create Branch " + response);
    }

    void putFile(File file) throws IOException {
        RequestBody requestBody = RequestBody.create(mediaType, gson.toJson(file));
        Request request = new Request.Builder()
                .url(baseURL + "/contents/profiles/" + file.getVendorKey() + ".json")
                .put(requestBody)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Basic " + credentials)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Update File " + response);
    }

    void postPullRequest(PullRequest pullRequest) throws IOException {
        RequestBody body = RequestBody.create(mediaType, gson.toJson(pullRequest));
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
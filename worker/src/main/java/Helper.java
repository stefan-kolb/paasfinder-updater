import com.google.gson.JsonObject;

public class Helper {

    static JsonObject buildBranchRequestJson(String branch, String masterSHA){
        JsonObject json = new JsonObject();
        json.addProperty("ref", "refs/heads/" + branch);
        json.addProperty("sha", masterSHA);
        return json;
    }
}

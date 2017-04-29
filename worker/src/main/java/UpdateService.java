import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.json.JSONObject;
import vendor.*;
import static spark.Spark.*;

public class UpdateService {

    public static class Model {
        private static List<Vendor> vendors = new ArrayList<>();

        public static Vendor store(Vendor vendor){
            vendors.add(vendor);
            return vendor;
        }

        public List<Vendor> getAllVendors(){
            return vendors;
        }
    }

    private static Gson GSON = new GsonBuilder().create();

    public static void main( String[] args) {
        port(9090);

        Model model = new Model();

        redirect.get("/", "/vendors");

        before((request,response)-> {
            response.header("Access-Control-Allow-Origin", "*");
        });

        get("/vendors", (request, response) -> {
            response.status(200);
            response.type("application/json; charset=utf-8");
            return GSON.toJson(model.getAllVendors());
        });

        post("/vendors", "application/json", (request, response) -> {
            JSONObject json = new JSONObject(request.body());

            final String fileName =  json.get("id").toString().toLowerCase();
            final String branchName = "updating-" + fileName;

            UpdateClient client = new UpdateClient();
            client.createBranch(branchName);
            client.updateFile(fileName, json.toString(), "update message", branchName);
            client.createPullRequest("title", "message", branchName);

            response.status(200);
            return "OK";
        });

        options("/*", (request,response)-> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if(accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });
    }
}
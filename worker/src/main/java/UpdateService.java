import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
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

        post("/add", "application/json", (request, response) -> {
            System.out.println("Received request from " + request.raw().getRemoteAddr());
            Vendor toStore = null;
            try {
                toStore = GSON.fromJson(request.body(), Vendor.class);
            } catch (JsonSyntaxException e) {
                response.status(400);
                return "INVALID JSON";
            }
            Model.store(toStore);
            return GSON.toJson(toStore);
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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

import spark.Spark;
import vendor.*;
import static spark.Spark.*;

public class UpdateService {

    private static final int HTTP_BAD_REQUEST = 400;
    private static final String CONTENT_TYPE = "application/json; charset=utf-8";

    public static class Model {
        private List<Vendor> updatedVendors = new ArrayList<>();

        public void storeVendor(Vendor data){
            updatedVendors.add(data);
        }

        public List<Vendor> getAllVendors(){
            return updatedVendors;
        }
    }

    public static String dataToJson(Object data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            StringWriter sw = new StringWriter();
            mapper.writeValue(sw, data);
            return sw.toString();
        } catch (IOException e){
            throw new RuntimeException("IOException from a StringWriter?");
        }
    }

    public static void main( String[] args) {
        port(9090);
        Model model = new Model();

        redirect.get("/", "/vendors");

        // get all vendors
        get("/vendors", (request, response) -> {
            response.status(200);
            response.type(CONTENT_TYPE);
            return dataToJson(model.getAllVendors());
        });

        // insert a vendor
        post("/test", "application/json", (request, response) -> {
            System.out.println("Received request from " + request.raw().getRemoteAddr());
            try {
                ObjectMapper mapper = new ObjectMapper();
                Vendor creation = mapper.readValue(request.body(), Vendor.class);
                if (!creation.isValid()) {
                    response.status(HTTP_BAD_REQUEST);
                    return "Vendor has incorrect structure!";
                }
                model.storeVendor(creation);
                response.status(200);
                response.type(CONTENT_TYPE);
                return "";
            } catch (JsonParseException jpe) {
                response.status(HTTP_BAD_REQUEST);
                return "";
            }
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

        before((request,response)-> {
            response.header("Access-Control-Allow-Origin", "*");
        });
    }
}
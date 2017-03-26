import com.sendgrid.Client;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import java.io.IOException;
/**
    For testing HTTP Client
 */
public class MyClient {
    public static void main(String[] args){

        // GET https://paasfinder.org/api/vendors/appfog

        Client client = new Client();
        Request request = new Request();
        request.setBaseUri("paasfinder.org");
        request.setMethod(Method.GET);
        String param = "appfog";
        request.setEndpoint("/api/vendors/" + param);

        try {
            Response response = client.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}

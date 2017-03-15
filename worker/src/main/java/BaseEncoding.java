import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class BaseEncoding {
    private static final String CHARSET = "utf-8";
    public static void main( String[] args) {
        try {
            // Encode using basic encoder
            String encodedString = Base64.getEncoder().encodeToString("Secret".getBytes(CHARSET));
            System.out.println("Encoded String: " + encodedString);

            // Decode
            byte[] decodedString = Base64.getDecoder().decode(encodedString);
            System.out.println("Decoded String: " + new String(decodedString, CHARSET));
        } catch(UnsupportedEncodingException e){
            System.out.println("Error :" + e.getMessage());
        }
    }

}

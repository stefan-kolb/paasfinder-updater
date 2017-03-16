import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BaseEncoding {
    public static void main( String[] args) {
        // Encode using basic encoder
        String encodedString = Base64.getEncoder().encodeToString("Secret".getBytes(StandardCharsets.UTF_8));
        System.out.println("Encoded String: " + encodedString);

        // Decode
        byte[] decodedString = Base64.getDecoder().decode(encodedString);
        System.out.println("Decoded String: " + new String(decodedString, StandardCharsets.UTF_8));
    }

}

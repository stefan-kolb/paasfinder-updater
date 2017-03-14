package vendor;

import lombok.Data;

@Data
public class Middleware {

    private String name;
    private String runtime;
    private String[] versions;

}
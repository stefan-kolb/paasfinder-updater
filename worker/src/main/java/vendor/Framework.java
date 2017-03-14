package vendor;

import lombok.Data;

@Data
public class Framework {

    private String name;
    private String runtime;
    // list
    private String[] versions;

}
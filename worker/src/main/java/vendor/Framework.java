package vendor;

import lombok.Data;

import java.util.List;

@Data
public class Framework {

    private String name;
    private String runtime;
    private List<String> versions;

}
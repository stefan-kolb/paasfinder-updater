package vendor;

import lombok.Data;

import java.util.List;

@Data
public class Runtime {

    private String language;
    private List<String> versions;

}

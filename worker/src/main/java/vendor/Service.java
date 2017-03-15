package vendor;

import lombok.Data;

import java.util.List;

@Data
public class Service {

    private List<Native> natives;
    private List<Addon> addons;

    @Data
    static class Native {
        private String name;
        private String description;
        private String type;
        private List<String> versions;
    }

    @Data
    static class Addon {
        private String name;
        private String url;
        private String description;
        private String type;
    }

}
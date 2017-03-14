package vendor;

import lombok.Data;

@Data
public class Service {

    private Native[] natives;
    private Addon[] addons;

    @Data
    static class Native {
        private String name;
        private String description;
        private String type;
        private String[] versions;
    }

    @Data
    static class Addon {
        private String name;
        private String url;
        private String description;
        private String type;
    }

}
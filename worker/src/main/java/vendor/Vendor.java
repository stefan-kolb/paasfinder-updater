package vendor;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Vendor implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String revision;
    private String vendor_verified;
    private String url;
    private String status;
    private String status_since;
    private String type;
    private String platform;
    private boolean extensible;

    private Hosting hosting;
    private List<Pricing> pricings;
    private Quality qos;
    private Scaling scaling;
    private List<Runtime> runtimes;
    private List<Middleware> middlewares;
    private List<Framework> frameworks;
    private Service service;
    private List<Infrastructure> infrastructures;

    private String contributorName;
    private String contributorEmail;
    private String contributorMessage;
}
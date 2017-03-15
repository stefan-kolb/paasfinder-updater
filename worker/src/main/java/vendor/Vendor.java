package vendor;

import lombok.Data;

import java.sql.Time;
import java.util.Date;
import java.util.List;

@Data
public class Vendor {

    private int id;
    private String name;
    private Time revision;
    private Date vendor_verified;
    private String url;
    private String status;
    private Date status_since;
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

    public boolean isValid(){
        return !name.isEmpty();
    }
}

package vendor;

import lombok.Data;

import java.sql.Time;
import java.util.Date;

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
    private Pricing[] pricings;
    private Quality qos;
    private Scaling scaling;
    private Runtime[] runtimes;
    private Middleware[] middlewares;
    private Framework[] frameworks;
    private Service service;
    private Infrastructure[] infrastructures;

    public boolean isValid(){
        return !name.isEmpty();
    }
}

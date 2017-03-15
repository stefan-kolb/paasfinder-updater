package vendor;

import lombok.Data;

import java.util.List;

@Data
public class Quality {

    private float uptime;
    private List<String> compliance;

}

package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import play.data.format.Formats.DateTime;
import play.data.validation.Constraints.Required;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class BeaconRendezvous extends Model {

    @Id @GeneratedValue
    public Long id;

    @DateTime(pattern="dd/MM/yyyy")
    @CreatedTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z", timezone="GMT")
    public Date creationDate;

    @Required
    public String beaconKey;

    public String remoteAddress;

    public static final Find<Long, BeaconRendezvous> FIND = new Find<Long, BeaconRendezvous>(){};

    protected BeaconRendezvous() {}

    public BeaconRendezvous(String beaconKey, String remoteAddress) {
        this.beaconKey = beaconKey;
        this.remoteAddress = remoteAddress;
        this.creationDate = new Date();
    }

}

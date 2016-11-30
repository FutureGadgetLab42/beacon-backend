package models;

import com.avaje.ebean.Model;
import com.avaje.ebean.annotation.CreatedTimestamp;
import play.data.format.Formats.DateTime;
import play.data.validation.Constraints.Required;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Beacon extends Model {

    @Id @GeneratedValue
    public Long id;

    @DateTime(pattern="dd/MM/yyyy")
    @CreatedTimestamp
    public Date creationDate;

    @Required
    public String beaconKey;

    public String userId, beaconName, description;

    // Generic query helper for Long IDs
    public static final Find<Long, Beacon> FIND = new Find<Long, Beacon>(){};

    protected Beacon() {}

    private Beacon(BeaconBuilder builder) {
        this.beaconKey = builder.beaconKey;
        this.creationDate = builder.creationDate;
        this.userId = builder.userId;
        this.beaconName = builder.beaconName;
        this.description = builder.description;
    }

    public static class BeaconBuilder {

        private String beaconKey, userId, beaconName, description;
        private Date creationDate;

        public BeaconBuilder(String beaconKey) {
            this.beaconKey = beaconKey;
            this.creationDate = new Date();
        }

        public Beacon build() {
            return new Beacon(this);
        }

        public String getBeaconKey() {
            return beaconKey;
        }

        public void setBeaconKey(String beaconKey) {
            this.beaconKey = beaconKey;
        }

        public Date getCreationDate() {
            return creationDate;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getBeaconName() {
            return beaconName;
        }

        public void setBeaconName(String beaconName) {
            this.beaconName = beaconName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

    }
}

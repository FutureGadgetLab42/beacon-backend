package models;

public class BeaconModel {
    private String beaconKey, userId, beaconName, description;

    public BeaconModel(String beaconKey, String userId, String beaconName, String description) {
        this.beaconKey = beaconKey;
        this.userId = userId;
        this.beaconName = beaconName;
        this.description = description;
    }

    public String getKey() {
        return beaconKey;
    }
}

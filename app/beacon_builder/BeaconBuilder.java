package beacon_builder;

import models.BeaconModel;

import java.math.BigInteger;
import java.security.SecureRandom;

public class BeaconBuilder {

    private final static SecureRandom SECURE_RNG = new SecureRandom();
    private final int KEY_BIT_LENGTH = 130;

    public BeaconModel buildBeacon( String userId, String beaconName, String description) {
        String beaconKey = new BigInteger(KEY_BIT_LENGTH, SECURE_RNG).toString(32);
        return new BeaconModel(beaconKey, userId, beaconName, description);
    }
}

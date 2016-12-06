package beacon_factory;

import com.fasterxml.jackson.databind.JsonNode;
import exceptions.BeaconCreationExcecption;
import models.Beacon;
import models.Beacon.BeaconBuilder;
import play.Logger;

import java.security.SecureRandom;

/**
 * Utilizes a combination of Factory and Builder design patterns to securely initialize
 * Beacons to abstract away the initialization of models.
 * */
public class BeaconFactory {

    private final static SecureRandom SECURE_RNG = new SecureRandom();
    private final static int KEY_LENGTH = 32;
    private final static char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    /**
     * Factory method that builds a Beacon from a requests body JSON.
     *
     * @param requestJson
     *              The JSON body of the request. Should detail Beacon properties.
     *
     * @return
     *              The result of the buildBeacon private method
     *
     * @throws BeaconCreationExcecption
     *              If the request body does not contain the required data in the required format.
     * */
    public Beacon buildBeacon(JsonNode requestJson) throws BeaconCreationExcecption {
        String beaconName = requestJson.findPath("beaconName").asText(),
                description = requestJson.findPath("description").asText();

        if(beaconName == null) {
            Logger.warn("Bad request - does not contain name: " + requestJson);
            throw new BeaconCreationExcecption("Bad request - does not contain name: " + requestJson);
        } else if(description == null) {
            Logger.warn("Bad request - does not contain description: " + requestJson);
            throw new BeaconCreationExcecption("Bad request - does not contain description: " + requestJson);
        }  else {
            return buildBeacon(beaconName, description);
        }
    }

    /**
     * Provides a beacon with the desired attributes.
     *
     * @param beaconName
     *              The name of the Beacon.
     *
     * @param description
     *              Additional or miscellaneous information.
     *
     * @return A Beacon with the given fields
     * */
    private Beacon buildBeacon(String beaconName, String description) {
        BeaconBuilder beaconBuilder = new BeaconBuilder(generateBeaconKey());
        beaconBuilder.setBeaconName(beaconName);
        beaconBuilder.setDescription(description);
        return beaconBuilder.build();
    }


    /**
     * Generates a random sequence of 32 characters from ALPHABET.
     *
     * @return
     *          The unique sequence of 32 characters.
     * */
    private String generateBeaconKey() {
        StringBuilder keyBuilder = new StringBuilder();

        for(int i = 0; i < KEY_LENGTH; i++) {
            keyBuilder.append(ALPHABET[SECURE_RNG.nextInt(ALPHABET.length)]);
        }

        return keyBuilder.toString();
    }
}

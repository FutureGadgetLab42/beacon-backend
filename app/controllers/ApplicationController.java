package controllers;

import beacon_factory.BeaconFactory;
import com.fasterxml.jackson.databind.JsonNode;
import data_sources.DatabaseAccessor;
import exceptions.BeaconCreationExcecption;
import exceptions.BeaconSearchException;
import exceptions.ConfigurationException;
import models.Beacon;
import models.BeaconRendezvous;
import play.Logger;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ApplicationController extends Controller {

    @Inject private static final DatabaseAccessor DATABASE_ACCESSOR = new DatabaseAccessor();
    private static final BeaconFactory BEACON_FACTORY = new BeaconFactory();
    private Result HOMEPAGE = ok("homepage");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private static final String PAYLOAD_PATH = "beacon.png"; //"transparent_pixel.png";

    public Result index() {
        return HOMEPAGE;
    }

    /**
     * Controller method for requesting a new Beacon. This method must be routed to
     * via a POST request with a JSON body containing the desired Beacon attributes.
     *
     * @return response
     *              The HTTP response containing either the serialized Beacon, or an error message
     * */
    @BodyParser.Of(BodyParser.Json.class)
    @Transactional
    public Result requestNewBeacon() {
        JsonNode requestJson = request().body().asJson();
        Result response;

        if(requestJson == null) {
            Logger.error("Invalid request received " + request().body().asText());
            response = badRequest("Invalid request format: " + request().body());
        } else {
            try {
                Beacon resultBeacon = BEACON_FACTORY.buildBeacon(requestJson);
                DATABASE_ACCESSOR.saveBeacon(resultBeacon);
                response = ok(Json.toJson(resultBeacon));
            } catch (BeaconCreationExcecption e) {
                response = badRequest("Invalid request: " + requestJson);
            }
        }

        return response;
    }

    /**
     * Controller method to view all Beacons currently available from the data source
     *
     * @return response
     *              The HTTP response containing either a serialized list of beacons, or an
     *              error message if an exception is thrown.
     * */
    @Transactional
    public Result viewBeacons() {
        Logger.info("Received request to view beacons");
        Optional<List<Beacon>> allBeacons = DATABASE_ACCESSOR.listAllBeacons();
        Result response;
        if(allBeacons.isPresent()) {
            List<Beacon> beacons = allBeacons.get();
            response = ok(Json.toJson(beacons));
        } else {
            response = ok("No Beacons have been initialized");
        }
        return response;
    }

    /**
     * Retrieves the Beacon corresponding to the specified key
     *
     * @return response
     *          An HTTP response containing either the desired Beacon, or an error
     *          response if the given key is not present.
     * */
    @Transactional
    public Result findBeaconByKey(String key) {
        Result response;
        Logger.info("Received request to find Beacon with key: " + key);
        Optional<Beacon> beaconOptional = DATABASE_ACCESSOR.findByKey(key);

        if(beaconOptional.isPresent()) {
            response = ok(Json.toJson(beaconOptional.get()));
        } else {
            response = badRequest("Unable to locate beacon with key: " + key);
        }
        return response;
    }

    /**
     * Retries all Beacons corresponding to the specified user, sorted by Date in ascending order.
     * */
    @Transactional
    public Result findBeaconByUser(String userId) {
        Result response;
        Logger.info("Received request to find Beacons for user: " + userId);
        Optional<List<Beacon>> beaconListOptional = DATABASE_ACCESSOR.findBeaconsForUser(userId);

        if(beaconListOptional.isPresent()) {
            List<Beacon> beaconList = beaconListOptional.get();
            beaconList.sort((a, b) -> a.creationDate.compareTo(b.creationDate));
            response = ok(Json.toJson(beaconList));
        } else {
            response = badRequest("Unable to locate Beacons for user: " + userId);
        }
        return response;
    }

    /**
     * Records an instance of a Beacon "phoning home"
     *
     * @param beaconKey
     *          The unique 32 character key associated with the desired Beacon.
     *
     * @return response
     *          An HTTP response containing
     * */
    @Transactional
    public Result recordBeaconRendezvous(String beaconKey) throws URISyntaxException {
        if(beaconKey == null || beaconKey.isEmpty()) {
            return badRequest("Bad Beacon access request: " + request());
        }

        Result response;
        String remoteAddress = request().remoteAddress();

        try {
            BeaconRendezvous rendezvous = new BeaconRendezvous(beaconKey, remoteAddress);
            DATABASE_ACCESSOR.recordBeaconRendezvous(rendezvous);
            response = ok(payload());
        } catch(BeaconSearchException bse) {
            Logger.warn("Invalid request to record Beacon access: " + beaconKey);
            response = badRequest("Invalid request to record Beacon access: " + beaconKey);
        } catch(URISyntaxException e) {
            Logger.error("Unable to locate payload due to invalid URI: " + PAYLOAD_PATH);
            response = internalServerError();
        }
        return response;
    }

    /**
     * Finds all Rendezvous for the given Beacon key
     *
     * @param beaconKey
     *          The key corresponding to the desired Beacon
     *
     * @return response
     *          A response containing either serialized BeaconRendezvous objects,
     *          or an error message indicating that no matching Rendezvous were found.
     * */
    public Result findAllRendezvous(String beaconKey) {
        Result response;
        Optional<List<BeaconRendezvous>> rendezvousListOptional = DATABASE_ACCESSOR.findRendezvousByKey(beaconKey);

        if(rendezvousListOptional.isPresent()) {
            response = ok(Json.toJson(rendezvousListOptional.get()));
        } else {
            Logger.info("Could not find Rendezvous for Beacon" + beaconKey);
            response = badRequest("No rendezvous present for key: " + beaconKey);
        }

        return response;
    }

    private File payload() throws URISyntaxException {
        URL payloadUrl = getClass().getClassLoader().getResource(PAYLOAD_PATH);
        if(payloadUrl == null) {
            throw new ConfigurationException("Unable to locate payload: " + PAYLOAD_PATH);
        } else {
            return new File(payloadUrl.toURI());
        }
    }

}

package controllers;

import beacon_factory.BeaconFactory;
import com.fasterxml.jackson.databind.JsonNode;
import data_sources.DatabaseAccessor;
import exceptions.BeaconCreationExcecption;
import models.Beacon;
import play.Logger;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class ApplicationController extends Controller {

    @Inject private static final DatabaseAccessor DATABASE_ACCESSOR = new DatabaseAccessor();
    private static final BeaconFactory BEACON_FACTORY = new BeaconFactory();
    private Result HOMEPAGE = ok("homepage");

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
                response = ok(Json.toJson(BEACON_FACTORY.buildBeacon(requestJson)));
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
    public Result viewBeacons() {
        Logger.info("Received request to view beacons");
        Optional<List<Beacon>> result = DATABASE_ACCESSOR.listAll();
        Result response;
        if(result.isPresent()) {
            List<Beacon> beacons = result.get();
            response = ok(Json.toJson(beacons));
        } else {
            response = ok("No Beacons have been initialized");
        }
        return response;
    }

}

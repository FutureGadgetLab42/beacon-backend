package controllers;

import beacon_builder.BeaconBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import data_accessors.DatabaseAccessor;
import models.BeaconModel;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Optional;

public class ApplicationController extends Controller {

    @Inject
    private static DatabaseAccessor DATABASE_ACCESSOR;
    private static BeaconBuilder BEACON_BUILDER = new BeaconBuilder();
    private Result HOMEPAGE = ok("homepage");

    public Result index() {
        return HOMEPAGE;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public Result requestNewBeacon() {
        JsonNode requestJson = request().body().asJson();
        if(requestJson == null) {
            return badRequest("Invalid request format: " + request().body());
        }

        return validateRequest(requestJson);
    }

    private Result validateRequest(JsonNode requestJson) {
        Result result = ok("beacon requested");
        String beaconName = requestJson.findPath("beaconName").asText(),
                userId = requestJson.findPath("userId").asText(), description = requestJson.findPath("description").asText();
        if(beaconName == null) {
            result = badRequest("Bad request - does not contain name: " + requestJson);
        } else if(description == null) {
            result = badRequest("Bad request - does not contain description: " + requestJson);
        } else if(userId == null) {
            result = badRequest("Bad request - does not contain user ID: " + requestJson);
        } else {
            BeaconModel resultingBeacon = BEACON_BUILDER.buildBeacon(beaconName, userId, description);
            return ok(resultingBeacon.getKey());
        }
        return ok("beacon requested");
    }

    private Optional<String> extractName(JsonNode json) {
        String nameNode = json.findPath("name").textValue();
        return nameNode == null ? Optional.empty() : Optional.of(nameNode);
    }

    public Result viewBeacons() {
        return ok("viewing beacons");
    }
}

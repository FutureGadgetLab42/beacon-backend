package data_sources;

import models.Beacon;
import play.Logger;
import play.db.ebean.Transactional;

import javax.persistence.NonUniqueResultException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class DatabaseAccessor {

    /**
     * Returns an Optional List containing all beacons in the table
     * */
    @Transactional
    public Optional<List<Beacon>> listAll() {
        List<Beacon> result = Beacon.FIND.all();
        return result.isEmpty()? Optional.empty() : Optional.of(result);
    }

    /**
     * Returns an Optional Beacon containing the unique Beacon associated with the given ID
     *
     * @param id
     *          The unique database ID of the Beacon
     * */
    @Transactional
    public Optional<Beacon> findById(Long id) {
        Beacon result = Beacon.FIND.byId(id);
        if(result == null) {
            Logger.debug("No beacon found with ID: " + id);
            return Optional.empty();
        } else {
            return Optional.of(result);
        }
    }

    /**
     * Returns an Optional Beacon containing the unique Beacon associated with the given key
     *
     * @param key
     *          The unique random key associated with the Beacon
     * */
    @Transactional
    public Optional<Beacon> findByKey(String key) {
        try {
            return Optional.of(Beacon.FIND.where()
                    .like("beaconKey", "%" + key + "%")
                    .findUnique());
        } catch(NonUniqueResultException nonUniqueResultException) {
            Logger.warn("Attempted to find non-existent beacon with key: " + key);
            return Optional.empty();
        }
    }

    /**
     * Finds all beacons for the given user.
     *
     * @param userName
     *          The username for whom to display Beacons
     * */
    @Transactional
    public Optional<List<Beacon>> findBeaconsForUser(String userName) {
        List<Beacon> result = Beacon.FIND.where()
                .like("userName", "%" + userName + "%")
                .findList();
        if(result.isEmpty()) {
            Logger.debug("Unable to find Beacons for user: " + userName);
            return Optional.empty();
        } else {
            return Optional.of(result);
        }
    }

    /**
     * Finds all beacons created on the given date
     *
     * @param date
     *          The desired creation date for Beacons to return
     * */
    @Transactional
    public  Optional<List<Beacon>> findBeaconsByCreationDate(Date date) {
        List<Beacon> result = Beacon.FIND.where()
                .between("creationDate", date, date)
                .findList();
        if(result.isEmpty()) {
            Logger.debug("No beacons on requested date: " + date);
            return Optional.empty();
        } else {
            return Optional.of(result);
        }
    }

    /**
     * Inserts the given Beacon to the database
     *
     * @param beacon
     *          The Beacon to be inserted
     * */
    public void saveBeacon(Beacon beacon) {
        beacon.save();
        Logger.info("Successfully saved Beacon with id: " + beacon.id);
    }
}

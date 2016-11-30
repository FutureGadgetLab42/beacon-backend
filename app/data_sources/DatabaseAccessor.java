package data_sources;

import models.BeaconRendezvous;
import exceptions.BeaconSearchException;
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
    public Optional<List<Beacon>> listAllBeacons() {
        List<Beacon> result = Beacon.FIND.all();
        return result == null? Optional.empty() : Optional.of(result);
    }

    /**
     * Returns an Optional Beacon containing the unique Beacon associated with the given ID
     *
     * @param id
     *          The unique database ID of the Beacon
     * */
    @Transactional
    public Optional<Beacon> findBeaconById(Long id) {
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
        Optional<Beacon> result;
        try {
            Beacon beacon = Beacon.FIND.where()
                    .like("beaconKey", "%" + key + "%")
                    .findUnique();
            if(beacon == null) result = Optional.empty(); else result = Optional.of(beacon);
        } catch(NonUniqueResultException nonUniqueResultException) {
            Logger.warn("Non-unique result found for key: " + key);
            result = Optional.empty();
        }
        return result;
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
    public  Optional<List<Beacon>> findBeaconsByDate(Date date) {
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

    @Transactional
    private boolean containsUniqueKey(String key) {
        return findByKey(key).isPresent();
    }

    /**
     * Inserts the given Beacon to the database
     *
     * @param beacon
     *          The Beacon to be inserted
     * */
    @Transactional
    public void saveBeacon(Beacon beacon) {
        beacon.save();
        Logger.info("Successfully saved Beacon with id: " + beacon.id);
    }

    /**
     * Records an access to the Beacon with the specified key.
     *
     * @param beaconRendezvous
     *          The BeaconRendezvous object that wraps the date of the access and
     *          the remote address of the requester.
     *
     * @throws BeaconSearchException
     *          If the specified key is not present in the database
     * */
    @Transactional
    public void recordBeaconRendezvous(BeaconRendezvous beaconRendezvous) throws BeaconSearchException {
        if(containsUniqueKey(beaconRendezvous.beaconKey)) {
            saveRendezvous(beaconRendezvous);
        } else {
            throw new BeaconSearchException("Cannot record Beacon Rendezvous. Key not found: " + beaconRendezvous.beaconKey);
        }
    }

    /**
     * Finds BeaconRendezvous with the given ID.
     *
     * @param id
     *              The ID of the desired BeaconRendezvous
     *
     * @return rendezvousOptional
     *              An Optional that wraps the Rendezvous with given ID, if it exists.
     *              Otherwise, it is the Empty Optional.
     * */
    @Transactional
    public Optional<BeaconRendezvous> findRendezvousById(long id) {
        Optional<BeaconRendezvous> rendezvousOptional;
        BeaconRendezvous rendezvous = BeaconRendezvous.FIND.byId(id);

        if(rendezvous == null) {
            rendezvousOptional = Optional.empty();
        } else {
            rendezvousOptional = Optional.of(rendezvous);
        }

        return rendezvousOptional;
    }

    /**
     * Finds all BeaconRendezvous associated with the given key
     *
     * @param beaconKey
     *              The unique 32 character key associated with the desired Beacon.
     * */
    @Transactional
    public Optional<List<BeaconRendezvous>> findRendezvousByKey(String beaconKey) {

        Optional<List<BeaconRendezvous>> rendezvousOptional;
        List<BeaconRendezvous> rendezvousList = BeaconRendezvous.FIND.where()
                .like("beaconKey", "%" + beaconKey + "%")
                .findList();

        if(rendezvousList == null) {
            rendezvousOptional = Optional.empty();
        } else {
            rendezvousOptional = Optional.of(rendezvousList);
        }

        return rendezvousOptional;
    }

    /**
     * Finds all BeaconRendezvous with the given creation date
     *
     * @param rendezvousDate
     *              The date for which to see all Rendezvous that occurred
     *
     * @return rendezvousOptional
     *              An optional that contains a List of BeaconRendezvous for the given date.
     *              The empty Optional if no such BeaconRendezvous exist.
     * */
    @Transactional
    public Optional<List<BeaconRendezvous>> findRendezvousByDate(Date rendezvousDate) {
        List<BeaconRendezvous> rendezvousList = BeaconRendezvous.FIND
                .where()
                .between("creationDate", rendezvousDate, rendezvousDate)
                .findList();

        Optional<List<BeaconRendezvous>> rendezvousOptional;
        if(rendezvousList == null) {
            Logger.info("Attempted to find Rendezvous on date which none took place: " + rendezvousDate);
            rendezvousOptional = Optional.empty();
        } else {
            rendezvousOptional = Optional.of(rendezvousList);
        }
        return rendezvousOptional;
    }

    /**
     * Inserts a BeaconRendezvous in to the database
     *
     * @param rendezvous
     *              The BeaconRendezvous to be inserted
     *
     * */
    @Transactional
    public void saveRendezvous(BeaconRendezvous rendezvous) {
        rendezvous.save();
        Logger.info("Saved rendezvous with " + rendezvous.beaconKey);
    }
}

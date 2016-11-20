package data_accessors;

import play.db.Database;

import javax.inject.Inject;

public class DatabaseAccessor {

    private Database database;

    @Inject
    public DatabaseAccessor(Database db) {
        this.database = db;
    }

}

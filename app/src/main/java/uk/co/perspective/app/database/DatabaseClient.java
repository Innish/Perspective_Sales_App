package uk.co.perspective.app.database;

import android.content.Context;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseClient {

    private static DatabaseClient mInstance;
    private final AppDatabase appDatabase;

    private DatabaseClient(Context mContext) {
        appDatabase = Room.databaseBuilder(mContext, AppDatabase.class, "MyBirdyCRM").allowMainThreadQueries().fallbackToDestructiveMigration().addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_10, MIGRATION_9_10, MIGRATION_12_13).build();
    }

    //For future migrations

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Contact ADD COLUMN localCustomerID INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Address ADD COLUMN localCustomerID INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Activity ADD COLUMN localCustomerID INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Lead ADD COLUMN localCustomerID INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Opportunity ADD COLUMN localCustomerID INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Quote ADD COLUMN localCustomerID INTEGER DEFAULT 0");
            database.execSQL("ALTER TABLE Task ADD COLUMN localCustomerID INTEGER DEFAULT 0");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'Project' ('id' INTEGER NOT NULL, 'projectID' INTEGER, 'localCustomerID' INTEGER, 'customerID' INTEGER, 'customerName' TEXT,"
                    + " 'projectName' TEXT, 'status' TEXT, 'startDate' TEXT, 'endDate' TEXT, 'reference' TEXT, 'details' TEXT, 'notes' TEXT, 'updated' TEXT, 'isArchived' INTEGER NOT NULL, 'isChanged' INTEGER NOT NULL, 'isNew' INTEGER NOT NULL, PRIMARY KEY(id))");

            database.execSQL("ALTER TABLE Customer ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Contact ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Address ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Lead ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Opportunity ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE Quote ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Lead ADD COLUMN telephone TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Lead ADD COLUMN email TEXT DEFAULT ''");
        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Opportunity ADD COLUMN telephone TEXT DEFAULT ''");
            database.execSQL("ALTER TABLE Opportunity ADD COLUMN email TEXT DEFAULT ''");
        }
    };

    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'OpportunityContact' ('id' INTEGER NOT NULL, 'localOpportunityID' INTEGER, 'localContactID' INTEGER, PRIMARY KEY(id))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'OpportunityQuote' ('id' INTEGER NOT NULL, 'localOpportunityID' INTEGER, 'localQuoteID' INTEGER, PRIMARY KEY(id))");
        }
    };

    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'Product' ('id' INTEGER NOT NULL, 'productID' INTEGER, 'partNumber' TEXT, 'description' TEXT , 'costPrice' FLOAT NOT NULL DEFAULT 0.00, 'salePrice' FLOAT NOT NULL DEFAULT 0.00, 'taxRate' FLOAT NOT NULL DEFAULT 0.00, PRIMARY KEY(id))");
            database.execSQL("ALTER TABLE QuoteLine ADD COLUMN costPrice FLOAT NOT NULL DEFAULT 0.00");
        }
    };

    static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE QuoteLine ADD COLUMN localQuoteID INTEGER");
        }
    };

    static final Migration MIGRATION_8_10 = new Migration(8, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'ProjectFile' ('id' INTEGER NOT NULL, 'localProjectID' INTEGER, 'filename' TEXT, 'filepath' TEXT , 'isNew' INTEGER NOT NULL, PRIMARY KEY(id))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'ProjectTask' ('id' INTEGER NOT NULL, 'localProjectID' INTEGER, 'localTaskID' INTEGER, 'localTaskPhaseID' INTEGER, PRIMARY KEY(id))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'ProjectPhase' ('id' INTEGER NOT NULL, 'localProjectID' INTEGER, 'phaseName' TEXT, 'description' TEXT , 'startDate' TEXT, 'endDate' TEXT, 'isArchived' INTEGER NOT NULL, 'isChanged' INTEGER NOT NULL, 'isNew' INTEGER NOT NULL, PRIMARY KEY(id))");
        }
    };

    static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'ProjectFile' ('id' INTEGER NOT NULL, 'localProjectID' INTEGER, 'filename' TEXT, 'filepath' TEXT , 'isNew' INTEGER NOT NULL, PRIMARY KEY(id))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'ProjectTask' ('id' INTEGER NOT NULL, 'localProjectID' INTEGER, 'localTaskID' INTEGER, 'localTaskPhaseID' INTEGER, PRIMARY KEY(id))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'ProjectPhase' ('id' INTEGER NOT NULL, 'localProjectID' INTEGER, 'phaseName' TEXT, 'description' TEXT , 'startDate' TEXT, 'endDate' TEXT, 'isArchived' INTEGER NOT NULL, 'isChanged' INTEGER NOT NULL, 'isNew' INTEGER NOT NULL, PRIMARY KEY(id))");
        }
    };

    static final Migration MIGRATION_10_12 = new Migration(10, 12) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Task ADD COLUMN createdByDisplayName TEXT");
        }
    };

    static final Migration MIGRATION_11_12 = new Migration(11, 12) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Task ADD COLUMN createdByDisplayName TEXT DEFAULT ''");
        }
    };

    static final Migration MIGRATION_12_13 = new Migration(12, 13) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS 'Order' ('id' INTEGER NOT NULL, 'orderID' INTEGER, 'localCustomerID' INTEGER, 'customerID' INTEGER, 'customerName' TEXT, 'contactID' INTEGER, 'opportunityID' INTEGER, 'contactName' TEXT, 'reference' TEXT, 'subject' TEXT, 'status' TEXT, 'currency' TEXT, 'exchangeRate' FLOAT, 'closingDate' TEXT, 'notes' TEXT, 'updated' TEXT, 'createdByDisplayName' TEXT, 'isArchived' INTEGER NOT NULL, 'isChanged' INTEGER NOT NULL, 'isNew' INTEGER NOT NULL, PRIMARY KEY(id))");
            database.execSQL("CREATE TABLE IF NOT EXISTS 'OrderLine' ('id' INTEGER NOT NULL, 'orderLineID' INTEGER, 'orderID' INTEGER, 'localOrderID' INTEGER, 'partNumber' TEXT, 'description' TEXT, 'quantity' FLOAT, 'value' FLOAT, 'costPrice' FLOAT, 'discount' FLOAT, 'taxRate' FLOAT, 'notes' TEXT, 'updated' TEXT, 'createdByDisplayName' TEXT, 'isChanged' INTEGER NOT NULL, 'isNew' INTEGER NOT NULL, PRIMARY KEY(id))");
        }
    };

    public static synchronized DatabaseClient getInstance(Context mContext) {

        if (mInstance == null) {
            mInstance = new DatabaseClient(mContext);
        }

        return mInstance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}

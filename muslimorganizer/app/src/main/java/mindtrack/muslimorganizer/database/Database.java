package mindtrack.muslimorganizer.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mindtrack.muslimorganizer.model.City;
import mindtrack.muslimorganizer.model.Country;
import mindtrack.muslimorganizer.model.LocationInfo;
import mindtrack.muslimorganizer.model.Zeker;
import mindtrack.muslimorganizer.model.ZekerType;

/**
 * Class to handel all database transactions
 */
public class Database {
    private final String MAIN_DATABSE = "/data/data/com.fekracomputers.muslimmate/"+"muslim_organizer.sqlite.png";

    /**
     * Function to open connection with Database
     *
     * @return Database object
     */
    public SQLiteDatabase openDB() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(MAIN_DATABSE, null, 0);
        return db;
    }

    /**
     * Function to close database connection
     *
     * @param db Database object to close
     */
    public void closeDB(SQLiteDatabase db) {
        db.close();
    }

    /**
     * Function to get location information
     *
     * @param latitude  Latitude degree
     * @param longitude Longitude degree
     * @return Object contains all location info
     */
    public LocationInfo getLocationInfo(float latitude, float longitude) {
        SQLiteDatabase db = openDB();

        String sql = "select  b.En_Name , b.Ar_Name , b.iso3 , a.city ," +
                " b.Continent_Code ,  b.number  , b.mazhab , b.way , b.dls , a.time_zone ," +
                " (latitude - " + latitude + ")*(latitude - " + latitude + ")+(longitude - " + longitude + ")" +
                "*(longitude - " + longitude + ") as ed , a.Ar_Name  from cityd a , countries b where" +
                " b.code = a.country order by ed asc limit 1;";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();

        LocationInfo locationInfo = new LocationInfo(latitude, longitude,
                cursor.getString(0), cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4), cursor.getInt(5),
                cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), cursor.getInt(9),
                cursor.getString(11) == null ? cursor.getString(3) : cursor.getString(11));

        closeDB(db);
        return locationInfo;
    }


    /**
     * Function to get all zekr
     *
     * @return List of zeker and some information
     */
    public List<ZekerType> getAllAzkarTypes() {
        List<ZekerType> zekerTypeList = new ArrayList<>();
        SQLiteDatabase db = openDB();
        String sql = "select a.ZekrTypeID , a.ZekrTypeName , count( b.TypeID ) from azkartypes a ," +
                "  azkar b where a.ZekrTypeID = b.TypeID group by a.ZekrTypeName order by  a.ZekrTypeID;";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            zekerTypeList.add(new ZekerType(cursor.getInt(0), cursor.getString(1), cursor.getInt(2)));
            cursor.moveToNext();
        }
        closeDB(db);
        return zekerTypeList;
    }


    /**
     * Function get azkar of certain type
     *
     * @param type zeker type
     * @return List of azkar
     */
    public List<Zeker> getAllAzkarOfType(int type) {
        List<Zeker> zekerList = new ArrayList<>();
        SQLiteDatabase db = openDB();
        String sql = "select ZekrContent , ZekrNoOfRep , Fadl from azkar where TypeID = " + type + "  ;";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            zekerList.add(new Zeker(cursor.getString(0),
                    cursor.getString(2), cursor.getInt(1)));
            cursor.moveToNext();
        }
        closeDB(db);
        return zekerList;
    }


    /**
     * Function to get all countries
     *
     * @return List of all countries
     */
    public List<Country> getAllCountries() {
        List<Country> countries = new ArrayList<>();
        SQLiteDatabase db = openDB();
        String sql = "select Code , EN_Name , AR_Name  from Countries ;";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            countries.add(new Country(cursor.getString(1) , cursor.getString(2) , cursor.getString(0)));
            cursor.moveToNext();
        }
        closeDB(db);
        return countries;
    }


    /**
     * Function to get all cities of country
     *
     * @param code Country code
     * @return List of all cities
     */
    public List<City> getAllCities(String code) {
        List<City> cities = new ArrayList<>();
        SQLiteDatabase db = openDB();
        String sql = "select * from cityd where country = '" + code + "' ;";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            cities.add(new City(cursor.getString(1),cursor.getString(6) , cursor.getFloat(2), cursor.getFloat(3)));
            cursor.moveToNext();
        }
        closeDB(db);

        return cities;
    }

    /**
     * Function to check if country is islamic or not
     *
     * @return Flag islamic or not
     */
    public Boolean isIslamicCountry(String code) {
        try {
            SQLiteDatabase db = openDB();
            String sql = "select islamic from countries where Code = '" + code + "'";
            Cursor cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            int type = 0;
            while (!cursor.isAfterLast()) {
                type = cursor.getInt(0);
                cursor.moveToNext();
            }
            cursor.close();
            closeDB(db);
            return type == 1 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

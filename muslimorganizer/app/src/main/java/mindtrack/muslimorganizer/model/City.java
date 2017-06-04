package mindtrack.muslimorganizer.model;

/**
 * Model class for city
 */
public class City {
    public String Name , arabicName ;
    public float Lat , lon;

    public City(String name, String arabicName, float lat, float lon) {
        this.Name = name;
        this.arabicName = arabicName ;
        this.Lat = lat;
        this.lon = lon;
    }

}

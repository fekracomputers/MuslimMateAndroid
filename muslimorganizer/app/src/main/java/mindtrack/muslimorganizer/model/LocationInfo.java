package mindtrack.muslimorganizer.model;

/**
 * Model class for location info
 */
public class LocationInfo {
    public float longitude , latitude , timeZone ;
    public int number , mazhab , way , dls ;
    public String name , name_english , iso , city , city_ar , continentCode ;

    public LocationInfo(float latitude , float longitude , String name ,
                        String name_english , String iso , String city ,
                        String continentCode , int number , int mazhab ,
                        int way , int dls , float timeZone , String city_ar)
    {
        this.longitude = longitude ;
        this.latitude  = latitude ;
        this.name = name ;
        this.name_english = name_english ;
        this.iso = iso ;
        this.city = city ;
        this.continentCode = continentCode ;
        this.number = number ;
        this.mazhab = mazhab ;
        this.way = way ;
        this.dls = dls ;
        this.timeZone = timeZone ;
        this.city_ar = city_ar ;
    }

}

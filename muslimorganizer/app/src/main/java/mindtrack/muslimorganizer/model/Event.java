package mindtrack.muslimorganizer.model;

/**
 * Model class for Event
 */
public class Event {
    public String eventName , hejriDate ;
    public int icon ;

    public Event(String eventName , String hejriDate)
    {
        this.eventName = eventName ;
        this.hejriDate = hejriDate ;
    }

    public Event(String eventName , String hejriDate , int icon)
    {
        this.eventName = eventName ;
        this.hejriDate = hejriDate ;
        this.icon = icon ;
    }

}

package mindtrack.muslimorganizer.model;

/**
 * Model class for zeker type
 */
public class ZekerType {
    public int zekrID , zekrCounter ;
    public String zekrTitle ;
    public boolean animated;

    public boolean isAnimated() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public ZekerType(int zekrID , String zekrTitle , int zekrCounter)
    {
        this.zekrID = zekrID ;
        this.zekrTitle = zekrTitle ;
        this.zekrCounter = zekrCounter ;
    }

}

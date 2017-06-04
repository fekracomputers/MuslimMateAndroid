package mindtrack.muslimorganizer.utility;

import com.tndgl.ogzjx284034.AdConfig;
import com.tndgl.ogzjx284034.AdView;

/**
 * Class to show adds in application
 */
public class Adds {

    /**
     * Constructor to validate add
     */
    public Adds() {
        // ads
        AdConfig.setAppId(302689);  //setting appid.
        AdConfig.setApiKey("1458551013284034680"); //setting apikey
        AdConfig.setCachingEnabled(true); //Enabling SmartWall ad caching.
        AdConfig.setPlacementId(0); //pass the placement id.
    }

    /**
     * Add adds to view
     *
     * @param adView View to add
     */
    public void addAdds(AdView adView) {
        adView.setBannerType(AdView.PLACEMENT_TYPE_INLINE);
        adView.setBannerAnimation(AdView.ANIMATION_TYPE_FADE);
        adView.showMRinInApp(false);
        adView.loadAd();
    }


}

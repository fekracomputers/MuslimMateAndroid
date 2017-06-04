package mindtrack.muslimorganizer.model;


import android.support.v4.app.Fragment;

import mindtrack.muslimorganizer.service.DetectLocationListener;

/**
 * Created by TuiyTuy on 12/14/2016.
 */

public class FragsInfo {

    private String name;
    private Fragment frag;





    public FragsInfo(String name, Fragment frag) {
        this.name = name;
        this.frag = frag;
    }

    public FragsInfo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Fragment getFrag() {
        return frag;
    }

    public void setFrag(Fragment frag) {
        this.frag = frag;
    }
}

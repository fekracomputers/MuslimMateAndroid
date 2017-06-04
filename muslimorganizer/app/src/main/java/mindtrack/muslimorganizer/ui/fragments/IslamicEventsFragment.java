package mindtrack.muslimorganizer.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.adapter.IslamicEventAdapter;
import mindtrack.muslimorganizer.calculator.calendar.HGDate;
import mindtrack.muslimorganizer.model.Event;

/**
 * Fragment of the islamic events
 */
public class IslamicEventsFragment extends Fragment {
    private RecyclerView eventRecyclerView;
    private IslamicEventAdapter adapter;
    private List<Event> eventList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_islamic_events, container, false);
        init(rootView);
        return rootView;
    }

    /**
     * Function to init fragment views
     * @param rootView Main view of fragment
     */
    private void init(View rootView) {

        HGDate hgDate = new HGDate();
        hgDate.toHigri();
        hgDate.setHigri(hgDate.getYear() , 9 , 1);

        eventList = new ArrayList<>();
        eventList.add(new Event(" "+getString(R.string.ramdanstart), hgDate.toString() , R.drawable.ramdan));

        hgDate.setHigri(hgDate.getYear() , 9 , 27);
        eventList.add(new Event(" "+getString(R.string.laylt_kader), hgDate.toString() , R.drawable.ic_azkar_n));

        hgDate.setHigri(hgDate.getYear() , 10 , 1);
        eventList.add(new Event(" "+getString(R.string.eid_el_feter), hgDate.toString() , R.drawable.ballon));

        hgDate.setHigri(hgDate.getYear() , 12 , 9);
        eventList.add(new Event(" "+getString(R.string.wafet_el_arafa), hgDate.toString() , R.drawable.ic_kaaba));

        hgDate.setHigri(hgDate.getYear() , 12 , 10);
        eventList.add(new Event(" "+getString(R.string.el_adha), hgDate.toString() , R.drawable.eldaha));

        hgDate.setHigri(hgDate.getYear()+1 , 1 , 1);
        eventList.add(new Event(" "+getString(R.string.islamic_year), hgDate.toString() , R.drawable.laytkadr));

        hgDate.setHigri(hgDate.getYear()+1 , 3 , 1);
        eventList.add(new Event(" "+getString(R.string.milad_al_naby), hgDate.toString() , R.drawable.mosque));

        eventRecyclerView = (RecyclerView) rootView.findViewById(R.id.events);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        eventRecyclerView.setLayoutManager(mLayoutManager);
        eventRecyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new IslamicEventAdapter(getActivity(), eventList);
        eventRecyclerView.setAdapter(adapter);
    }

}

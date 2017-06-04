package mindtrack.muslimorganizer.ui.fragments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mindtrack.muslimorganizer.R;
import mindtrack.muslimorganizer.adapter.AzkarAdapter;
import mindtrack.muslimorganizer.ui.activity.MainActivity;

/**
 * Fragment to show all azkar titles
 */
public class AzkarFragment extends Fragment {
    private RecyclerView azkarRecyclerView;
    private AzkarAdapter adapter;
    RecyclerView.LayoutManager mLayoutManager;
    private View rootview;
    /*private AdView adds ;
    private Adds addHelper ;*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //addHelper = new Adds();
        rootview = inflater.inflate(R.layout.fragment_azkar, container, false);
        init();
        return rootview;
    }

    private void init() {

        azkarRecyclerView = (RecyclerView) rootview.findViewById(R.id.AzkarList);
        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        azkarRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new AzkarAdapter(MainActivity.zekerTypeList, getActivity());
        azkarRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (adapter != null){
            Log.i("ADAPTER_COUNT" , "Size : "+adapter.getItemCount());
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        init();
        if (adapter != null){
            Log.i("ADAPTER_COUNT" , "Size : "+adapter.getItemCount());
        }
    }


}

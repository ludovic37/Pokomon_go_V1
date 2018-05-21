package cefim.turing.pokomon_go_v1.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cefim.turing.pokomon_go_v1.R;


/**
 * Created by crespeau on 13/04/2018.
 */

public class ProfileFragment extends Fragment {
    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("lol","onCreate Frag");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("lol","onCreateView Frag");
        Log.d("lol", "MapFragment : onCreateView");

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        return view;
    }
}

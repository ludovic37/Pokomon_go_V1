package cefim.turing.pokomon_go_v1.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import cefim.turing.pokomon_go_v1.R;
import cefim.turing.pokomon_go_v1.interfaces.APICallback;
import cefim.turing.pokomon_go_v1.models.Pokodex;
import cefim.turing.pokomon_go_v1.utils.UtilsAPI;
import cefim.turing.pokomon_go_v1.utils.UtilsPreferences;
import okhttp3.Response;

/**
 * Created by crespeau on 13/04/2018.
 */

public class PokodexFragment extends Fragment implements APICallback {

    private RecyclerView mRecyclerView;

    private ArrayList<Pokodex> mPokodexs;

    private Handler mHandler;

    private PokodexAdapter mPokodexAdapter;

    public PokodexFragment() {
    }

    public static PokodexFragment newInstance() {
        return new PokodexFragment();
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

        mPokodexs = new ArrayList<>();
        mHandler = new Handler();

        View view = inflater.inflate(R.layout.fragment_pokodex, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewPokodex);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),4);

        mRecyclerView.setLayoutManager(layoutManager);

        mPokodexAdapter = new PokodexAdapter(getContext(), mPokodexs);

        mRecyclerView.setAdapter(mPokodexAdapter);

        UtilsAPI.getInstance().get(this, UtilsAPI.URL_POKODEX, "{}", UtilsPreferences.getPreferences(getContext()).getString("token"), 0);

        return view;
    }

    @Override
    public void successCallback(Response response, int code) throws IOException {
        final String stringJSON = response.body().string();
        Log.d("TEST","**********");
        Log.d("TEST",stringJSON);
        Log.d("TEST","**********");

        try {

            for (int i = 1; i<=151; i++){
                String jsonPokodex = "{\"number\":" + i + ",\"name\":\"\",\"picture\":\"\",\"parent_number\":0,\"candy_required\":0,\"captured\":false}";

                JSONObject jsonObject = new JSONObject(jsonPokodex);
                Pokodex pokodex = new Pokodex(jsonObject);
                mPokodexs.add(pokodex);
            }

            JSONArray jsonArrayMyPokodex = new JSONArray(stringJSON);

            for (int i = 0; i < jsonArrayMyPokodex.length(); i++){
                JSONObject jsonMyPokodex = jsonArrayMyPokodex.getJSONObject(i);
                Pokodex myPokodex = new Pokodex(jsonMyPokodex);

                int number = jsonMyPokodex.getInt("number");

                for (Pokodex p : mPokodexs){

                    if (p.number == number){

                        p.name = myPokodex.name;
                        p.candy_required = myPokodex.candy_required;
                        p.parent_number = myPokodex.parent_number;
                        p.picture = myPokodex.picture;
                        p.captured = myPokodex.captured;
                        p.number = myPokodex.number;
                        //mPokodexs.set(j,myPokodex);
                        Log.d("AAA","----->"+p.name);
                        break;
                    }
                }
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mPokodexAdapter.notifyDataSetChanged();

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void failCallback(Response response, int code) {

    }
}

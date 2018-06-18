package cefim.turing.pokomon_go_v1.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cefim.turing.pokomon_go_v1.R;
import cefim.turing.pokomon_go_v1.interfaces.APICallback;
import cefim.turing.pokomon_go_v1.utils.UtilsAPI;
import cefim.turing.pokomon_go_v1.utils.UtilsCircleTransform;
import cefim.turing.pokomon_go_v1.utils.UtilsPreferences;
import okhttp3.Response;

public class PokostopActivity extends AppCompatActivity implements APICallback {

    private TextView mEditTextName;
    private ImageView mPokostopPicture;


    private Handler mHandler;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokostop);

        mContext = this;
        mHandler = new Handler();

        mEditTextName = (TextView) findViewById(R.id.pokostop_name);
        mPokostopPicture = (ImageView) findViewById(R.id.img_pokostop);

        Button mPokostopButton = (Button) findViewById(R.id.btn_pokostop_item);

        Intent intent = getIntent();

        String jsonPokostops = intent.getStringExtra("pokostops");
        final String pokostopsId = intent.getStringExtra("idPokostop");

        mPokostopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getItemPokostop(pokostopsId);
            }
        });
        Log.d("Test", jsonPokostops);

        JSONArray jsonarray = null;
        try {
            jsonarray = new JSONArray(jsonPokostops);

            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);

                if (jsonobject.getString("_id").equals(pokostopsId)){
                    mEditTextName.setText(jsonobject.getString("name"));
                    Picasso.get().load(jsonobject.getString("picture"))
                            //.centerCrop()
                            .transform(new UtilsCircleTransform())
                            .into(mPokostopPicture);
                    break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void successCallback(Response response, int code) throws IOException {

        if (code == 0){
            String newitems = response.body().string();
            String allItems = UtilsPreferences.getPreferences(mContext).getString("allitems");

            Log.d("----> MyItems : ", allItems);
            Log.d("----> NewItems : ", newitems);

            try {
                JSONArray jsonArrayNewItems = new JSONArray(newitems);

                String message = "Vous avez obtenue: ";

                for (int i = 0; i < jsonArrayNewItems.length(); i++) {
                    JSONObject jsonobject = jsonArrayNewItems.getJSONObject(i);
                    String nameItem = jsonobject.getString("name");

                    message = message + "1 " + nameItem + " ";
                }

                UtilsAPI.getInstance().get(this,UtilsAPI.URL_BAG,null,UtilsPreferences.getPreferences(mContext).getString("token"),1);

                final String finalMessage = message;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, finalMessage, Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (code == 1){
            String newitems = response.body().string();
            Log.d("----> ADD ITEM : ", newitems);
            UtilsPreferences.getPreferences(mContext).setKey("allitems",newitems);
        }

    }

    @Override
    public void failCallback(Response response, int code) {
        Log.d("---> Erreur", "Je suis dans le Fail");
        try {
            Log.d("---> Body", response.body().string());
            Log.d("---> Code", String.valueOf(response.code()));

            if (code == 0) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Vous devez encore attendre", Toast.LENGTH_SHORT).show();
                    }
                });
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getItemPokostop(String idPokostop){
        String url = String.format(UtilsAPI.URL_POKOSTOP, idPokostop);
        try{
            UtilsAPI.getInstance().post(PokostopActivity.this, url, "{}", UtilsPreferences.getPreferences(mContext).getString("token"), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

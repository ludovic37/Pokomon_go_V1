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

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cefim.turing.pokomon_go_v1.R;
import cefim.turing.pokomon_go_v1.interfaces.APICallback;
import cefim.turing.pokomon_go_v1.models.Pokomon;
import cefim.turing.pokomon_go_v1.utils.UtilsAPI;
import cefim.turing.pokomon_go_v1.utils.UtilsPreferences;
import okhttp3.Response;

public class CatchActivity extends AppCompatActivity implements APICallback {

    private TextView mTextViewXP;
    private TextView mTextViewName;
    private ImageView mPokostopPicture;

    private Pokomon pokomon;

    private Handler mHandler;
    private Context mContext;

    private String idPokomon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokomon);

        mContext = this;
        mHandler = new Handler();

        mTextViewName = (TextView) findViewById(R.id.textViewCatchPokomonName);
        mTextViewXP = (TextView) findViewById(R.id.textViewCatchPokomonXP);
        mPokostopPicture = (ImageView) findViewById(R.id.ImageViewCatchPokomonName);

        //Button mPokostopButton = (Button) findViewById(R.id.buttonCatch);
        ImageView mImageViewPokeball = (ImageView) findViewById(R.id.catchPokeball);
        ImageView mImageViewSuperball = (ImageView) findViewById(R.id.catchSuperball);
        ImageView mImageViewHyperball = (ImageView) findViewById(R.id.catchHyperball);

        Intent intent = getIntent();

        String jsonPokomon = intent.getStringExtra("pokomon");

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonPokomon);

            Gson gson = new Gson();
            pokomon = gson.fromJson(jsonObject.toString(), Pokomon.class);

            Log.d("POKOMON",jsonObject.toString());

            mTextViewName.setText(pokomon.name);
            mTextViewXP.setText(""+pokomon.xp);
            Picasso.get().load(pokomon.picture)
                    //.centerCrop()
                    //.transform(new UtilsCircleTransform())
                    .into(mPokostopPicture);

            idPokomon = pokomon._id;

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this,"error interne", Toast.LENGTH_SHORT).show();
            finish();
        }

        mImageViewPokeball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    trytocatch("pokeball");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mImageViewSuperball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    trytocatch("superball");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mImageViewHyperball.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    trytocatch("hyperball");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        /*mPokostopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    trytocatch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/


    }

    @Override
    public void successCallback(Response response, int code) throws IOException {

        String data = response.body().string();

        if (code == 0){

            //retiter une pokoball
            //removeOnePokoball();

            Log.d("POKOMON","capturer");
            finish();

        }else{
            Log.d("----> ADD ITEM : ", data);
            UtilsPreferences.getPreferences(mContext).setKey("allitems",data);
        }


    }

    @Override
    public void failCallback(Response response, int code) {

        try {
            String data = response.body().string();

            if (code == 0){
                final int g = response.code();

                switch (g){
                    case 403:
                        //todo catch fail sup 1 pokoball
                        Log.d("POKOMON","fail");
                        //removeOnePokoball();
                        break;
                    case 404:
                        //todo pokomon plus dispo
                        Log.d("POKOMON","move");
                        finish();
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void trytocatch(String pokeball) throws Exception {

        String allItems = UtilsPreferences.getPreferences(mContext).getString("allitems");
        JSONArray jsonArrayAllItem = new JSONArray(allItems);
        boolean ihavepokoball = false;

        for (int i = 0; i < jsonArrayAllItem.length(); i++) {

            Log.d("ITEM",allItems);

            JSONObject jsonobjectAllItem = jsonArrayAllItem.getJSONObject(i);

            String idItem = jsonobjectAllItem.getJSONObject("item").getString("_id");
            String nameItem = jsonobjectAllItem.getJSONObject("item").getString("name");
            int amount = jsonobjectAllItem.getInt("amount");

            if (nameItem.equals(pokeball) && amount > 0){

                String url = String.format(UtilsAPI.URL_POKOMON,idPokomon);
                String paramCatch = String.format(UtilsAPI.URL_POKOMON_PARAMS,pokeball);
                UtilsAPI.getInstance().post(this,url,paramCatch, UtilsPreferences.getPreferences(mContext).getString("token"),0);

                amount = amount - 1;
                ihavepokoball = true;

                Object[] paramsValues = {idItem, nameItem, amount};
                String paramItem = String.format(UtilsAPI.URL_BAG_PARAMS,paramsValues);

                UtilsAPI.getInstance().post(this,UtilsAPI.URL_BAG,paramItem,UtilsPreferences.getPreferences(mContext).getString("token"),1);
            }


            /*if (nameItem.equals("pokeball") && amount > 0){

                String url = String.format(UtilsAPI.URL_POKOMON,idPokomon);
                UtilsAPI.getInstance().post(this,url,"{}", UtilsPreferences.getPreferences(mContext).getString("token"),0);

                amount = amount - 1;
                ihavepokoball = true;

                Object[] paramsValues = {idItem, nameItem, amount};
                String param = String.format(UtilsAPI.URL_BAG_PARAMS,paramsValues);

                UtilsAPI.getInstance().post(this,UtilsAPI.URL_BAG,param,UtilsPreferences.getPreferences(mContext).getString("token"),1);
            }*/

        }

        if (!ihavepokoball){
            Toast.makeText(mContext,"vous avez plus de "+pokeball, Toast.LENGTH_SHORT).show();
            Log.d("catch", "vous avez plus de pokeball");
        }
    }

    public void removeOnePokoball() throws JSONException, IOException {

        String allItems = UtilsPreferences.getPreferences(mContext).getString("allitems");
        JSONArray jsonArrayAllItem = null;

        jsonArrayAllItem = new JSONArray(allItems);

        for (int i = 0; i < jsonArrayAllItem.length(); i++) {

            JSONObject jsonobjectAllItem = jsonArrayAllItem.getJSONObject(i);

            String idItem = jsonobjectAllItem.getJSONObject("item").getString("_id");
            String nameItem = jsonobjectAllItem.getJSONObject("item").getString("name");
            int amount = jsonobjectAllItem.getInt("amount");

            if (nameItem.equals("pokoball") && amount > 0){
                amount = amount - 1;
                Object[] paramsValues = {idItem, nameItem, amount};
                String param = String.format(UtilsAPI.URL_BAG_PARAMS,paramsValues);
                UtilsAPI.getInstance().post(this,UtilsAPI.URL_BAG,param,UtilsPreferences.getPreferences(mContext).getString("token"),1);
            }

        }
    }
}

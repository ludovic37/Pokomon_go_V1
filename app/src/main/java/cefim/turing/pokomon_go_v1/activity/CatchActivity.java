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

        Button mPokostopButton = (Button) findViewById(R.id.buttonCatch);

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

        mPokostopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    trytocatch();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void successCallback(Response response, int code) throws IOException {

        String data = response.body().string();

        if (code == 0){
            try {

                //retiter une pokoball
                removeOnePokoball();

                /*String allItems = UtilsPreferences.getPreferences(mContext).getString("allitems");
                JSONArray jsonArrayAllItem = null;

                jsonArrayAllItem = new JSONArray(allItems);

                for (int i = 0; i < jsonArrayAllItem.length(); i++) {

                    JSONObject jsonobjectAllItem = jsonArrayAllItem.getJSONObject(i);

                    String idItem = jsonobjectAllItem.getString("_id");
                    String nameItem = jsonobjectAllItem.getString("name");
                    int amount = jsonobjectAllItem.getInt("amount");

                    if (nameItem.equals("pokoball") && amount > 0){
                        amount = amount - 1;
                        Object[] paramsValues = {idItem, nameItem, amount};
                        String param = String.format(UtilsAPI.URL_BAG_PARAMS,paramsValues);
                        UtilsAPI.getInstance().post(this,UtilsAPI.URL_BAG,param,UtilsPreferences.getPreferences(mContext).getString("token"),1);
                    }


                }*/
                Log.d("POKOMON","capturer");
                finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                        removeOnePokoball();
                        break;
                    case 404:
                        //todo pokomon plus dispo
                        Log.d("POKOMON","move");
                        finish();
                        break;
                }
            }else{

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void trytocatch() throws Exception {

        String allItems = UtilsPreferences.getPreferences(mContext).getString("allitems");
        JSONArray jsonArrayAllItem = new JSONArray(allItems);
        boolean ihavepokoball = false;

        for (int i = 0; i < jsonArrayAllItem.length(); i++) {

            Log.d("ITEM",allItems);

            JSONObject jsonobjectAllItem = jsonArrayAllItem.getJSONObject(i);

            String idItem = jsonobjectAllItem.getJSONObject("item").getString("_id");
            String nameItem = jsonobjectAllItem.getJSONObject("item").getString("name");
            int amount = jsonobjectAllItem.getInt("amount");


            if (nameItem.equals("pokeball") && amount > 0){

                String url = String.format(UtilsAPI.URL_POKOMON,idPokomon);
                UtilsAPI.getInstance().post(this,url,"{}", UtilsPreferences.getPreferences(mContext).getString("token"),0);

                amount = amount - 1;
                ihavepokoball = true;

                Object[] paramsValues = {idItem, nameItem, amount};
                String param = String.format(UtilsAPI.URL_BAG_PARAMS,paramsValues);

                UtilsAPI.getInstance().post(this,UtilsAPI.URL_BAG,param,UtilsPreferences.getPreferences(mContext).getString("token"),1);
            }

        }

        if (!ihavepokoball){
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

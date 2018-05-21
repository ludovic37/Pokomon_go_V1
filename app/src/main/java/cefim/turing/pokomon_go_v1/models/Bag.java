package cefim.turing.pokomon_go_v1.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by crespeau on 15/05/2018.
 */

public class Bag implements Serializable {

    public String _id;
    public String name;
    public int amount;

    public Bag(JSONObject jsonObject) throws JSONException {

        _id = jsonObject.getJSONObject("item").getString("_id");
        name = jsonObject.getJSONObject("item").getString("name");
        amount = jsonObject.getInt("amount");
    }
}

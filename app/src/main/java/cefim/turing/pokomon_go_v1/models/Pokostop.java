package cefim.turing.pokomon_go_v1.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by peterbardu on 3/24/17.
 */

public class Pokostop implements Serializable {

    public String _id;
    public String name;
    public double[] coordinates;
    public String picture;


    public Pokostop(JSONObject jsonObject) throws JSONException {

        _id = jsonObject.getString("_id");
        picture = jsonObject.getString("picture");
        name = jsonObject.getString("name");

    }
}

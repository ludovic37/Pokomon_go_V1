package cefim.turing.pokomon_go_v1.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by crespeau on 16/05/2018.
 */

public class Pokodex implements Serializable {

    public String name;
    public String picture;
    public int number;
    public int parent_number;
    public int candy_required;
    public boolean captured;

    public Pokodex(JSONObject jsonObject) throws JSONException {

        picture = jsonObject.getString("picture");
        name = jsonObject.getString("name");
        number = jsonObject.getInt("number");
        parent_number = jsonObject.getInt("parent_number");
        candy_required = jsonObject.getInt("candy_required");
        captured = jsonObject.getBoolean("captured");
    }
}

package cefim.turing.pokomon_go_v1.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by peterbardu on 3/24/17.
 */

public class Pokomon implements Serializable {

    //{"_id":"58b6f4bc75fa97000469e21f",
    // "level":2,
    // "attaque":8,
    // "defense":0,
    // "vitesse":9,
    // "xp":0,
    // "coordinates":[47.39233510793859,0.7000970190311437],
    // "name":"Chenipan",
    // "picture":"http://www.pokemontrash.com/pokedex/images/minixy/010.png",
    // "number":10,
    // "captured":false}

    //public String mId;
    // TODO : les autres...
    //public int mPicture;
    // TODO : les autres...

    public String _id;
    public int level;
    public int attaque;
    public int defense;
    public int vitesse;
    public int xp;
    public double[] coordinates;
    public String name;
    public String picture;
    public int number;
    public boolean captured;
    public int parent_number;

    public Pokomon(JSONObject jsonObject) throws JSONException {

        _id = jsonObject.getString("_id");
        name = jsonObject.getString("name");
        picture = jsonObject.getString("picture");
        level = jsonObject.getInt("level");
        attaque = jsonObject.getInt("attaque");
        defense = jsonObject.getInt("defense");
        vitesse = jsonObject.getInt("vitesse");
        parent_number = jsonObject.getInt("parent_number");
        xp = jsonObject.getInt("xp");
        number = jsonObject.getInt("number");
        captured = jsonObject.getBoolean("captured");
        //coordinates = jsonObject.get("captured");
        // TODO : les autres...
    }
}

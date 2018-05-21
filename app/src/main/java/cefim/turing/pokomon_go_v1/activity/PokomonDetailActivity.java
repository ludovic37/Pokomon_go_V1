package cefim.turing.pokomon_go_v1.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cefim.turing.pokomon_go_v1.R;
import cefim.turing.pokomon_go_v1.models.Pokomon;
import cefim.turing.pokomon_go_v1.utils.Utils;

public class PokomonDetailActivity extends AppCompatActivity {
    // Init Object Pokomon
    Pokomon mPokomon;

    // Init variables view
    TextView dTextXP;
    ImageView dImagePokomon;
    ImageView dImgPokomonCurrent;
    ImageView dImgPokomonEvolve;
    TextView dTextName;
    TextView dTextAttack;
    TextView dTextDefense;
    TextView dTextSpeed;

    LinearLayout blockEvolve;

    // Init global variable Handler and Context
    private Handler mHandler;
    private Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pokomon_detail);

        mContext = this;
        mHandler = new Handler();

        dTextXP         = (TextView) findViewById(R.id.text_pokomon_detail_xp);
        dTextName       = (TextView) findViewById(R.id.text_pokomon_detail_name);
        dTextAttack     = (TextView) findViewById(R.id.text_pokomon_detail_attack);
        dTextDefense    = (TextView) findViewById(R.id.text_pokomon_detail_defense);
        dTextSpeed      = (TextView) findViewById(R.id.text_pokomon_detail_speed);
        dImagePokomon   = (ImageView) findViewById(R.id.img_pokomon_detail_current);
        dImgPokomonCurrent = (ImageView) findViewById(R.id.img_pokomon_detail_evolution_current);
        dImgPokomonEvolve  = (ImageView) findViewById(R.id.img_pokomon_detail_evolution_evolve);

        blockEvolve = (LinearLayout) findViewById(R.id.block_evolve);

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){
            mPokomon = (Pokomon) bundle.getSerializable("pokomon");

            dTextXP.setText(mPokomon.xp + " PC");
            dTextName.setText(mPokomon.name);
            dTextAttack.setText(""+mPokomon.attaque);
            dTextSpeed.setText(""+mPokomon.vitesse);
            dTextDefense.setText(""+mPokomon.defense);
            dImagePokomon.setImageResource(Utils.TAB_IMAGE_POKOMON_L[mPokomon.number]);

            Log.d("DETAIL","PARENT : "+mPokomon.parent_number);

            if(mPokomon.parent_number == 0){
                blockEvolve.setVisibility(View.INVISIBLE);
            } else {
                blockEvolve.setVisibility(View.VISIBLE);
                dImgPokomonEvolve.setImageResource(Utils.TAB_IMAGE_POKOMON_L[mPokomon.parent_number]);
            }
            dImgPokomonCurrent.setImageResource(Utils.TAB_IMAGE_POKOMON_L[mPokomon.number]);
        }
    }

}
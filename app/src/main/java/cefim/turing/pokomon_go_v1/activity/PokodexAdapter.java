package cefim.turing.pokomon_go_v1.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cefim.turing.pokomon_go_v1.R;
import cefim.turing.pokomon_go_v1.models.Pokodex;
import cefim.turing.pokomon_go_v1.utils.Utils;

/**
 * Created by crespeau on 16/05/2018.
 */

public class PokodexAdapter extends RecyclerView.Adapter<PokodexAdapter.ViewHolder> {

    private ArrayList<Pokodex> mPokodexs;
    private Context mContext;

    public PokodexAdapter(Context context, ArrayList<Pokodex> pokodexs){
        mContext = context;
        mPokodexs = pokodexs;
    }



    @Override
    public PokodexAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // On créée une vue depuis le fichier template (Habillage)
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_pokodex, parent, false);

        // On créée un holder qui va contenir la vue afin d'en extraire les objets de la vue (TextView, ImageView, ...)
        PokodexAdapter.ViewHolder viewHolder = new PokodexAdapter.ViewHolder(itemView);

        // On retourne le view holder
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PokodexAdapter.ViewHolder holder, int position) {
        Pokodex pokodex = mPokodexs.get(position);

        if (pokodex.name.equals("")){
            holder.mPokomonNumber.setText(""+pokodex.number);
            holder.mPokomonNumber.setVisibility(View.VISIBLE);
            holder.mPokomonImage.setVisibility(View.INVISIBLE);
        }else {
            holder.mPokomonNumber.setVisibility(View.INVISIBLE);
            holder.mPokomonImage.setImageResource(Utils.TAB_IMAGE_POKOMON_L[pokodex.number]);
            holder.mPokomonImage.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return mPokodexs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mPokomonImage;
        public TextView mPokomonNumber;

        public ViewHolder(View itemView) {
            super(itemView);

            mPokomonImage = (ImageView) itemView.findViewById(R.id.imageViewPokodexPokomon);
            mPokomonNumber = (TextView) itemView.findViewById(R.id.textViewPokodexNumber);

        }

    }
}

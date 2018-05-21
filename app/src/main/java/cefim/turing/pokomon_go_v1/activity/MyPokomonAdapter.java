package cefim.turing.pokomon_go_v1.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cefim.turing.pokomon_go_v1.R;
import cefim.turing.pokomon_go_v1.models.Pokomon;
import cefim.turing.pokomon_go_v1.utils.Utils;

class MyPokomonAdapter extends Adapter<MyPokomonAdapter.ViewHolder> {

    private ArrayList<Pokomon> mPokomons;
    private Context mContext;

    public MyPokomonAdapter(Context context, ArrayList<Pokomon> pokomons){
        mContext = context;
        mPokomons = pokomons;
    }

    @NonNull
    @Override
    public MyPokomonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // On créée une vue depuis le fichier template (Habillage)
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_mypokomons, parent, false);

        // On créée un holder qui va contenir la vue afin d'en extraire les objets de la vue (TextView, ImageView, ...)
        ViewHolder viewHolder = new ViewHolder(itemView);

        // On retourne le view holder
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyPokomonAdapter.ViewHolder holder, int position) {
        Pokomon pokomon = mPokomons.get(position);

        holder.mPokomonNumber.setText(""+pokomon.number);
        holder.mPokomonName.setText(pokomon.name);
        holder.mPokomonXp.setText(""+pokomon.xp);
        holder.mPokomonImage.setImageResource(Utils.TAB_IMAGE_POKOMON_L[pokomon.number]);

        holder.mPokomon = pokomon;

    }

    @Override
    public int getItemCount() {
        return mPokomons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public RelativeLayout mPokomonLayout;

        public ImageView mPokomonImage;
        public TextView mPokomonName;
        public TextView mPokomonNumber;
        public TextView mPokomonXp;

        public Pokomon mPokomon;

        public ViewHolder(View itemView) {
            super(itemView);

            mPokomonLayout = (RelativeLayout) itemView.findViewById(R.id.itemMyPokomonLine);
            mPokomonImage = (ImageView) itemView.findViewById(R.id.itemMyPokomonImage);
            mPokomonName = (TextView) itemView.findViewById(R.id.itemMyPokomonName);
            mPokomonNumber = (TextView) itemView.findViewById(R.id.itemMyPokomonNumber);
            mPokomonXp = (TextView) itemView.findViewById(R.id.itemMyPokomonXp);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, PokomonDetailActivity.class);
            intent.putExtra("pokomon", mPokomon);

            Log.d("DETAIL","PARENT : "+mPokomon.parent_number);

            mContext.startActivity(intent);
        }
    }
}
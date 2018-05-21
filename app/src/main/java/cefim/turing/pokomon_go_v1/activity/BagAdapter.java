package cefim.turing.pokomon_go_v1.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cefim.turing.pokomon_go_v1.R;
import cefim.turing.pokomon_go_v1.models.Bag;


/**
 * Created by crespeau on 15/05/2018.
 */

public class BagAdapter extends RecyclerView.Adapter<BagAdapter.ViewHolder>{

    private ArrayList<Bag> mBags;
    private Context mContext;

    public BagAdapter(Context context, ArrayList<Bag> bags) {

        Log.d("Adapter", "BagAdapter constructor");

        mContext = context;
        mBags = bags;
    }


    @Override
    public BagAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("lol", "onCreateViewHolder");

        // On créée une vue depuis le fichier template (Habillage)
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_bag, parent, false);

        // On créée un holder qui va contenir la vue afin d'en extraire les objets de la vue (TextView, ImageView, ...)
        ViewHolder viewHolder = new ViewHolder(itemView);

        // On retourne le view holder
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BagAdapter.ViewHolder holder, int position) {
        Log.d("lol", "onBindViewHolder");

        // On récupère la livre à partir de la postion pour l'affichage
        Bag bag = mBags.get(position);

        Log.d("NAME","NAME : "+bag.name);
        Log.d("NAME","NAME : "+bag.amount);

        // On rempli l'objet holder en avec les informations d'un livre (Remplissage)
        holder.mTextViewName.setText(bag.name);
        holder.mTextViewAmount.setText(""+bag.amount);

        int resourceID = mContext.getResources().getIdentifier(bag.name, "drawable",mContext.getPackageName());

        holder.mImageViewItem.setImageResource(resourceID);

        holder.mBag = bag;

    }

    @Override
    public int getItemCount() {
        return mBags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public ImageView mImageViewItem;
        public TextView mTextViewName;
        public TextView mTextViewAmount;

        public Bag mBag;

        public ViewHolder(View itemView) {
            super(itemView);

            mImageViewItem = (ImageView) itemView.findViewById(R.id.imageViewItem);
            mTextViewName = (TextView) itemView.findViewById(R.id.textViewItemName);
            mTextViewAmount = (TextView) itemView.findViewById(R.id.textViewItemamout);

            //itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            /*Intent intent = new Intent(mContext, BookActivity.class);
            intent.putExtra("book", mBook);

            mContext.startActivity(intent);*/

        }
    }
}

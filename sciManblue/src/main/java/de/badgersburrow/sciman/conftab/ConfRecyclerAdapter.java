package de.badgersburrow.sciman.conftab;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.badgersburrow.sciman.main.MainActivity;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.Conference;

import java.util.ArrayList;
import java.util.Map;


public class ConfRecyclerAdapter extends RecyclerView.Adapter<ConfRecyclerAdapter.ViewHolder>{
    private ArrayList<Conference> itemsData;
    private static final String TAG = "AndroidBib";
    Map<String, Integer> topicsDict = MainActivity.topicsDict;

    public ConfRecyclerAdapter(ArrayList<Conference> itemsData) {
        this.itemsData = itemsData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ConfRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.bib_cardview_item, null);

        // create ViewHolder
        //ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        ConfRecyclerAdapter.ViewHolder vh = new ViewHolder(itemLayoutView, new ConfRecyclerAdapter.ViewHolder.ViewHolderClicks() {
            public void onCardView(CardView callerCardView, int pos) {

            	}

            @Override
			public void onMenuButton(ImageView callerImage, int pos) {

            	}
        });
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData
        final int posPub = position;

        viewHolder.txtViewTitle.setText(itemsData.get(position).getConfname());
        if (itemsData.get(position).getIconString()!= null){
            viewHolder.imgViewIcon.setImageBitmap(itemsData.get(position).getIconBitmap());
        }

        viewHolder.mConfitem = itemsData.get(position);
        //BibFragment.mainActivity.registerForContextMenu(viewHolder.menuIcon);
        viewHolder.mListener = new ConfRecyclerAdapter.ViewHolder.ViewHolderClicks() {
            @Override
			public void onMenuButton(ImageView callerImageView, int pos) {
            	ConfFragment.selectedConf = itemsData.get(pos);
                ConfFragment.selectedConf.setID(pos);
            	callerImageView.showContextMenu();

            	
            }
			@Override
			public void onCardView(CardView callerCardView, int pos) {
				ConfFragment.selectedConf = itemsData.get(pos);
                ConfFragment.selectedConf.setID(pos);
				Context context = ConfFragment.mainActivity;
				ConfFragment.OpenConf(itemsData.get(pos), context);
			}
        };
    }
     
    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder  implements OnClickListener {
        
    	public CardView  bibCardView;
        public TextView  txtViewTitle;
        public ImageView imgViewIcon;
        public ImageView menuIcon;
        public ViewHolderClicks mListener;
        public Conference mConfitem;
         
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            bibCardView  = (CardView)  itemLayoutView.findViewById(R.id.card_view);
            txtViewTitle = (TextView)  itemLayoutView.findViewById(R.id.item_title);
            imgViewIcon  = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
            menuIcon     = (ImageView) itemLayoutView.findViewById(R.id.item_menu);
            menuIcon.setOnClickListener(this);
            bibCardView.setOnClickListener(this);
            // Is this needed or handled automatically by RecyclerView.ViewHolder?
            itemLayoutView.setOnClickListener(this);
        }

        public ViewHolder(View itemLayoutView, ViewHolderClicks listener) {
            super(itemLayoutView);
            mListener = listener;
            bibCardView  = (CardView)  itemLayoutView.findViewById(R.id.card_view);
            txtViewTitle = (TextView)  itemLayoutView.findViewById(R.id.item_title);
            imgViewIcon  = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
            menuIcon     = (ImageView) itemLayoutView.findViewById(R.id.item_menu);
            
            menuIcon.setOnClickListener(this);
            bibCardView.setOnClickListener(this);
            // Is this needed or handled automatically by RecyclerView.ViewHolder?
            itemLayoutView.setOnClickListener(this);
        }


        public interface ViewHolderClicks {
            void onMenuButton(ImageView callerImage, int pos);
            void onCardView(CardView callerCardView, int pos);
        }

		@Override
		public void onClick(View v) {
            int pos = this.getAdapterPosition();
            //ConfFragment.selectedConf = ConfFragment.Confitems.get(pos);
            if (v instanceof ImageView){
                mListener.onMenuButton((ImageView) v, pos);
             } else if (v instanceof CardView){
                mListener.onCardView((CardView) v, pos);
             }			
		}
    }
    
    public void add(Conference Confitem){
        Confitem.setID(getItemCount());
    	itemsData.add(Confitem);
    	this.notifyItemInserted(getItemCount() - 1);
        if (Confitem.getIdentifier() != null){
            MainActivity.ConfitemsDict.put(Confitem.getIdentifier(),Confitem);
        }
    }
    
    public void remove(Conference Confitem){
    	int Confid = Confitem.getID();
    	itemsData.remove(Confid);
        this.notifyItemRemoved(Confid);

        if (Confitem.getIdentifier() != null){
            MainActivity.ConfitemsDict.remove(Confitem.getIdentifier());
        }

    }

    
    public void replace(int id,Conference Confitem){
    	itemsData.set(id, Confitem);
    	this.notifyItemChanged(Confitem.getID());
    	this.notifyDataSetChanged();
    }
    
    public ArrayList<Conference> getItems(){
    	return itemsData;
    }
    
    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }


}
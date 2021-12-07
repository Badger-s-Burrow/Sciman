package de.badgersburrow.sciman.bibtab;

import java.util.ArrayList;


import java.util.Map;

import de.badgersburrow.sciman.main.MainActivity;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.Bib;

import android.content.Context;
import android.view.View.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


public class BibRecyclerAdapter extends RecyclerView.Adapter<BibRecyclerAdapter.ViewHolder>{
    private ArrayList<Bib> itemsData;
    private static final String TAG = "AndroidBib";
    Map<String, Integer> topicsDict = MainActivity.topicsDict;
 
    public BibRecyclerAdapter(ArrayList<Bib> itemsData) {
        this.itemsData = itemsData;
    }
     
    // Create new views (invoked by the layout manager)
    @Override
    public BibRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                               .inflate(de.badgersburrow.sciman.R.layout.bib_cardview_item, null);
        

        
        // create ViewHolder        
        //ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        BibRecyclerAdapter.ViewHolder vh = new ViewHolder(itemLayoutView, new BibRecyclerAdapter.ViewHolder.ViewHolderClicks() { 
            public void onCardView(CardView callerCardView, int pos) {
            	BibFragment.showToast("cardView pressed"); 
            	}

            @Override
			public void onMenuButton(ImageView callerImage, int pos) {
            	BibFragment.showToast("menuButton pressed"); 
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
        
        viewHolder.txtViewTitle.setText(itemsData.get(position).getBibname());
        viewHolder.imgViewIcon.setImageResource(topicsDict.get(itemsData.get(position).getIconKey()));
        viewHolder.mBibitem = itemsData.get(position);
        //BibFragment.mainActivity.registerForContextMenu(viewHolder.menuIcon);
        viewHolder.mListener = new BibRecyclerAdapter.ViewHolder.ViewHolderClicks() { 
            @Override
			public void onMenuButton(ImageView callerImageView, int pos) {
            	BibFragment.selectedBib = itemsData.get(pos);
            	callerImageView.showContextMenu();
            	/*callerImageView.
            	//Creating the instance of PopupMenu  
                PopupMenu popup = new PopupMenu(callerImageView.getContext(), callerImageView);  
                //Inflating the Popup using xml file  
                
                if (BibFragment.selectedBib.getSource().equalsIgnoreCase("local")){
    	        	popup.getMenuInflater().inflate(R.layout.main_cm_local, popup.getMenu());  
    	        }else if(BibFragment.selectedBib.getSource().equalsIgnoreCase("dropbox")){
    	        	popup.getMenuInflater().inflate(R.layout.main_cm_dropbox, popup.getMenu());  
    	        }else if(BibFragment.selectedBib.getSource().equalsIgnoreCase("manualApp")){
	    	        popup.getMenuInflater().inflate(R.layout.main_cm_manualapp, popup.getMenu());  
	    	        MenuItem syncItem = popup.getMenu().findItem(R.id.SyncManualApp);
	    	        syncItem.setTitle(callerImageView.getContext().getString(R.string.mcm_syncmanualapp) + " " + BibFragment.selectedBib.getManualAppName());
    	        	  
    	        }else{
    	        	popup.getMenuInflater().inflate(R.layout.main_cm_local, popup.getMenu());
    	        }
                
                
                //registering popup with OnMenuItemClickListener  
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {  
                 public boolean onMenuItemClick(MenuItem item) {  
                  Toast.makeText(item.getContext(),"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();  
                  return true;  
                 }  

                });*/
            	
            }
			@Override
			public void onCardView(CardView callerCardView, int pos) {
				BibFragment.selectedBib = itemsData.get(pos);
				Context context = BibFragment.mainActivity;	
				BibFragment.OpenBib(itemsData.get(pos), context);
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
        public Bib mBibitem;
         
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
            if (v instanceof ImageView){
                mListener.onMenuButton((ImageView) v, pos);
             } else if (v instanceof CardView){
                mListener.onCardView((CardView) v, pos);
             }			
		}
    }
    
    public void add(Bib Bibitem){
    	itemsData.add(Bibitem);
    	this.notifyItemInserted(getItemCount() - 1);
    	//this.notifyDataSetChanged();
    }
    
    public void remove(Bib Bibitem){
    	int Bibid = Bibitem.getID();
    	itemsData.remove(Bibid);
    	this.notifyItemRemoved(Bibid);
    	for (int i=Bibid; i<getItemCount();i++){
    		itemsData.get(i).setID(i);
    	}
    	//this.notifyDataSetChanged();
    }
    
    public void removeAll(){
    	for (Bib Bibitem : itemsData){
    		itemsData.remove(Bibitem);
    		this.notifyItemRemoved(Bibitem.getID());
    	}
    	this.notifyDataSetChanged();
    }
    
    public void replace(int id,Bib Bibitem){
    	itemsData.set(id, Bibitem);
    	this.notifyItemChanged(Bibitem.getID());
    	this.notifyDataSetChanged();
    }
    
    public ArrayList<Bib> getItems(){
    	return itemsData;
    }
    
    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}
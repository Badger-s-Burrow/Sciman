package de.badgersburrow.sciman.conftab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.badgersburrow.sciman.main.MainActivity;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.Conference;

import java.util.ArrayList;
import java.util.Map;


public class ConfSelectRecyclerAdapter extends RecyclerView.Adapter<ConfSelectRecyclerAdapter.ViewHolder>{
    private ArrayList<Conference> itemsData;
    private static final String TAG = "AndroidBib";
    Map<String, Integer> topicsDict = MainActivity.topicsDict;

    public ConfSelectRecyclerAdapter(ArrayList<Conference> itemsData) {
        this.itemsData = itemsData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ConfSelectRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.conf_cardview_item, null);


        ConfSelectRecyclerAdapter.ViewHolder vh = new ViewHolder(itemLayoutView, new ConfSelectRecyclerAdapter.ViewHolder.ViewHolderClicks() {
            public void onCardView(CardView callerCardView) {

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
        viewHolder.imgViewIcon.setImageBitmap(itemsData.get(position).getIconBitmap());
        viewHolder.mConfitem = itemsData.get(position);
        //BibFragment.mainActivity.registerForContextMenu(viewHolder.menuIcon);
        viewHolder.mListener = new ConfSelectRecyclerAdapter.ViewHolder.ViewHolderClicks() {

			@Override
			public void onCardView(CardView callerCardView) {
				Conference selectedConf = itemsData.get(posPub);

                //REDO


                if (MainActivity.ConfitemsDict.containsKey(selectedConf.getIdentifier())){

                } else {
                    ConfFragment.pollServer(MainActivity.requestIdGetConf, selectedConf.getIdentifier());
                }

                //OpenConf(itemsData.get(posPub), context);

			}
        };
    }
     
    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder  implements OnClickListener {
        
    	public CardView  confCardView;
        public TextView  txtViewTitle;
        public ImageView imgViewIcon;
        //public ImageView menuIcon;
        public ViewHolderClicks mListener;
        public Conference mConfitem;
         
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            confCardView  = (CardView)  itemLayoutView.findViewById(R.id.card_view);
            txtViewTitle = (TextView)  itemLayoutView.findViewById(R.id.item_title);
            imgViewIcon  = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
            //menuIcon     = (ImageView) itemLayoutView.findViewById(R.id.item_menu);
            //menuIcon.setOnClickListener(this);
            confCardView.setOnClickListener(this);
            // Is this needed or handled automatically by RecyclerView.ViewHolder?
            itemLayoutView.setOnClickListener(this);
        }

        public ViewHolder(View itemLayoutView, ViewHolderClicks listener) {
            super(itemLayoutView);
            mListener = listener;
            confCardView  = (CardView)  itemLayoutView.findViewById(R.id.card_view);
            txtViewTitle = (TextView)  itemLayoutView.findViewById(R.id.item_title);
            imgViewIcon  = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
            //menuIcon     = (ImageView) itemLayoutView.findViewById(R.id.item_menu);
            
            //menuIcon.setOnClickListener(this);
            confCardView.setOnClickListener(this);
            // Is this needed or handled automatically by RecyclerView.ViewHolder?
            itemLayoutView.setOnClickListener(this);
        }


        public interface ViewHolderClicks {
            //void onMenuButton(ImageView callerImage);
            void onCardView(CardView callerCardView);
        }

		@Override
		public void onClick(View v) {
           if (v instanceof CardView){
                mListener.onCardView((CardView) v);
             }			
		}
    }

    public void add(Conference Confitem){
        Confitem.setID(getItemCount());
        itemsData.add(Confitem);
        this.notifyItemInserted(getItemCount() - 1);



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
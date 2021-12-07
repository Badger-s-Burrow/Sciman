package de.badgersburrow.sciman.bibtab;

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
import de.badgersburrow.sciman.objects.item;

import java.util.ArrayList;
import java.util.Map;


public class BibListViewRecyclerAdapter extends RecyclerView.Adapter<BibListViewRecyclerAdapter.ViewHolder>{
    private ArrayList<item> itemsData;
    private static final String TAG = "AndroidBib";
    private Map<String,Integer> bibitemtypesDict = MainActivity.bibitemtypesDict;

    public BibListViewRecyclerAdapter(ArrayList<item> itemsData) {
        this.itemsData = itemsData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public BibListViewRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.bib_listview_entry, null);


        BibListViewRecyclerAdapter.ViewHolder vh = new ViewHolder(itemLayoutView, new BibListViewRecyclerAdapter.ViewHolder.ViewHolderClicks() {
            public void onCardView(CardView callerCardView) {
                BibFragment.showToast("cardView pressed");
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
        int type;
        String key = itemsData.get(position).getType();
        if (bibitemtypesDict.containsKey(key)){
            type = bibitemtypesDict.get(key);
        } else {
            type = bibitemtypesDict.get("Article");
        }

        viewHolder.typeView.setImageResource(type);
        viewHolder.textViewTitle.setText(itemsData.get(position).getTitle());
        viewHolder.textViewAuthor.setText(itemsData.get(position).getAuthor());

        //BibFragment.mainActivity.registerForContextMenu(viewHolder.menuIcon);
        viewHolder.mListener = new BibListViewRecyclerAdapter.ViewHolder.ViewHolderClicks() {

			@Override
			public void onCardView(CardView callerCardView) {
				item mBibitem = itemsData.get(posPub);
                BibListViewFragment.showBibitem(mBibitem);

			}
        };
    }
     
    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder  implements OnClickListener {

        public CardView  itemCardView;
    	public ImageView typeView;
        public TextView  textViewTitle;
        public TextView  textViewAuthor;
        //public ImageView menuIcon;
        public ViewHolderClicks mListener;
        public item mBibitem;
         
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            itemCardView  = (CardView)  itemLayoutView.findViewById(R.id.card_view);
            typeView       = (ImageView) itemLayoutView.findViewById(R.id.listview_type);
            textViewTitle  = (TextView)  itemLayoutView.findViewById(R.id.listview_title);
            textViewAuthor = (TextView)  itemLayoutView.findViewById(R.id.listview_author);

            itemCardView.setOnClickListener(this);
            itemLayoutView.setOnClickListener(this);
        }

        public ViewHolder(View itemLayoutView, ViewHolderClicks listener) {
            super(itemLayoutView);
            mListener = listener;
            itemCardView    = (CardView)  itemLayoutView.findViewById(R.id.card_view);
            typeView       = (ImageView) itemLayoutView.findViewById(R.id.listview_type);
            textViewTitle  = (TextView)  itemLayoutView.findViewById(R.id.listview_title);
            textViewAuthor = (TextView)  itemLayoutView.findViewById(R.id.listview_author);

            itemCardView.setOnClickListener(this);
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


    public ArrayList<item> getItems(){
    	return itemsData;
    }

    public void setItems(ArrayList<item> itemsData) {
        this.itemsData = itemsData;
        this.notifyDataSetChanged();
    }
    
    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}
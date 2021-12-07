package de.badgersburrow.sciman.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.News;
import de.badgersburrow.sciman.utilities.VariousMethods;

import java.util.ArrayList;


public class MainNewsRecyclerAdapter extends RecyclerView.Adapter<MainNewsRecyclerAdapter.ViewHolder>{
    private ArrayList<News> itemsData;
    private static final String TAG = "AndroidBib";
    private static ViewGroup mParent;

    public MainNewsRecyclerAdapter(ArrayList<News> itemsData) {
        this.itemsData = itemsData;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MainNewsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.news_cardview_item, null);

        mParent = parent;


        MainNewsRecyclerAdapter.ViewHolder vh = new ViewHolder(itemLayoutView, new MainNewsRecyclerAdapter.ViewHolder.ViewHolderClicks() {
            public void onCardView(CardView callerCardView) {

           	}

            public void onMenuButton(ImageView callerImage) {

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

        viewHolder.txtViewTitle.setText(itemsData.get(position).getTitle());
        viewHolder.txtViewSubtitle.setText(itemsData.get(position).getSubtitle(MainActivity.mainActivity));

        //viewHolder.imgViewIcon.setImageBitmap(itemsData.get(position).getIconBitmap());
        viewHolder.mNewsitem = itemsData.get(position);

        View interiorCard = viewHolder.mNewsitem.getCardView(mParent, MainActivity.mainActivity);

        viewHolder.rlCardHolder.addView(interiorCard);
        //BibFragment.mainActivity.registerForContextMenu(viewHolder.menuIcon);
        viewHolder.mListener = new MainNewsRecyclerAdapter.ViewHolder.ViewHolderClicks() {

			@Override
			public void onCardView(CardView callerCardView) {
				News selectedNews = itemsData.get(posPub);


			}
            @Override
            public void onMenuButton(ImageView callerImage) {
                itemsData.remove(posPub);
                notifyItemRemoved(posPub);
                VariousMethods.saveNews(MainActivity.Newsitems, MainActivity.mainActivity);

            }
        };
    }
     
    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder  implements OnClickListener {
        
    	public CardView  newsCardView;
        public TextView  txtViewTitle;
        public TextView  txtViewSubtitle;
        public RelativeLayout rlCardHolder;
        //public RelativeLayout imgViewIcon;
        public ImageView menuIcon;
        public ViewHolderClicks mListener;
        public News mNewsitem;
         
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            newsCardView  = (CardView)  itemLayoutView.findViewById(R.id.card_view);
            txtViewTitle = (TextView)  itemLayoutView.findViewById(R.id.item_title);
            txtViewSubtitle = (TextView)  itemLayoutView.findViewById(R.id.item_subtitle);
            rlCardHolder = (RelativeLayout) itemLayoutView.findViewById(R.id.card_holder);
            //imgViewIcon  = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
            menuIcon     = (ImageView) itemLayoutView.findViewById(R.id.item_dismiss);
            menuIcon.setOnClickListener(this);

            newsCardView.setOnClickListener(this);
            // Is this needed or handled automatically by RecyclerView.ViewHolder?
            itemLayoutView.setOnClickListener(this);
        }

        public ViewHolder(View itemLayoutView, ViewHolderClicks listener) {
            super(itemLayoutView);
            mListener = listener;
            newsCardView  = (CardView)  itemLayoutView.findViewById(R.id.card_view);
            txtViewTitle = (TextView)  itemLayoutView.findViewById(R.id.item_title);
            txtViewSubtitle = (TextView)  itemLayoutView.findViewById(R.id.item_subtitle);
            rlCardHolder = (RelativeLayout) itemLayoutView.findViewById(R.id.card_holder);
            //imgViewIcon  = (ImageView) itemLayoutView.findViewById(R.id.item_icon);
            menuIcon     = (ImageView) itemLayoutView.findViewById(R.id.item_dismiss);

            menuIcon.setOnClickListener(this);
            newsCardView.setOnClickListener(this);
            // Is this needed or handled automatically by RecyclerView.ViewHolder?
            itemLayoutView.setOnClickListener(this);
        }


        public interface ViewHolderClicks {
            void onMenuButton(ImageView callerImage);
            void onCardView(CardView callerCardView);
        }

		@Override
		public void onClick(View v) {
            if (v instanceof ImageView){
                mListener.onMenuButton((ImageView) v);
            } else if (v instanceof CardView){
                mListener.onCardView((CardView) v);
            }
		}
    }

    public void add(News Newsitem){
        //News.setID(getItemCount());
        itemsData.add(Newsitem);
        this.notifyItemInserted(getItemCount() - 1);



    }

    public void setItems(ArrayList<News> newItemsData) {
        itemsData = newItemsData;
    }
    
    public ArrayList<News> getItems(){
    	return itemsData;
    }
    
    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }
}
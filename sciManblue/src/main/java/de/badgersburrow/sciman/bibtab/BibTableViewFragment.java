package de.badgersburrow.sciman.bibtab;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import de.badgersburrow.sciman.main.MainActivity;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.SuggestionsDatabase;
//import de.badgersburrow.sciman.MainActivity_new.DrawerItemClickListener;
import de.badgersburrow.sciman.objects.Bib;
import de.badgersburrow.sciman.objects.item;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import androidx.drawerlayout.widget.DrawerLayout;


public class BibTableViewFragment extends Fragment {
	 
	
	//private int numberOfItemsAfterSearch;
	
	private Activity bibViewActivity = null;
	private View rootView = null;

	String fileName 		= BibViewActivity.fileName;
    String pdfDirectory 	= BibViewActivity.pdfDirectory;
    int bibID 				= BibViewActivity.bibID;
    ArrayList<Bib> Bibitems	= MainActivity.Bibitems;
    ArrayList<item> items 	= BibViewActivity.items;
    int AnzahlItemsGesamt 	= BibViewActivity.AnzahlItemsGesamt;
    int sortAfterColumn     = BibViewActivity.sortAfterColumn;
    int sortedBeforeColumn  = BibViewActivity.sortedBeforeColumn;
    String searchText		= BibViewActivity.searchText;
    boolean search_active   = BibViewActivity.search_active;
	
		
    //private int AnzahlItems; //number of items currently viewed
    private int prefTextSize;
    private int prefHeaderTextSize;
    private Bib recentBib;
    
    
    private int width;
    private int author_width;
    private int title_width;
    private int year_width;
    private int journal_width;
    private int timestamp_width;
    private int bibtexkey_width;
    
    private String theme_color;
    private boolean isSearchtimestampEnabled;
    

    private DrawerLayout mDrawerLayout;
    private LinearLayout mDrawerLinearLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mSortFields;
    
    private SearchView mSearchView;
    private SuggestionsDatabase database;
    
    private TableLayout tl;
    
    
    

    
    
    




	public void GetRowID(item selectedItem) 
	{
	       //open the job details     
	       Intent in = new Intent(bibViewActivity, BibDetailedViewActivity.class);

	       //passing the parameters - Job ID
	       //int itemId = Integer.parseInt(Args)-1000;

	       in.putExtra("item",selectedItem);
	       BibViewActivity.selectedItem = selectedItem;
	       startActivity(in);
	       getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}
	
	
	
		
	public void createRows(ArrayList<item> itemsSorted){
		Resources res = getResources();
		final float scale = getResources().getDisplayMetrics().density;
		int tableview_horizontal_margin = (int) (res.getDimension(R.dimen.tableview_horizontal_margin)*scale + 0.5f);
		int tableview_vertical_margin = (int) (res.getDimension(R.dimen.tableview_vertical_margin)*scale + 0.5f);
		

		if (itemsSorted.size()==0){
			TextView tv_noItem = (TextView)rootView.findViewById(R.id.tv_noitem);
			tv_noItem.setText(getString(R.string.noitem));
			
			final TableRow tr = new TableRow(bibViewActivity);
        	tr.setId(10001);
            tr.setLayoutParams(new LayoutParams(
            			android.view.ViewGroup.LayoutParams.FILL_PARENT, 
            			android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            
            Paint paint = new Paint();
            final float densityMultiplier = 1.2f*bibViewActivity.getBaseContext().getResources().getDisplayMetrics().density;
            String emptyfield= getString(R.string.emptyfield);
            
            TextView tv_Author = new TextView(bibViewActivity);
        	tv_Author.setId(20000);
        	tv_Author.setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefTextSize);
        	tv_Author.setLines(1);
        	tv_Author.setPadding(tableview_horizontal_margin,tableview_vertical_margin,tableview_horizontal_margin,tableview_vertical_margin);
        	tv_Author.setText(emptyfield);
        	
        	
        		tv_Author.setLayoutParams(new LayoutParams(
            			author_width,//130,//LayoutParams.FILL_PARENT,
            			android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
        	
        	tr.addView(tv_Author);
        	
        	TextView tv_Title = new TextView(bibViewActivity);
        	tv_Title.setId(30000);
        	tv_Title.setLines(1);
        	tv_Title.setPadding(tableview_horizontal_margin,tableview_vertical_margin,tableview_horizontal_margin,tableview_vertical_margin);
        	tv_Title.setText(emptyfield);
        	
        	tv_Title.setLayoutParams(new LayoutParams(
            			title_width,//130,//LayoutParams.FILL_PARENT,
            			android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
        	
        	tr.addView(tv_Title);
        	
            TextView tv_Year = new TextView(bibViewActivity);
            tv_Year.setId(40000);
            tv_Year.setLines(1);
            tv_Year.setPadding(tableview_horizontal_margin,tableview_vertical_margin,tableview_horizontal_margin,tableview_vertical_margin);
            tv_Year.setText(emptyfield);
            tv_Year.setLayoutParams(new LayoutParams(
            		year_width,//LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        	tr.addView(tv_Year);
            
        	TextView tv_Journal = new TextView(bibViewActivity);
        	tv_Journal.setId(50000);
        	tv_Journal.setLines(1);
        	tv_Journal.setPadding(tableview_horizontal_margin,tableview_vertical_margin,tableview_horizontal_margin,tableview_vertical_margin);
        	tv_Journal.setText(emptyfield);
        	tv_Journal.setLayoutParams(new LayoutParams(
            			journal_width,//130,//LayoutParams.FILL_PARENT,
            			android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
        	
        	tr.addView(tv_Journal);
        	
        	//if (isSearchtimestampEnabled){
        		TextView tv_Timestamp = new TextView(bibViewActivity);
        		tv_Timestamp.setId(70000);
        		tv_Timestamp.setLines(1);
        		tv_Timestamp.setPadding(tableview_horizontal_margin,tableview_vertical_margin,tableview_horizontal_margin,tableview_vertical_margin);
        		tv_Timestamp.setText(emptyfield);
        		tv_Timestamp.setLayoutParams(new LayoutParams(
                			timestamp_width,//130,//LayoutParams.FILL_PARENT,
                			android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
            	
            	tr.addView(tv_Timestamp);
        	//}
        	
        	TextView tv_Bibtexkey = new TextView(bibViewActivity);
        	tv_Bibtexkey.setId(60000);
        	tv_Bibtexkey.setLines(1);
        	tv_Bibtexkey.setPadding(tableview_horizontal_margin,tableview_vertical_margin,tableview_horizontal_margin,tableview_vertical_margin);
        	tv_Bibtexkey.setText(emptyfield);
        	tv_Bibtexkey.setLayoutParams(new LayoutParams(
                    bibtexkey_width,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        	tr.addView(tv_Bibtexkey);
        	
        	
        	tl.addView(tr,new TableLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.FILL_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		} else{
			int currentAnzahlItems=0;

			boolean lightBack = false;





			for (int j = 0; j < itemsSorted.size(); j++) {


					final TableRow tr = new TableRow(bibViewActivity);
					tr.setId(10000 + itemsSorted.get(j).getId());
					tr.setLayoutParams(new LayoutParams(
								android.view.ViewGroup.LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
					//show abstract or overview like in jabref
					tr.setClickable(true);
					//registerForContextMenu(tr);
					//Selector light = android.R.drawable.list_selector_background;
					tr.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.list_selector_background));


					 //tr.drawable.list_selector_background);


					tr.setOnClickListener(new View.OnClickListener() {

							//@Override
							@Override
							public void onClick(View v) {
							   int rowid;
							   rowid = v.getId();
							   //String args = Integer.toString(rowid);
							   item selectedItem = items.get(rowid-10000);
							   GetRowID(selectedItem);

							}
						 });


					//i could check if text is smaller then maximum width and then use fill_parent
					Paint paint = new Paint();
					final float densityMultiplier = 1.2f*bibViewActivity.getBaseContext().getResources().getDisplayMetrics().density;


					TextView tv_Author = new TextView(bibViewActivity);
					tv_Author.setId(20000+j);
					tv_Author.setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefTextSize);
					tv_Author.setLines(1);
					tv_Author.setPadding(tableview_horizontal_margin,tableview_vertical_margin,tableview_horizontal_margin,tableview_vertical_margin);
					tv_Author.setText(itemsSorted.get(j).getAuthor());

					if (paint.measureText(itemsSorted.get(j).getAuthor())*densityMultiplier<author_width){
						tv_Author.setLayoutParams(new LayoutParams(
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT,//130,//LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
					} else {
						tv_Author.setLayoutParams(new LayoutParams(
								author_width,//130,//LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
					}
					tr.addView(tv_Author);


					TextView tv_Title = new TextView(bibViewActivity);
					tv_Title.setId(30000+j);
					tv_Title.setLines(1);
					tv_Title.setPadding(tableview_horizontal_margin,tableview_vertical_margin,tableview_horizontal_margin,tableview_vertical_margin);
					tv_Title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefTextSize);
					tv_Title.setText(itemsSorted.get(j).getTitle());

					if (paint.measureText(itemsSorted.get(j).getTitle())*densityMultiplier<title_width){
						tv_Title.setLayoutParams(new LayoutParams(
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT,//130,//LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
					} else {
						tv_Title.setLayoutParams(new LayoutParams(
								title_width,//130,//LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
					}
					tr.addView(tv_Title);

					TextView tv_Year = new TextView(bibViewActivity);
					tv_Year.setId(40000+j);
					tv_Year.setLines(1);
					tv_Year.setPadding(tableview_horizontal_margin,tableview_vertical_margin,tableview_horizontal_margin,tableview_vertical_margin);
					tv_Year.setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefTextSize);
					tv_Year.setText(itemsSorted.get(j).getYear());

					if (paint.measureText(itemsSorted.get(j).getYear())*densityMultiplier<year_width){
						tv_Year.setLayoutParams(new LayoutParams(
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT,//130,//LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
					} else {
						tv_Year.setLayoutParams(new LayoutParams(
								year_width,//130,//LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
					}

					tr.addView(tv_Year);

					TextView tv_Journal = new TextView(bibViewActivity);
					tv_Journal.setId(50000+j);
					tv_Journal.setLines(1);
					tv_Journal.setPadding(tableview_horizontal_margin,tableview_vertical_margin,tableview_horizontal_margin,tableview_vertical_margin);
					tv_Journal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefTextSize);
					tv_Journal.setText(itemsSorted.get(j).getJournal());
					if (paint.measureText(itemsSorted.get(j).getJournal())*densityMultiplier<journal_width){
						tv_Journal.setLayoutParams(new LayoutParams(
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT,//130,//LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
					} else {
						tv_Journal.setLayoutParams(new LayoutParams(
								journal_width,//130,//LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
					}
					tr.addView(tv_Journal);

					TextView tv_Timestamp = new TextView(bibViewActivity);
					tv_Timestamp.setId(70000+j);
					tv_Timestamp.setLines(1);
					tv_Timestamp.setPadding(tableview_horizontal_margin,tableview_vertical_margin,tableview_horizontal_margin,tableview_vertical_margin);
					tv_Timestamp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefTextSize);
					tv_Timestamp.setText(itemsSorted.get(j).getTimestamp());
					if (paint.measureText(itemsSorted.get(j).getTimestamp())*densityMultiplier<timestamp_width){
						tv_Timestamp.setLayoutParams(new LayoutParams(
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT,//130,//LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
					} else {
						tv_Timestamp.setLayoutParams(new LayoutParams(
								timestamp_width,//130,//LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
					}
					tr.addView(tv_Timestamp);

					TextView tv_Bibtexkey = new TextView(bibViewActivity);
					tv_Bibtexkey.setId(60000+j);
					tv_Bibtexkey.setLines(1);
					tv_Bibtexkey.setPadding(tableview_horizontal_margin,tableview_vertical_margin,tableview_horizontal_margin,tableview_vertical_margin);
					tv_Bibtexkey.setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefTextSize);
					tv_Bibtexkey.setText(itemsSorted.get(j).getBibtexkey());

					if (paint.measureText(itemsSorted.get(j).getBibtexkey())*densityMultiplier<bibtexkey_width){
						tv_Bibtexkey.setLayoutParams(new LayoutParams(
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT,//130,//LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
					} else {
						tv_Bibtexkey.setLayoutParams(new LayoutParams(
								bibtexkey_width,//130,//LayoutParams.FILL_PARENT,
								android.view.ViewGroup.LayoutParams.WRAP_CONTENT));//LayoutParams.WRAP_CONTENT));
					}

					tr.addView(tv_Bibtexkey);



					if (lightBack){
						tv_Author.setBackgroundResource(R.color.transgrey);
						tv_Title.setBackgroundResource(R.color.transgrey);
						tv_Year.setBackgroundResource(R.color.transgrey);
						tv_Journal.setBackgroundResource(R.color.transgrey);
						tv_Bibtexkey.setBackgroundResource(R.color.transgrey);
						tv_Timestamp.setBackgroundResource(R.color.transgrey);
						//tr.setBackgroundDrawable(getResources().getDrawable(R.drawable.list_selector_background_light));
						lightBack=false;
					}else{

						lightBack=true;
					}


					//tl.addView(tr);
					/* Add row to TableLayout. */


					tl.addView(tr,new TableLayout.LayoutParams(
							android.view.ViewGroup.LayoutParams.FILL_PARENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
					//itemsSorted[countItems]=itemsSorted[j];
					//countItems++;
			}
		}
	}

	
    public void saveBibItems(Bib[] Bibitems){
        //final Bib[] Bibitemsdefault = new Bib[100];

        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = bibViewActivity.openFileOutput("androidbib.cfg", Context.MODE_PRIVATE);
            out = new ObjectOutputStream(fos);
            out.writeObject(Bibitems);
            out.close();
            System.out.println("Object Persisted");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
	 
	 
	public void clearTable(){
		try{
			TableRow tr = (TableRow)rootView.findViewById(10001);
			((ViewManager)tr.getParent()).removeView(tr);
		} catch (RuntimeException e){
			
		}
		for(item singleItem : items) {
			try{
				TableRow tr = (TableRow)rootView.findViewById(10000+singleItem.getId());
				((ViewManager)tr.getParent()).removeView(tr);
			} catch (RuntimeException e){
				
			}
		}
	}

    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	bibViewActivity = getActivity();
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(bibViewActivity);
        theme_color = sp.getString("pref_color_theme_entries","Black");

        super.onCreate(savedInstanceState);  
        ScrollView sv = new ScrollView(bibViewActivity);

        rootView = inflater.inflate(R.layout.bib_tableview_fragment,container,false);

        boolean isSavesortsearchEnabled = sp.getBoolean("isSavesortsearchEnabled", false);
        if (isSavesortsearchEnabled){
            sortedBeforeColumn = Bibitems.get(bibID).getSortColumn();
            searchText = Bibitems.get(bibID).getSearchText();	            
        }

        //String searchText = extras.getString(key)
        double scalewidth=(sp.getInt("prefBibwidth", 130))*1.0/100.0;
        Display display = bibViewActivity.getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        author_width=(int)Math.round(width*scalewidth*0.2);
        title_width=(int)Math.round(width*scalewidth*0.6);
        year_width=(int)Math.round(width*scalewidth*0.1);//68;
        journal_width=(int)Math.round(width*scalewidth*0.15);
        timestamp_width=(int)Math.round(width*scalewidth*0.25);
        bibtexkey_width=(int)Math.round(width*scalewidth*0.25);

        /* Find Tablelayout defined in main.xml */
        tl = (TableLayout)rootView.findViewById(R.id.BodyTable);
                    
        prefTextSize = sp.getInt("prefTextSize", 14);
        prefHeaderTextSize = prefTextSize + sp.getInt("prefHeaderTextSize", 2);
        
        TableLayout header_row = (TableLayout)rootView.findViewById(R.id.HeaderTable);
        header_row.setLayoutParams(new LayoutParams(
    			android.view.ViewGroup.LayoutParams.MATCH_PARENT,
    			android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        
        TextView header_author = (TextView)rootView.findViewById(R.id.header_author);
        header_author.setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefHeaderTextSize);
        TextView header_title = (TextView)rootView.findViewById(R.id.header_title);
        header_title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefHeaderTextSize);
        TextView header_year = (TextView)rootView.findViewById(R.id.header_year);
        header_year.setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefHeaderTextSize);
        TextView header_journal = (TextView)rootView.findViewById(R.id.header_journal);
        header_journal.setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefHeaderTextSize);
        TextView header_timestamp = (TextView)rootView.findViewById(R.id.header_timestamp);
        header_timestamp.setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefHeaderTextSize);
        TextView header_bibtexkey = (TextView)rootView.findViewById(R.id.header_bibtexkey);
        header_bibtexkey.setTextSize(TypedValue.COMPLEX_UNIT_DIP, prefHeaderTextSize);
        
       
        createRows(items);

		search_active = !searchText.equalsIgnoreCase("");

            
            
        
		return rootView;
    } 


	@Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {      		
       super.onSaveInstanceState(savedInstanceState);
    }

  
}
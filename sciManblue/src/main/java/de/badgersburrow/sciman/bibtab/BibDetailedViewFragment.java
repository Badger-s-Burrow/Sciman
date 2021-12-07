package de.badgersburrow.sciman.bibtab;

import java.io.File;

import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.item;
import de.badgersburrow.sciman.utilities.TextViewOverflowing;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;


public class BibDetailedViewFragment extends Fragment {
	
	private Activity bibViewActivity = null;
	private View rootView = null;
	private item selectedItem = BibViewActivity.selectedItem;
	private int prefTextSize;
	private boolean isViewpdffileEnabled;
	private boolean isViewdoilinkEnabled;
	
	static String DOILink = null;
	/**public void openPdf(String path) 
	{
		//final List<ResolveInfo> mList;
	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("path-to-document"));
	intent.setType("application/pdf");
	PackageManager pm = getPackageManager();
	List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
	if (activities.size() > 0) {
	    startActivity(intent);
	} else {
	    // Do something else here. Maybe pop up a Dialog or Toast
	}
	}**/
	
	
		
    /** Called when the activity is first created. */
	
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	bibViewActivity = getActivity();
    	SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(bibViewActivity);
        String theme_color = sp.getString("pref_color_theme_entries","Black");
        /*if (theme_color.equalsIgnoreCase("White")){
        	setTheme(android.R.style.Theme_Light_NoTitleBar);
        }*/
        super.onCreate(savedInstanceState); 
        
        
        // Obtain the sharedPreference, default to true if not available
        isViewpdffileEnabled = sp.getBoolean("isViewpdffileEnabled", false);
        isViewdoilinkEnabled = sp.getBoolean("isViewdoilinkEnabled", false);
        boolean isAutotextsizeEnabled = sp.getBoolean("isAutotextsizeEnabled", true);
        prefTextSize = 14;
        if (isAutotextsizeEnabled){
        	prefTextSize = 14;
        }
        else{
        	prefTextSize = sp.getInt("prefTextSize", 10);
        }
        /*if (theme_color.equalsIgnoreCase("White")){
        	setContentView(R.layout.detailedview_white);
        } else {
        	setContentView(R.layout.detailedview);
        }*/
        
        Activity parentActivity = getActivity();
        if(parentActivity instanceof BibDetailedViewActivity) {
        	rootView = inflater.inflate(R.layout.bib_detailedview_styled_fragment,container,false);
        	
        } else if (parentActivity instanceof BibViewActivity) {
        	rootView = inflater.inflate(R.layout.bib_detailedview_styled_fragment,container,false);
        }
        
        
        setContent(selectedItem);
        
        //Intent  in = super.getIntent();
        //Bundle b = in.getExtras();

        //item ide = b.getString("itemID");
        //Intent in = super.getIntent();
        //final item selectedItem = (item)in.getSerializableExtra("item");

        //TextView tv = (TextView)findViewById(R.id.text_view_text2);
        //tv.setText(selectedItem.getAuthor());
        //LinearLayout dv_ll = (LinearLayout)findViewById(R.id.dv_linearlayout);
        return rootView;
    }
    
    public void setContent(item selectedItemextern){
    	selectedItem = selectedItemextern;


        TextViewOverflowing tv_author = (TextViewOverflowing) rootView.findViewById(R.id.tv_author);
        TextViewOverflowing tv_title = (TextViewOverflowing) rootView.findViewById(R.id.tv_title);
		TextView tv_year = (TextView) rootView.findViewById(R.id.tv_bibtexkey);
        TextViewOverflowing tv_journal = (TextViewOverflowing) rootView.findViewById(R.id.tv_journal);
        TextViewOverflowing tv_abstract = (TextViewOverflowing) rootView.findViewById(R.id.tv_abstract);
		TextView tv_bibtexkey = (TextView) rootView.findViewById(R.id.tv_bibtexkey);
        TextView tv_doi = (TextView) rootView.findViewById(R.id.tv_doi);

        tv_author.setText(selectedItem.getAuthor());
        tv_author.setOverflowTextViewId(R.id.tv_author_overflow);

        tv_title.setText(selectedItem.getTitle());
        tv_title.setOverflowTextViewId(R.id.tv_title_overflow);

        tv_year.setText(selectedItem.getYear());

        tv_journal.setText(selectedItem.getJournal());
        tv_journal.setOverflowTextViewId(R.id.tv_journal_overflow);

        tv_abstract.setText(selectedItem.getAbstract());
        tv_abstract.setOverflowTextViewId(R.id.tv_abstract_overflow);

        tv_bibtexkey.setText(selectedItem.getBibtexkey());

        //tv_doi.setText(getString(R.string.dv_bibtexkey) + selectedItem.getDoi());

		
		if (selectedItem.getDoi().contains("/")){
			DOILink = "http://dx.doi.org/" + selectedItem.getDoi();
			tv_doi.setText(Html.fromHtml("<a href=\'" + DOILink + "\'>" + selectedItem.getDoi() + "</a>"));
			tv_doi.setMovementMethod(LinkMovementMethod.getInstance());
		} else {
			tv_doi.setText(selectedItem.getDoi());
		}
        /*
    	if (isViewdoilinkEnabled){
            tv_doi.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.FILL_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            lp_Doi.addRule(RelativeLayout.RIGHT_OF, tv_Doi_space.getId());
        	lp_Doi.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        	lp_Doi_space.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        	lp_Doi_space.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        	lp_Doi.leftMargin = leftMarginToText;        	
        	dv_lldoi.addView(tv_Doi_space,lp_Doi_space);
        	dv_lldoi.addView(tv_Doi,lp_Doi);
    	}
    	
    	if (isViewpdffileEnabled){
    		tempwidth = paint.measureText(getResources().getString(R.string.dv_file));
            
            preString= "";
            for (;paint.measureText(preString)<(tempwidth-leftMarginToText+prefTextSizePlus)*divisor;){
            	preString+=" ";
            }
            preString+=suffix;
    		TextView tv_File_header = new TextView(bibViewActivity);
    		tv_File_header.setTextSize(prefTextSize);
    		tv_File_header.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
    		tv_File_header.setText(getString(R.string.dv_file));
    		tv_File_header.setTextColor(getResources().getColor(R.color.white));
    		dv_llfile.addView(tv_File_header);	
    		
    		TextView tv_File = new TextView(bibViewActivity);
    		tv_File.setTextSize(prefTextSize);
        	tv_File.setText(preString + selectedItem.getFile());
        	tv_File.setLayoutParams(new LayoutParams(
                    android.view.ViewGroup.LayoutParams.FILL_PARENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
        	lp_File.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        	lp_File.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        	lp_File.leftMargin = leftMarginToText; 
        	dv_llfile.addView(tv_File,lp_File);
    	}*/
    	
    	
    	
    	LinearLayout dv_llbuttons = (LinearLayout)rootView.findViewById(R.id.dv_llbuttons);
    	TextView tv_space = new TextView(bibViewActivity);
    	tv_space.setLayoutParams(new LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
    	tv_space.setText("   ");
    	
    	Button buttonDOI = new Button(bibViewActivity);
    	buttonDOI.setTextSize(prefTextSize);
    	buttonDOI.setText(R.string.bt_opendoi);
    	buttonDOI.setClickable(true);
    	buttonDOI.setCompoundDrawablePadding(20);
    	buttonDOI.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.doi,0);
    	buttonDOI.setLayoutParams(new LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
    	
    	if (selectedItem.getDoi().contains("/")){
    		dv_llbuttons.addView(buttonDOI);
    	 }
    	//button.setText(this.getString(R.string.bt_viewpdf));
    	 buttonDOI.setOnClickListener(new View.OnClickListener() {
            //@Override
            @Override
			public void onClick(View v) {
            
            	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(DOILink));
            	startActivity(browserIntent);
            }
        });
    	
    	
    	Button buttonPDF = new Button(bibViewActivity);
    	buttonPDF.setTextSize(prefTextSize);
    	buttonPDF.setText(R.string.bt_viewpdf);
    	buttonPDF.setClickable(true);
    	buttonPDF.setCompoundDrawablePadding(20);
    	buttonPDF.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.adobe_pdf_icon,0);
    	buttonPDF.setLayoutParams(new LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
    	File file = new File(selectedItem.getFile());
    	 if (file.exists()) {
    		 dv_llbuttons.addView(buttonPDF);
    	 }
    	//button.setText(this.getString(R.string.bt_viewpdf));
    	 buttonPDF.setOnClickListener(new View.OnClickListener() {
            //@Override
            @Override
			public void onClick(View v) {
            
                File file = new File(selectedItem.getFile());

                if (file.exists()) {
                	
                    Uri path = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    try {
                        startActivity(intent);
                    } 
                    catch (ActivityNotFoundException e) {
                        Toast.makeText(bibViewActivity, 
                        		getString(R.string.dv_nopdfapp), 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                	Toast.makeText(bibViewActivity, 
                			getString(R.string.dv_nopdf), Toast.LENGTH_SHORT).show();
                }
            }
        });
       // LinearLayout dv_linearlayout = (LinearLayout)findViewById(R.id.dv_linearlayout);
       
        //dv_linearlayout.addView(button);


      
      
    }  
    
    public void test(){
    	Toast.makeText(bibViewActivity,"aha", Toast.LENGTH_SHORT).show();
    }
}
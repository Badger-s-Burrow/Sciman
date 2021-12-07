package de.badgersburrow.sciman.objects;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.bibtab.BibFragment;
import de.badgersburrow.sciman.conftab.ConfFragment;
import de.badgersburrow.sciman.utilities.VariousMethods;


import java.text.DateFormat;
import java.util.Calendar;
import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.Map;

@SuppressWarnings("serial") //with this annotation we are going to hide compiler warning
public class News implements Serializable{

    private String action; //either "added" or "updated"
	private GregorianCalendar date;
	private Bib biblio;
	private Conference conf;

	public News(Bib Biblio, String Action){
        this.date   = new GregorianCalendar();
		this.biblio = Biblio;
        this.conf   = null;
        this.action = Action;
	}

	public News(Conference Conf, String Action){
        this.date   = new GregorianCalendar();
        this.biblio = null;
		this.conf   = Conf;
        this.action = Action;
	}

	public String getTitle(){
        if (this.biblio != null){

            return "Bibliography";
        } else if (this.conf != null){
            return "Conference";
        } else {
            return "";
        }

    }

    public String getSubtitle(Activity act){
        /*if (this.biblio != null){

            return "SubtitleBib";
        } else if (this.conf != null){
            return "SubtitleConf";
        } else {
            return "";
        }*/
        String subtitle;
        if (this.action.equalsIgnoreCase("added")){
            subtitle = act. getResources().getString(R.string.news_added);

        } else if (this.action.equalsIgnoreCase("updated")){
            subtitle = act. getResources().getString(R.string.news_updated);
        } else {
            subtitle = "";
        }
        String dateStr = DateFormat.getDateInstance(DateFormat.SHORT).format(this.date.getTime());
        subtitle += dateStr;
        return subtitle;

    }

    public Bitmap getIcon(){
        if (this.biblio != null){
            return this.biblio.getIconBitmap();
        } else if (this.conf != null){
            return this.conf.getIconBitmap();
        } else {
            return null;
        }
    }

    public String getType(){
        if (this.biblio != null){
            return "bib";
        } else if (this.conf != null){
            return "conf";
        } else {
            return null;
        }
    }

    public String getAction(){
        return this.action;
    }


    public View getCardView(ViewGroup parent, final Activity act){

        if (this.biblio != null){
            View card = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bib_cardview_item, null);
            TextView txtViewTitle = (TextView)  card.findViewById(R.id.item_title);
            ImageView imgViewIcon  = (ImageView) card.findViewById(R.id.item_icon);
            ImageView menuIcon     = (ImageView) card.findViewById(R.id.item_menu);
            txtViewTitle.setText(this.biblio.getBibname());
            Map<String, Integer> topicsDict = VariousMethods.getTopicDict(act);
            int resID = topicsDict.get(this.biblio.getIconKey());
            imgViewIcon.setImageResource(resID);
            //imgViewIcon.setImageBitmap(this.biblio.getIconBitmap());

            menuIcon.setVisibility(View.GONE);
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BibFragment.OpenBib(biblio,act);
                }
            });
            return card;
        } else if (this.conf != null){
            View card = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bib_cardview_item, null);
            TextView txtViewTitle = (TextView)  card.findViewById(R.id.item_title);
            ImageView imgViewIcon  = (ImageView) card.findViewById(R.id.item_icon);
            ImageView menuIcon     = (ImageView) card.findViewById(R.id.item_menu);
            txtViewTitle.setText(this.conf.getConfname());
            imgViewIcon.setImageBitmap(this.conf.getIconBitmap());
            menuIcon.setVisibility(View.GONE);

            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfFragment.OpenConf(conf,act);

                }
            });
            return card;
        } else {
            return null;
        }


    }
	

    
}

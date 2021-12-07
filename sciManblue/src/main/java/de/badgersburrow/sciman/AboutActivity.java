package de.badgersburrow.sciman;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import android.view.MenuItem;
import android.widget.Toolbar;


public class AboutActivity extends AppCompatActivity {
	
	public void onCreate(Bundle savedInstanceState) {  
		super.onCreate(savedInstanceState);
       	setContentView(R.layout.about_view_activity);

        Toolbar toolbar = (Toolbar) findViewById(de.badgersburrow.sciman.R.id.about_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        PackageInfo pinfo;
        int versionNumber = 0;
        String versionName = "";
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionNumber = pinfo.versionCode;
	        versionName = pinfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TextView apptitle = (TextView)findViewById(R.id.Apptitle);
		apptitle.setText(getString(R.string.app_name));
        TextView version = (TextView)findViewById(R.id.version);
        version.setText(versionName);
        
        TextView tv_aboutmain = (TextView)findViewById(R.id.tv_aboutmain);
        Spanned spanner =  Html.fromHtml(getString(R.string.about_main1) + "<a href='http://iffwww.iff.kfa-juelich.de/~reim/AndroidBib/index.html\'>" + getString(R.string.about_homepage) + "</a>"+ getString(R.string.about_main2));
        tv_aboutmain.setText( spanner );
        tv_aboutmain.setMovementMethod(LinkMovementMethod.getInstance());


	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();

        }
        return true;
    }
}
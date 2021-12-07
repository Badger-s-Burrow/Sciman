package de.badgersburrow.sciman;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MyFragment extends Fragment implements FragmentUpdate
{

	 @Override
	    public void onActivityCreated(Bundle savedInstanceState) {
	    super.onActivityCreated(savedInstanceState);
	    }
        

        
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View rootView = inflater.inflate(R.layout.my_main_fragment,container,false);
			TextView dummyTextView = (TextView) rootView
				.findViewById(R.id.section_label);
		dummyTextView.setText("My");
		return rootView;

	}




	@Override
	public void update() {
		// TODO Auto-generated method stub
		
	}
}
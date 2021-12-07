package de.badgersburrow.sciman.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import de.badgersburrow.sciman.FragmentUpdate;
import de.badgersburrow.sciman.R;

/**
 * Created by reim on 23.06.15.
 */
public class MainContactsFragment extends Fragment implements FragmentUpdate {



    static FragmentActivity mainActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_contacts_fragment,container,false);
        mainActivity = getActivity();



        return rootView;
    }




    @Override
    public void update() {
        // TODO Auto-generated method stub

    }
}


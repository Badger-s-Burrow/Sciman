package de.badgersburrow.sciman.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.badgersburrow.sciman.FragmentUpdate;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.News;
import de.badgersburrow.sciman.utilities.VariousMethods;

/**
 * Created by reim on 23.06.15.
 */
public class MainNewsFragment extends Fragment implements FragmentUpdate {



    static FragmentActivity mainActivity;
    public RecyclerView mRecyclerView;
    public static MainNewsRecyclerAdapter mRecyclerAdapter;
    public static News selectedNews;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_news_fragment,container,false);
        mainActivity = getActivity();

        // 1. get a reference to recyclerView
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        // 2. set layoutManger
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
        readNewsItems();
        // 3. create an adapter
        mRecyclerAdapter = new MainNewsRecyclerAdapter(MainActivity.Newsitems);
        registerForContextMenu(mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        // 5. set item animator to DefaultAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


        return rootView;
    }

    public static void readNewsItems(){
        if (MainActivity.Newsitems.isEmpty()) {
            MainActivity.Newsitems = VariousMethods.readNews(mainActivity);
        }
    }


    @Override
    public void update() {
        mRecyclerAdapter.notifyDataSetChanged();
        //mRecyclerAdapter.setItems(MainActivity.Newsitems);

    }
}


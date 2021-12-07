package de.badgersburrow.sciman.conftab;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import de.badgersburrow.sciman.main.MainActivity;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.Conference;
import de.badgersburrow.sciman.objects.WebsocketRequest;
import de.badgersburrow.sciman.objects.WebsocketReturn;

import java.util.ArrayList;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

/**
 * Created by reim on 27.06.15.
 */
public class ConfSelectActivity extends AppCompatActivity {

    public static ConfRecyclerView mRecyclerView;
    public static GridLayoutManager mLayoutManager;
    public static ConfSelectRecyclerAdapter mRecyclerAdapter;

    String confListString = "confListString";
    String confIdentifierString = "confIdentifierString";
    static ArrayList<Conference> confList;
    static ArrayList<String> confIdentifierList;
    private final static WebSocketConnection mConnection = new WebSocketConnection();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private static boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 1; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;
    static int previousVisibleItemCount = -5;
    private static int current_page = 0;
    int pastVisibleItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conf_select_activity);

        Intent intentFromMenu = super.getIntent();//
        Bundle extras = intentFromMenu.getExtras();
        confIdentifierList = (ArrayList<String>)intentFromMenu.getSerializableExtra("confList");

        confList = new ArrayList<Conference>();
        Toolbar toolbar = (Toolbar) findViewById(de.badgersburrow.sciman.R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // 1. get a reference to recyclerView
        mRecyclerView = (ConfRecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        // 2. set layoutManger
        //mRecyclerView.setLayoutManager(new GridLayoutManager(ConfFragment.mainActivity,5));

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            confList = (ArrayList<Conference>) savedInstanceState.getSerializable(confListString);
            confIdentifierList = savedInstanceState.getStringArrayList(confIdentifierString);

        }
        mRecyclerAdapter = new ConfSelectRecyclerAdapter(confList);
        registerForContextMenu(mRecyclerView);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        // 5. set item animator to DefaultAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        previousVisibleItemCount = -5;
        current_page = 0;

        mLayoutManager = mRecyclerView.getLayoutManager();
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                //(totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)
                //if (!loading && (totalItemCount<confIdentifierList.size()) && (totalItemCount - visibleItemCount < firstVisibleItem+mLayoutManager.getSpanCount()*current_page)) {
                if (!loading && (totalItemCount<confIdentifierList.size())) {
                    // End has been reached
                    // Do something
                    current_page+=2;
                    loading = true;
                    pollServer(MainActivity.requestIdGetConf,confIdentifierList.get(mLayoutManager.getItemCount()));
                    /*String reason = "getConf";
                    ArrayList<String> arguments = new ArrayList<String>();
                    arguments.add(confIdentifierList.get(totalItemCount));
                    WebsocketRequest request = new WebsocketRequest(MainActivity.appId, MainActivity.userId, MainActivity.deviceId, reason, arguments);

                    mConnection.sendTextMessage(request.getGson());*/
                }
            }
        });

        if (savedInstanceState == null){
            //mRecyclerAdapter = new ConfSelectRecyclerAdapter(confList);
            pollServer(MainActivity.requestIdGetConf,confIdentifierList.get(0));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mConnection.disconnect();
                super.onBackPressed();

        }
        return true;
    }

    @Override
    public void onBackPressed() {
        mConnection.disconnect();
        super.onBackPressed();
    }

    // maybe even switch to ArrayList<String> instead of single argument
    public final static void pollServer(final int requestId, final String argument) {

        try {
            mConnection.connect(MainActivity.wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(MainActivity.TAG, "Status: Connected to " + MainActivity.wsuri);
                    ArrayList<String> arguments = new ArrayList<String>();
                    switch (requestId) {
                        case MainActivity.requestIdGetConf: {
                            // argument is confIdentifier
                            String reason = "getConf";

                            arguments.add(argument);
                            WebsocketRequest request = new WebsocketRequest(MainActivity.appId, MainActivity.userId, MainActivity.deviceId, reason, arguments);

                            mConnection.sendTextMessage(request.getGson());
                            break;
                        }
                        default: {

                            break;
                        }
                    }
                }

                @Override
                public void onTextMessage(String payload) {
                    Log.d(MainActivity.TAG, "Got echo: " + payload);
                    Gson gson = new Gson();
                    WebsocketReturn websocketReturn = gson.fromJson(payload, WebsocketReturn.class);
                    switch (websocketReturn.getReturnType()) {
                        case MainActivity.requestIdGetConf: {
                            Log.d(MainActivity.TAG, "reason: getConf");
                            Conference Conf = websocketReturn.getConference();
                            mRecyclerAdapter.add(Conf);
                            int spanCount = mLayoutManager.getSpanCount();
                            // + current_page*spanCount ;
                            int totalItemCount = mLayoutManager.getItemCount();
                            int invisibleItemsTop = mLayoutManager.findFirstVisibleItemPosition();
                            int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
                            int visibleItemCount = mRecyclerView.getChildCount();

                            //if ((totalItemCount<confIdentifierList.size()) && (totalItemCount - visibleItemCount < firstVisibleItem+spanCount*current_page)){
                            if ((totalItemCount<confIdentifierList.size()) && (previousVisibleItemCount < visibleItemCount + invisibleItemsTop)){
                                String reason = "getConf";
                                ArrayList<String> arguments = new ArrayList<String>();
                                arguments.add(confIdentifierList.get(totalItemCount));
                                WebsocketRequest request = new WebsocketRequest(MainActivity.appId, MainActivity.userId, MainActivity.deviceId, reason, arguments);

                                mConnection.sendTextMessage(request.getGson());
                                previousVisibleItemCount = visibleItemCount + invisibleItemsTop;
                            }
                            else{
                                mConnection.disconnect();
                                loading =false;
                            }
                            break;
                        }
                        default: {
                            Log.d(MainActivity.TAG, "Invalid reason!");
                            break;
                        }
                    }
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(MainActivity.TAG, "Connection lost.");
                    loading =false;
                }
            });
        } catch (WebSocketException e) {

            Log.d(MainActivity.TAG, e.toString());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(confListString, confList);
        savedInstanceState.putStringArrayList(confIdentifierString, confIdentifierList);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}

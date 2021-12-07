package de.badgersburrow.sciman.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.badgersburrow.sciman.FragmentUpdate;
import de.badgersburrow.sciman.R;

/**
 * Created by reim on 23.06.15.
 */
public class MainProfileFragment extends Fragment implements FragmentUpdate {



    static FragmentActivity mainActivity;
    Button btnLogin;
    TextView tvStatus;
    TextView tvUserName;
    TextView tvUserEmail;
    SharedPreferences.OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_profile_fragment, container, false);
        mainActivity = getActivity();

        SharedPreferences userDetails = mainActivity.getSharedPreferences(MainActivity.sharedPrefUserDetails, mainActivity.MODE_PRIVATE);
        String userPasswordHash = userDetails.getString(MainActivity.sharedPrefUserDetailsPasswordHash, null);

        btnLogin = (Button) rootView.findViewById(R.id.btn_loginout);
        tvStatus = (TextView) rootView.findViewById(R.id.tv_status);
        tvUserName = (TextView) rootView.findViewById(R.id.tv_username);
        tvUserEmail = (TextView) rootView.findViewById(R.id.tv_useremail);

        if (userPasswordHash.equals(MainActivity.defaultPasswordHash)){
            setStateLoggedOut();

        } else {
            setStateLoggedIn();
        }

        mOnSharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals(MainActivity.sharedPrefUserDetailsPasswordHash)){
                    String userPasswordHash = sharedPreferences.getString(MainActivity.sharedPrefUserDetailsPasswordHash, null);
                    if (userPasswordHash.equals(MainActivity.defaultPasswordHash)){
                        setStateLoggedOut();
                    } else {
                        setStateLoggedIn();
                    }

                }
            }
        };
        userDetails.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);


        return rootView;
    }


    public void setStateLoggedOut(){
        btnLogin.setText("Login");
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, LoginActivity.class);
                startActivity(intent);
            }
        });

        tvStatus.setText("You are currently not logged in");
        tvUserName.setText(null);
        tvUserEmail.setText(null);
    }

    public void setStateLoggedIn(){
        btnLogin.setText("Logout");
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences prefs = mainActivity.getSharedPreferences(MainActivity.sharedPrefUserDetails, mainActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(MainActivity.sharedPrefUserDetailsEmail, "");
                editor.putString(MainActivity.sharedPrefUserDetailsPasswordHash,MainActivity.defaultPasswordHash);
                editor.putString(MainActivity.sharedPrefUserDetailsName, "Default");
                //Code for extracting password value and saving it in the SharedPreference
                editor.commit();
            }
        });
        SharedPreferences userDetails = mainActivity.getSharedPreferences(MainActivity.sharedPrefUserDetails, mainActivity.MODE_PRIVATE);
        String userEmail = userDetails.getString(MainActivity.sharedPrefUserDetailsEmail, null);
        String userName = userDetails.getString(MainActivity.sharedPrefUserDetailsName, null);

        tvStatus.setText("You are logged in as:");
        tvUserName.setText(userName);
        tvUserEmail.setText(userEmail);
    }



    @Override
    public void update() {
        // TODO Auto-generated method stub

    }
}


package de.badgersburrow.sciman.objects;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.badgersburrow.sciman.main.MainActivity;
//import de.badgersburrow.sciman.;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by reim on 25.06.15.
 */
public class WebsocketReturn implements Serializable
    {
        private String reason;
        // arguments: various arguments necessary, eg. id of conference
        private ArrayList<String> arguments;
        //
        private String returnString;
        private String identifier;

        private static final String TAG = "de.tavendo.test1";

        public WebsocketReturn(){

        }

        public WebsocketReturn(String Reason, ArrayList<String> Arguments, String ReturnString) {

            this.reason = Reason;
            this.arguments = Arguments;
            this.returnString = ReturnString;
        }

        public int getReturnType(){


            if (this.reason.equals("getConf")){
                this.identifier = this.arguments.get(0);
                return MainActivity.requestIdGetConf;
            }
            else if (this.reason.equals("getConfHeader")){
                this.identifier = this.arguments.get(0);
                return MainActivity.requestIdGetConfHeader;
            }
            else if (this.reason.equals("getConfList")){
                this.identifier = this.arguments.get(0);
                return MainActivity.requestIdGetConfList;
            }
            else if (this.reason.equals("userLogin")){
                this.identifier = this.arguments.get(0);
                return MainActivity.requestIdUserLogin;
            }
            else if (this.reason.equals("userSignup")){
                this.identifier = this.arguments.get(0);
                return MainActivity.requestIdUserSignup;
            }
            else{
                Log.d(TAG, "Invalid reason!");
                return 0;
            }

        }

        public Conference getConference(){
            Gson gson = new Gson();
            return gson.fromJson(this.returnString, Conference.class);

        }

        public ArrayList<String> getConferenceList(){
            Gson gson = new Gson();

            Type listType = new TypeToken<ArrayList<String>>() {}.getType();
            ArrayList<String> yourClassList = gson.fromJson(this.returnString, listType);

            return yourClassList;

        }

        public User getUser(){
            Gson gson = new Gson();
            return gson.fromJson(this.returnString, User.class);
        }

        public String getReturnString(){
            return this.returnString;
        }

        public String getIdentifier(){
            return this.identifier;
        }


}

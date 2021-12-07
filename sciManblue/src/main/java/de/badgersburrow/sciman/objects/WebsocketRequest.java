package de.badgersburrow.sciman.objects;

import java.io.Serializable;
import java.util.ArrayList;
import com.google.gson.Gson;

/**
 * Created by reim on 25.06.15.
 */
public class WebsocketRequest implements Serializable
    {
        // appIdentifier: fixhash + version
        private String appIdentifier;
        // userIdentifier: profileId
        private int userIdentifier;
        // deviceIdentifier:
        private String deviceIdentifier;
        // reason: of the request, eg. getConf
        private String reason;
        // arguments: various arguments necessary, eg. id of conference
        private ArrayList<String> arguments;


        public WebsocketRequest(String AppIdentifier, int UserIdentifier, String DeviceIdentifier, String Reason, ArrayList<String> Arguments) {
            this.appIdentifier = AppIdentifier;
            this.userIdentifier = UserIdentifier;
            this.deviceIdentifier = DeviceIdentifier;
            this.reason = Reason;
            this.arguments = Arguments;
        }

        public String getGson(){
            Gson gson = new Gson();
            return gson.toJson(this);
        }


}

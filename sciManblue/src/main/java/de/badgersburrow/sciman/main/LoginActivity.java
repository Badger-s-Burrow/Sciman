package de.badgersburrow.sciman.main;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.User;
import de.badgersburrow.sciman.objects.WebsocketReturn;
import de.badgersburrow.sciman.objects.WebsocketRequest;
import de.badgersburrow.sciman.utilities.VariousMethods;

import java.util.ArrayList;

import butterknife.ButterKnife;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;





//TODO on cancel reset login button


public class LoginActivity extends AppCompatActivity {
    private final static WebSocketConnection mConnection = new WebSocketConnection();
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    ProgressDialog progressDialog = null;

    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login) Button _loginButton;
    @InjectView(R.id.link_signup) TextView _signupLink;
    @InjectView(R.id.btn_proceed) Button _proceedButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        _proceedButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                proceed();
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed(MainActivity.failureIdUserLoginInput);
            return;
        }

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.Base_Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        _loginButton.setEnabled(false);


        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        String passwordHash = VariousMethods.computeSHAHash(MainActivity.passwdSecret + password);

        User loginUser = new User(email, passwordHash, null);
        pollServer(MainActivity.requestIdUserLogin, loginUser.getGson());

    }

    public void proceed(){
        SharedPreferences prefs = getSharedPreferences(MainActivity.sharedPrefUserDetails, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(MainActivity.sharedPrefUserDetailsEmail, "");
        editor.putString(MainActivity.sharedPrefUserDetailsPasswordHash, MainActivity.defaultPasswordHash);
        editor.putString(MainActivity.sharedPrefUserDetailsName, "Default");
        //Code for extracting password value and saving it in the SharedPreference
        editor.commit();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        progressDialog.dismiss();
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed(int failureReason) {

        switch (failureReason) {
            case MainActivity.failureIdUserLoginKeyerror: {
                Toast.makeText(getBaseContext(), "Login failed: Submitted data was corrupted", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                break;
            }
            case MainActivity.failureIdUserLoginCredentials: {
                Toast.makeText(getBaseContext(), "Login failed: The inserted ", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                break;
            }
            case MainActivity.failureIdUserLoginInput: {
                Toast.makeText(getBaseContext(), "Login failed: Please correct your input", Toast.LENGTH_LONG).show();
                break;
            }
            default: {
                Toast.makeText(getBaseContext(), "Login failed: Unkown error occured", Toast.LENGTH_LONG).show();
            }
        }

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public final void pollServer(final int requestId, final String argument) {

        try {
            mConnection.connect(MainActivity.wsuri, new WebSocketHandler() {

                @Override
                public void onOpen() {
                    Log.d(MainActivity.TAG, "Status: Connected to " + MainActivity.wsuri);
                    ArrayList<String> arguments = new ArrayList<String>();
                    switch (requestId) {
                        case MainActivity.requestIdUserLogin: {
                            // argument is confIdentifier
                            String reason = "userLogin";

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
                        case MainActivity.requestIdUserLogin: {
                            Log.d(MainActivity.TAG, "reason: userLogin");
                            int id = Integer.parseInt(websocketReturn.getIdentifier());

                            if (id<1){
                                onLoginFailed(id);
                            }
                            else{
                                User user = websocketReturn.getUser();
                                SharedPreferences prefs = getSharedPreferences(MainActivity.sharedPrefUserDetails, MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString(MainActivity.sharedPrefUserDetailsEmail, user.getEmail());
                                editor.putString(MainActivity.sharedPrefUserDetailsPasswordHash,user.getPasswordHash());
                                editor.putString(MainActivity.sharedPrefUserDetailsName, user.getName());
                                //Code for extracting password value and saving it in the SharedPreference
                                editor.commit();
                                //Code for showing next Activity using intent (unchanged)
                                onLoginSuccess();
                            }




                            break;
                        }
                        default: {
                            Log.d(MainActivity.TAG, "Invalid reason!");
                            break;
                        }


                    }

                    mConnection.disconnect();
                }

                @Override
                public void onClose(int code, String reason) {
                    Log.d(MainActivity.TAG, "Connection lost.");
                }
            });
        } catch (WebSocketException e) {

            Log.d(MainActivity.TAG, e.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mConnection.isConnected()) {
            mConnection.disconnect();
        }
    }
}

package de.badgersburrow.sciman.main;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import de.badgersburrow.sciman.R;
import de.badgersburrow.sciman.objects.User;
import de.badgersburrow.sciman.objects.WebsocketRequest;
import de.badgersburrow.sciman.objects.WebsocketReturn;
import de.badgersburrow.sciman.utilities.VariousMethods;

import java.util.ArrayList;

import butterknife.ButterKnife;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private final static WebSocketConnection mConnection = new WebSocketConnection();
    ProgressDialog progressDialog = null;

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.input_passwordconfirm) EditText _passwordConfirm;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed(MainActivity.failureIdUserSignupInput);
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.Base_Theme_AppCompat_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();


        String passwordHash = VariousMethods.computeSHAHash(MainActivity.passwdSecret + password);

        // TODO: Implement your own signup logic here.
        User newUser = new User(email, passwordHash, name);
        pollServer(MainActivity.requestIdUserSignup,newUser.getGson());

    }


    public void onSignupSuccess() {
        progressDialog.dismiss();
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed(int failureReason) {
        switch (failureReason) {
            case MainActivity.failureIdUserSignupKeyerror: {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Signup failed: Submitted data was corrupted", Toast.LENGTH_LONG).show();
                break;
            }
            case MainActivity.failureIdUserSignupEmail: {
                progressDialog.dismiss();
                Toast.makeText(getBaseContext(), "Signup failed: Email is already used", Toast.LENGTH_LONG).show();
                break;
            }
            case MainActivity.failureIdUserSignupInput: {
                Toast.makeText(getBaseContext(), "Signup failed: Please correct your input", Toast.LENGTH_LONG).show();
                break;
            }
            default: {
                Toast.makeText(getBaseContext(), "Signup failed: Unkown error occured", Toast.LENGTH_LONG).show();
            }
        }


        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String passwordConfirm = _passwordConfirm.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

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

        if (!passwordConfirm.equals(password)) {
            _passwordConfirm.setError("password does not match");
            valid = false;
        } else {
            _passwordConfirm.setError(null);
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
                        case MainActivity.requestIdUserSignup: {
                            // argument is confIdentifier
                            String reason = "userSignup";

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
                        case MainActivity.requestIdUserSignup: {
                            Log.d(MainActivity.TAG, "reason: userSignup");
                            int id = Integer.parseInt(websocketReturn.getIdentifier());

                            if (id<1){
                                onSignupFailed(id);
                            }
                            else{
                                User user = websocketReturn.getUser();
                                SharedPreferences prefs = getSharedPreferences(MainActivity.sharedPrefUserDetails, MODE_PRIVATE);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString(MainActivity.sharedPrefUserDetailsEmail,user.getEmail());
                                editor.putString(MainActivity.sharedPrefUserDetailsPasswordHash,user.getPasswordHash());
                                editor.putString(MainActivity.sharedPrefUserDetailsName,user.getName());
                                //Code for extracting password value and saving it in the SharedPreference
                                editor.commit();
                                //Code for showing next Activity using intent (unchanged)
                                onSignupSuccess();
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
}

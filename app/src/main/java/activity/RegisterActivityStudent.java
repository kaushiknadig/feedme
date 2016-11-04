package activity;

/**
 * Created by Kaushik on 31-10-2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.pk.feedme.R;
import app.AppConfigStudent;
import app.AppControllerStudent;
import helper.SQLiteHandlerStudent;
import helper.SessionManager;

public class RegisterActivityStudent extends Activity {
    private static final String TAG = RegisterActivityStudent.class.getSimpleName();
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputUsn;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputBranch;
    private EditText inputSem;
    private EditText inputSection;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandlerStudent db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        inputUsn = (EditText) findViewById(R.id.usn);
        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputBranch = (EditText) findViewById(R.id.branch);
        inputSem = (EditText) findViewById(R.id.sem);
        inputSection = (EditText) findViewById(R.id.section);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandlerStudent(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivityStudent.this,
                    MainActivityStudent.class);
            startActivity(intent);
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String usn = inputUsn.getText().toString().trim();
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String branch = inputBranch.getText().toString().trim();
                String sem = inputSem.getText().toString().trim();
                String section = inputSection.getText().toString().trim();

                if (!usn.isEmpty() && !name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !branch.isEmpty() && !sem.isEmpty() && !section.isEmpty()) {
                    registerUser(usn, name, email, password, branch, sem, section);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivityStudent.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void registerUser(final String usn, final String name, final String email,
                              final String password, final String branch, final String sem, final String section) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfigStudent.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String usn = user.getString("usn");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String branch = user.getString("branch");
                        String sem = user.getString("sem");
                        String section = user.getString("section");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(usn, name, email, branch, sem, section, uid, created_at);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterActivityStudent.this,
                                LoginActivityStudent.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("usn", usn);
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("branch", branch);
                params.put("sem", sem);
                params.put("section", section);

                return params;
            }

        };

        // Adding request to request queue
        AppControllerStudent.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
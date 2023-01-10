package com.example.capstone_employee.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.capstone_employee.JavaMailAPI;
import com.example.capstone_employee.R;
import com.example.capstone_employee.SessionManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AuthenticateActivity extends AppCompatActivity {
    Button back,submit,verify;
    TextView tEmail;
    String userEmail;
    SessionManager sessionManager;
    ProgressDialog progressDialog;
    LinearLayout l_email;
    TextInputLayout l_verify;
    TextInputEditText authText;
    String determine;
    private static String verificationURl = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/verificationOTK.php";
    private static String fetchOTKURL = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/fetchOTKURL.php";
    private static String insert_is_mauth = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/insertMAuthCode.php";
    private static String insert_is_aauth = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/insertAAuthCode.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_authenticate);
        String email = getIntent().getStringExtra("email");
        determine = getIntent().getStringExtra("determine");
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();
        progressDialog = new ProgressDialog(this);
        tEmail = findViewById(R.id.txtEmail);
        back = findViewById(R.id.btn_back);
        submit = findViewById(R.id.btn_send);
        verify = findViewById(R.id.btn_verify);
        tEmail.setText(email);
        authText = findViewById(R.id.txt_auth);
        userEmail = email;
        l_verify = findViewById(R.id.layout_auth);
        l_email = findViewById(R.id.layout_email);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int min = 1000;
                final int max = 9999;
                //Generates random 4 digits key.
                final int random = new Random().nextInt((max - min) + 1) + min;
                final String rand = String.valueOf(random);
                final String title =  "OTK for email verification.";
                final String message = "This is your one time key to verify your email: "+rand;
                if(!email.isEmpty()){
                    //Sends OTK email to user
                    JavaMailAPI javaMailAPI = new JavaMailAPI(AuthenticateActivity.this,email,title,message);
                    javaMailAPI.execute();
                    if(javaMailAPI.isSent(true)){
                        Toast.makeText(AuthenticateActivity.this, "Verification code sent.", Toast.LENGTH_SHORT).show();
                    }
                    //Send OTK to database
                    insertKey(rand);
                }else if(email.isEmpty()){
                    Toast.makeText(AuthenticateActivity.this, "Must not be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String key = authText.getText().toString().trim();
                if(!key.isEmpty()){
                    fetchResultOTK(key);
                }else{
                    authText.setError("");
                    Toast.makeText(AuthenticateActivity.this, "Must not be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public void insertKey(String rand){
        progressDialog.setMessage("Sending!");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, verificationURl, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(AuthenticateActivity.this, "Check your email for one time key!", Toast.LENGTH_SHORT).show();
                                l_email.setVisibility(View.GONE);
                                l_verify.setVisibility(View.VISIBLE);
                                submit.setVisibility(View.GONE);
                                verify.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(AuthenticateActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AuthenticateActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",userEmail);
                params.put("key",rand);
                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
    public void fetchResultOTK(String key){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, fetchOTKURL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                if(determine.equals("main")){
                                    changeMData();
                                }else{
                                    changeAData();
                                }
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(AuthenticateActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AuthenticateActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",userEmail);
                params.put("key",key);
                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
    public void changeMData(){
        progressDialog.setMessage("Verifying!");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, insert_is_mauth, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(AuthenticateActivity.this, "Verified!", Toast.LENGTH_SHORT).show();
                                getFragmentManager().popBackStackImmediate();
                                finish();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(AuthenticateActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AuthenticateActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",userEmail);
                params.put("key","1");
                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
    public void changeAData(){
        progressDialog.setMessage("Verifying!");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, insert_is_aauth, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(AuthenticateActivity.this, "Verified!", Toast.LENGTH_SHORT).show();
                                getFragmentManager().popBackStackImmediate();
                                finish();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(AuthenticateActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AuthenticateActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",userEmail);
                params.put("key","1");
                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
}
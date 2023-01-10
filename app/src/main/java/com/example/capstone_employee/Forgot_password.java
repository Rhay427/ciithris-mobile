package com.example.capstone_employee;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.capstone_employee.ui.profile.ProfileFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Forgot_password extends AppCompatActivity {
    Button buttonBack,submit,send,auth;
    TextInputEditText email,pass,repass,txtAuth;
    TextInputLayout l_pass,l_repass,l_email,l_auth;
    String userEmail;
    ProgressDialog progressDialog;
    private static String insertURL = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/ot_Key.php";
    private static String authURL = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/verify_OTK.php";
    private static String passURL = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/changePass.php";
    private static String verifyURL = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/checkPass.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_forgot_password);
        progressDialog = new ProgressDialog(Forgot_password.this);
        buttonBack = findViewById(R.id.btn_back);
        submit = findViewById(R.id.btn_submit);
        email = findViewById(R.id.txt_email);
        txtAuth = findViewById(R.id.txt_auth);
        pass = findViewById(R.id.txt_pass);
        repass = findViewById(R.id.txt_repass);
        send = findViewById(R.id.btn_send);
        l_pass = findViewById(R.id.layout_password);
        l_repass = findViewById(R.id.layout_repassword);
        l_email = findViewById(R.id.layout_email);
        l_auth = findViewById(R.id.layout_auth);
        auth = findViewById(R.id.btn_auth);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Forgot_password.this,MainActivity.class));
                finish();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userEmail = email.getText().toString().trim();
                final int min = 1000;
                final int max = 9999;
                //Generates random 4 digits key.
                final int random = new Random().nextInt((max - min) + 1) + min;
                final String rand = String.valueOf(random);
                final String title =  "OTK for password reset.";
                final String message = "This is your one time key to change your password: "+rand;
                if(!userEmail.isEmpty()){
                    //Sends OTK email to user
                    JavaMailAPI javaMailAPI = new JavaMailAPI(Forgot_password.this,userEmail,title,message);
                    javaMailAPI.execute();
                    //Send OTK to database
                    insertKey(rand);
                }else if(userEmail.isEmpty()){
                    email.setError("");
                    Toast.makeText(Forgot_password.this, "Must not be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String key = txtAuth.getText().toString().trim();
                if(!key.isEmpty()){
                    Authenticate(key);
                }else{
                    txtAuth.setError("");
                    Toast.makeText(Forgot_password.this, "Must not be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String Pass = pass.getText().toString().trim();
                final String Repass = repass.getText().toString().trim();
                if(!Pass.isEmpty() && !Repass.isEmpty()){
                    if(!Pass.equals(Repass)){
                        pass.setError("");
                        repass.setError("");
                        Toast.makeText(Forgot_password.this, "Passwords does not match!", Toast.LENGTH_SHORT).show();
                    }else{
                        verifyPass(Pass);
                    }
                }else if(Pass.isEmpty()){
                    pass.setError("");
                    Toast.makeText(Forgot_password.this, "Password field required!", Toast.LENGTH_SHORT).show();
                }else if(Repass.isEmpty()){
                    repass.setError("");
                    Toast.makeText(Forgot_password.this, "must not be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //Send OTK to database
    public void insertKey(String authKey){
        progressDialog.setMessage("Sending!");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, insertURL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(Forgot_password.this, "Check your email for one time key!", Toast.LENGTH_SHORT).show();
                                l_auth.setVisibility(View.VISIBLE);
                                auth.setVisibility(View.VISIBLE);
                                send.setVisibility(View.GONE);
                                l_email.setVisibility(View.GONE);
                                progressDialog.dismiss();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(Forgot_password.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Forgot_password.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",userEmail);
                params.put("key",authKey);
                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
    //Verify if OTK matches the entry
    public void Authenticate(String authKey){
        progressDialog.setMessage("Verifying!");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, authURL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                l_auth.setVisibility(View.GONE);
                                auth.setVisibility(View.GONE);
                                submit.setVisibility(View.VISIBLE);
                                l_pass.setVisibility(View.VISIBLE);
                                l_repass.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(Forgot_password.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Forgot_password.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",userEmail);
                params.put("key",authKey);
                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
    //Changes the password
    public void changePass(String pass){
        progressDialog.setMessage("Processing Request!");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, passURL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                startActivity(new Intent(Forgot_password.this,MainActivity.class));
                                finish();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(Forgot_password.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Forgot_password.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",userEmail);
                params.put("password",pass);
                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
    //Check of password entered exists
    public void verifyPass(String pass){
        progressDialog.setMessage("Processing Request!");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, verifyURL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(Forgot_password.this, "You entered your old password!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }else if(success.equals("0")){
                                changePass(pass);
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(Forgot_password.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Forgot_password.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",userEmail);
                params.put("password",pass);
                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
}
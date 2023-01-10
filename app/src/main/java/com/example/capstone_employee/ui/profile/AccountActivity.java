package com.example.capstone_employee.ui.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.capstone_employee.R;
import com.example.capstone_employee.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {
    private static final String TAG = EditActivity.class.getSimpleName();
    Button back,
        edit;
    String empid, username;
    EditText mainEmail,
        secondEmail,
        UserName;
    SessionManager sessionManager;
    ProgressDialog progressDialog;
    private static String URL_READ = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/getuserDetail.php";
    private static String URL_EDIT = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/editAcc_Detail.php";
    private static String mainemailChanged = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/mainemailChanged.php";
    private static String altemailChanged = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/altemailChanged.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_account);
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        empid = user.get(sessionManager.EMPID);
        username = user.get(sessionManager.USERNAME);
        back = findViewById(R.id.btn_back);
        mainEmail = findViewById(R.id.txtmainEmail);
        secondEmail = findViewById(R.id.txtsecondEmail);
        UserName = findViewById(R.id.txtUsername);
        edit = findViewById(R.id.Btn_Edit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
                finish();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = UserName.getText().toString().trim();
                String memail = mainEmail.getText().toString().trim();
                String semail = secondEmail.getText().toString().trim();
                if(!user.isEmpty() && !memail.isEmpty() && !semail.isEmpty()){
                    editAccountDetail();
                    mainEmailChanged(memail);
                    altEmailChanged(semail);
                }else if (user.isEmpty()){
                    UserName.setError("Username is required.");
                }else if (memail.isEmpty()){
                    mainEmail.setError("Main Email is required.");
                }else if (semail.isEmpty()){
                    secondEmail.setError("Second Email is required.");
                }else{
                    Toast.makeText(AccountActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getuserDetail(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String main = object.getString("memail").trim();
                                    String second = object.getString("aemail").trim();
                                    String userName = object.getString("username").trim();
                                    mainEmail.setText(main);
                                    secondEmail.setText(second);
                                    UserName.setText(userName);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error Reading detail!"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error Reading detail!"+error, Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("empid",empid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    @Override
    protected void onResume() {
        super.onResume();
        getuserDetail();
    }
    private void editAccountDetail(){
        progressDialog = new ProgressDialog(this);
        final String mainEmail = this.mainEmail.getText().toString().trim();
        final String secondEmail = this.secondEmail.getText().toString().trim();
        final String username = this.UserName.getText().toString().trim();
        final String id = empid;
        final String user = username;

        progressDialog.setMessage("Saving...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST
                , URL_EDIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(AccountActivity.this,
                                        "Successfully edited user account!", Toast.LENGTH_SHORT).show();
                                sessionManager.createSession(id,user);
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(AccountActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(AccountActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("empid",id);
                params.put("mainEmail",mainEmail);
                params.put("secondEmail",secondEmail);
                params.put("username",username);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void mainEmailChanged(String email){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, mainemailChanged, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(AccountActivity.this,
                                        "Verification of main email is required!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(AccountActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AccountActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("key","0");
                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
    public void altEmailChanged(String email){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, altemailChanged, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(AccountActivity.this,
                                        "Verification of second email is required!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(AccountActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AccountActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("email",email);
                params.put("key","0");
                return params;
            }
        };
        RequestQueue request = Volley.newRequestQueue(this);
        request.add(stringRequest);
    }
}
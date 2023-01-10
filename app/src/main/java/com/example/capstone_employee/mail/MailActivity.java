package com.example.capstone_employee.mail;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.capstone_employee.JavaMailAPI;
import com.example.capstone_employee.R;
import com.example.capstone_employee.SessionManager;
import com.example.capstone_employee.recruit.RecruitActivity;
import com.example.capstone_employee.ui.profile.ProfileFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MailActivity extends AppCompatActivity {
    Button back,submit;
    EditText title,message;
    ProgressDialog progressDialog;
    private static final String TAG = MailActivity.class.getSimpleName();
    SessionManager sessionManager;
    String empid,userEmail,Fullname;
    private static String URL_READ = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/getuserDetail.php";
    private static String mailURL = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/sendMail.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_mail);
        getuserDetail();
        back = findViewById(R.id.btn_back);
        title = findViewById(R.id.messageTitle);
        message = findViewById(R.id.txt_message);
        submit = findViewById(R.id.BtnSubmit);
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        empid = user.get(sessionManager.EMPID);
        HashMap<String, String> item = sessionManager.getEmail();
        userEmail = item.get(sessionManager.CMAIN_EMAIL);
        progressDialog = new ProgressDialog(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MailActivity.this,MailStatus.class));
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mTitle = title.getText().toString().trim();
                final String mMessage = message.getText().toString().trim();
                if(!mTitle.isEmpty() && !mMessage.isEmpty()){
                    sendMail(mTitle,mMessage);
                }else if(mTitle.isEmpty()){
                    title.setError("Title required!");
                }else if(mMessage.isEmpty()){
                    message.setError("Message required!");
                }else{
                    Toast.makeText(MailActivity.this, "All fields must not be empty!", Toast.LENGTH_SHORT).show();
                }
                //function
            }
        });
    }
    public void sendMail(String mTitle,String mMessage){
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, mailURL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                progressDialog.dismiss();
                                final String Title =  "Mail received.";
                                final String Message = "Greetings\n\n\tYour mail has been received, please wait for the admin to reply.";
                                JavaMailAPI javaMailAPI = new JavaMailAPI(MailActivity.this,userEmail,Title,Message);
                                javaMailAPI.execute();
                                if(javaMailAPI.isSent(true)){
                                    AlertDialog.Builder alert = new AlertDialog.Builder(MailActivity.this);
                                    alert.setTitle("Mail successfully sent!");
                                    // alert.setMessage("Message");

                                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            title.setText("");
                                            message.setText("");
                                        }
                                    });
                                    alert.show();
                                }

                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(MailActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(MailActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("empid",empid);
                params.put("name",Fullname);
                params.put("email",userEmail);
                params.put("title",mTitle);
                params.put("message",mMessage);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    //Fetch user detail
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
                                    String fullName = object.getString("firstname").trim()+
                                            " "+object.getString("middlename").trim()+
                                            " "+object.getString("lastname").trim();
                                    Fullname = fullName;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MailActivity.this, "Error Reading detail!"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MailActivity.this, "Error Reading detail!"+error, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(MailActivity.this);
        requestQueue.add(stringRequest);
    }

}
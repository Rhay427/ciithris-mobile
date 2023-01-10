package com.example.capstone_employee;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.capstone_employee.leave.LeaveActivity;
import com.example.capstone_employee.mail.MailActivity;
import com.example.capstone_employee.recruit.RecruitActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResignActivity extends AppCompatActivity {
    SessionManager sessionManager;
    Button back,submit;
    String userEmail, empid, Fullname, Dept;
    EditText reason;
    TextView department;
    ProgressDialog progressDialog;
    private static final String TAG = ResignActivity.class.getSimpleName();


    private static String URL_READ = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/getuserDetail.php";
    private static String resignURL = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/resign_request.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_resign);
        getuserDetail();
        sessionManager = new SessionManager(this);
        back = findViewById(R.id.btn_back);
        submit = findViewById(R.id.BtnSubmit);
        department = findViewById(R.id.department);
        reason = findViewById(R.id.txt_reason);
        progressDialog = new ProgressDialog(this);
        HashMap<String,String> getID = sessionManager.getUserDetail();
        HashMap<String,String> getEmail = sessionManager.getEmail();
        userEmail = getEmail.get(sessionManager.CMAIN_EMAIL);
        empid = getID.get(sessionManager.EMPID);
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
                String Reason = reason.getText().toString().trim();
                if(!Reason.isEmpty()){
                    AlertDialog.Builder alert = new AlertDialog.Builder(ResignActivity.this);
                    alert.setTitle("Do you really want to send this request?");
                    // alert.setMessage("Message");

                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            submitResignation(Fullname,Dept,Reason);
                        }
                    });

                    alert.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            });

                    alert.show();
                }else if(Reason.isEmpty()){
                    reason.setError("Reason for request is required!");
                }
            }
        });
    }
    public void submitResignation(String name, String dept, String Reason){
        progressDialog.setMessage("loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, resignURL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                progressDialog.dismiss();
                                final String Title =  "Request for Resignation received.";
                                final String Message = "Greetings\n\n\tYour request for resignation has been received, please wait for the admin to reply.";
                                JavaMailAPI javaMailAPI = new JavaMailAPI(ResignActivity.this,userEmail,Title,Message);
                                javaMailAPI.execute();
                                if(javaMailAPI.isSent(true)){
                                    AlertDialog.Builder alert = new AlertDialog.Builder(ResignActivity.this);
                                    alert.setTitle("Resignation request successfully sent!");
                                    // alert.setMessage("Message");

                                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            reason.setText("");
                                        }
                                    });
                                    alert.show();
                                    Toast.makeText(ResignActivity.this, "Request sent successfully!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(ResignActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ResignActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("empid",empid);
                params.put("name",name);
                params.put("email",userEmail);
                params.put("dept",Dept);
                params.put("reason",Reason);
                params.put("state","1");
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
                                    String dept = object.getString("department").trim();
                                    department.setText(dept);
                                    Dept = dept;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ResignActivity.this, "Error Reading detail!"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ResignActivity.this, "Error Reading detail!"+error, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(ResignActivity.this);
        requestQueue.add(stringRequest);
    }
}
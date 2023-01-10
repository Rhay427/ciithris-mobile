package com.example.capstone_employee.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.capstone_employee.BottomNav;
import com.example.capstone_employee.JavaMailAPI;
import com.example.capstone_employee.R;
import com.example.capstone_employee.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TimeReport extends AppCompatActivity {
    String date,timeOut,id,userEmail;
    Button back, submit;
    EditText errorMessage;
    SessionManager sessionManager;
    private static String UPTIME_URL = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/uptime.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_time_report);
        date = getIntent().getStringExtra("date");
        timeOut = getIntent().getStringExtra("timeOut");
        id = getIntent().getStringExtra("id");
        back = findViewById(R.id.btn_back);
        submit = findViewById(R.id.BtnSubmit);
        errorMessage = findViewById(R.id.txt_message);
        sessionManager = new SessionManager(this);
        HashMap<String, String> item = sessionManager.getEmail();
        userEmail = item.get(sessionManager.CMAIN_EMAIL);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(TimeReport.this, BottomNav.class));
                getFragmentManager().popBackStackImmediate();
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String message = errorMessage.getText().toString().trim();
                if(!message.isEmpty()){
                    updateReport(message);
                }else {
                    errorMessage.setError("This field must not be empty");
                }
            }
        });


    }
    public void updateReport(String message){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPTIME_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                final String Title =  "Report received.";
                                final String Message = "Greetings\n\n\tYour report has been received, time inserted has been ignored.";
                                JavaMailAPI javaMailAPI = new JavaMailAPI(TimeReport.this,userEmail,Title,Message);
                                javaMailAPI.execute();
                                if(javaMailAPI.isSent(true)){
                                    Toast.makeText(TimeReport.this, "Successfully Reported!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(TimeReport.this, BottomNav.class));
                                }
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(TimeReport.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TimeReport.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",id);
                params.put("state","0");
                params.put("errorMessage",message);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(TimeReport.this);
        requestQueue.add(stringRequest);
    }
}
package com.example.capstone_employee;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.capstone_employee.ui.home.HomeFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kotlin.collections.MapsKt;

public class MainActivity extends AppCompatActivity {
    Button login;
    TextView forgot;
    TextInputEditText empID, pass;
    SessionManager sessionManager;
    ProgressDialog progressDialog;
    private static String login_URL = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/login.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        sessionManager = new SessionManager(this);
        login = findViewById(R.id.btn_login);
        forgot = findViewById(R.id.txt_forgot);
        empID = findViewById(R.id.txt_empID);
        pass = findViewById(R.id.txt_pass);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Logging-in...");
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Forgot_password.class);
                startActivity(intent);
                finish();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String empid = empID.getText().toString().trim();
                String password = pass.getText().toString().trim();
                if(!empid.isEmpty() && !password.isEmpty()){
                    login(empid,password);
                }else if(empid.isEmpty()){
                    empID.setError("Employee ID required");
                }else if(password.isEmpty()){
                    pass.setError("Password required");
                }else{
                    empID.setError("Employee ID required");
                    pass.setError("Password required");
                }
            }
        });
    }
    //Check user, proceeds to login if matches the entry
    public void login(String empId, String passWord){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, login_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");
                            if(success.equals("1")){
                                for (int i = 0; i < jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String Empid = object.getString("empid").trim();
                                    String Username = object.getString("username").trim();
                                    sessionManager.createSession(Empid,Username);
                                    Intent intent = new Intent(MainActivity.this, BottomNav.class);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                }
                            }else if(success.equals("0")){
                                Toast.makeText(MainActivity.this, "Incorrect: please check credentials", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if(e.toString().matches(".*\\d.*")){
                                Toast.makeText(MainActivity.this,
                                        "User not found, please check credentials", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }else{
                                Toast.makeText(MainActivity.this,
                                        "Error: "+e, Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,
                                "Error "+ error.toString(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("empid",empId);
                params.put("password",passWord);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}

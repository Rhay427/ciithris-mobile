package com.example.capstone_employee.mail;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
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
import com.example.capstone_employee.ui.home.TimeReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ViewMail extends AppCompatActivity {
    TextView title,date,message,response;
    String strTitle,strDate,strMessage,strResponse,empid, mainID;
    Button back, delete;
    ProgressDialog progressDialog;
    private static String deleteURL = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/deleteMail.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_view_mail);
        title = findViewById(R.id.subject);
        date = findViewById(R.id.date);
        message = findViewById(R.id.message);
        response = findViewById(R.id.response);
        back = findViewById(R.id.btn_back);
        delete = findViewById(R.id.btnDelete);
        progressDialog = new ProgressDialog(this);
        empid = getIntent().getStringExtra("empid");
        strTitle = getIntent().getStringExtra("title");
        strDate = getIntent().getStringExtra("datestamp");
        strMessage = getIntent().getStringExtra("message");
        strResponse = getIntent().getStringExtra("response");
        mainID = getIntent().getStringExtra("mainID");
        title.setText(strTitle);
        date.setText(strDate);
        message.setText(strMessage);
        response.setText(strResponse);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewMail.this,MailStatus.class));
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ViewMail.this);
                alert.setTitle("Are you sure?");
                // alert.setMessage("Message");

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteMail();
                    }
                });

                alert.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });

                alert.show();
            }
        });
    }
    public void deleteMail(){
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, deleteURL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(ViewMail.this, "Successfully Deleted!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ViewMail.this,MailStatus.class));
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ViewMail.this, e.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(ViewMail.this, error.toString().trim(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",mainID);
                params.put("state","0");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
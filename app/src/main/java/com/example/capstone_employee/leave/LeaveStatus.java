package com.example.capstone_employee.leave;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
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
import com.example.capstone_employee.mail.MailStatus;
import com.example.capstone_employee.mail.Mails;
import com.example.capstone_employee.mail.ViewMail;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeaveStatus extends AppCompatActivity {
    Button back;
    FloatingActionButton fab;
    LeaveAdapter leaveAdapter;
    Leave leave;
    SwipeRefreshLayout onswipe;
    ShimmerFrameLayout shimmerFrameLayout;
    SessionManager sessionManager;
    String empid;
    ListView listView;
    ProgressDialog progressDialog;
    private static String getLeavesUrl = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/retrieveLeave.php";
    private static String cancelURL = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/cancelLeave.php";
    public static ArrayList<Leave> leaveArrayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_leave_status);
        progressDialog = new ProgressDialog(this);
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        empid = user.get(sessionManager.EMPID);
        back = findViewById(R.id.btn_back);
        fab = findViewById(R.id.Btn_fab);
        listView = findViewById(R.id.list_statusLeave);
        leaveAdapter = new LeaveAdapter(this,leaveArrayList);
        listView.setAdapter(leaveAdapter);
        onswipe = findViewById(R.id.swipe_refresh);
        shimmerFrameLayout = findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmer();
        retrieveLeave();
        onswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveLeave();
                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                onswipe.setRefreshing(false);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
                finish();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LeaveStatus.this,LeaveActivity.class));
                finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                CharSequence[] item = {"Delete","Dismiss"};
                builder.setTitle("What to do?");
                builder.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch(i){
                            case 0:
                                cancelLeave(leaveArrayList.get(position).getId());
                                break;
                            case 1:
                                builder.create().dismiss();
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    public void retrieveLeave() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getLeavesUrl, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        onswipe.setVisibility(View.VISIBLE);
                        leaveArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if(success.equals("1")){
                                for(int i = 0;i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String id = object.getString("id");
                                    String email = object.getString("email");
                                    String startdate = object.getString("startdate");
                                    String enddate = object.getString("enddate");
                                    String total = object.getString("total");
                                    String reason = object.getString("reason");
                                    String status = object.getString("status");
                                    String datestamp = object.getString("datestamp");
                                    String remarks = object.getString("remarks");

                                    leave = new Leave(id,email,startdate,enddate,total,reason,status,datestamp,remarks);
                                    leaveArrayList.add(leave);
                                    leaveAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LeaveStatus.this, e.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LeaveStatus.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
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
    public void cancelLeave(String leaveID){
        progressDialog.setMessage("Loading, Please wait...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, cancelURL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(LeaveStatus.this, "Deleted Successfully!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LeaveStatus.this,LeaveStatus.class));
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LeaveStatus.this, e.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(LeaveStatus.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id",leaveID);
                params.put("status","Cancelled");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
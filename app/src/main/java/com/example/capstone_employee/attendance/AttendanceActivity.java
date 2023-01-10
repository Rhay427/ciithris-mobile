package com.example.capstone_employee.attendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendanceActivity extends AppCompatActivity {
    Button btn_back;
    AttAdapter attAdapter;
    AttendanceList attendanceList;
    SwipeRefreshLayout onswipe;
    ShimmerFrameLayout shimmerFrameLayout;
    SessionManager sessionManager;
    String empid;
    ListView listView;
    ProgressDialog progressDialog;
    public static ArrayList<AttendanceList> attList = new ArrayList<>();
    private static String getAttList = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/Attendance_list.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_attendance);
        progressDialog = new ProgressDialog(this);
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        empid = user.get(sessionManager.EMPID);
        listView = findViewById(R.id.list_attendance);
        attAdapter = new AttAdapter(this,attList);
        listView.setAdapter(attAdapter);
        onswipe = findViewById(R.id.swipe_refresh);
        shimmerFrameLayout = findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmer();
        retrieveAttendance(empid);
        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
                finish();
            }
        });
        onswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveAttendance(empid);
                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                onswipe.setRefreshing(false);
            }
        });
    }
    public void retrieveAttendance(String empId){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getAttList, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        onswipe.setVisibility(View.VISIBLE);
                        attList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if(success.equals("1")){
                                for (int i = 0;i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String id = object.getString("id");
                                    String Empid = object.getString("empid");
                                    String Fullname = object.getString("fullname");
                                    String timeIn = object.getString("timeIn");
                                    String timeout = object.getString("timeOut");
                                    String hours = object.getString("hours");
                                    String date = object.getString("dateStamp");
                                    String lateStatus = object.getString("lateStatus");
                                    attendanceList = new AttendanceList(id,Empid,Fullname,
                                            timeIn,timeout,hours,date,lateStatus);
                                    attList.add(attendanceList);
                                    attAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(AttendanceActivity.this, e.toString().trim(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AttendanceActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("empid",empId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
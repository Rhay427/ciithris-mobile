package com.example.capstone_employee.ui.home;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.capstone_employee.BottomNav;
import com.example.capstone_employee.R;
import com.example.capstone_employee.SessionManager;
import com.example.capstone_employee.ui.profile.ProfileFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {


    private HomeViewModel homeViewModel;
    private static final String TAG = ProfileFragment.class.getSimpleName();
    AttendanceAdapter adapter;
    public static ArrayList<Attendance> attendances = new ArrayList<>();
    ListView listView;
    TextView btn_timeout;
    TextView btn_timein;
    String timeIn, timeOut;
    TextView User;
    SessionManager sessionManager;
    String empid;
    String time;
    String fullname;
    String currentDate;
    String res;
    String int_hours;
    String int_min;
    Attendance attendance;
    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    ProgressDialog progressDialog;
    private static String URL_READ = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/getuserDetail.php";
    private static String TIME_URL = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/timeInsert.php";
    private static String TIMEOUT_URL = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/timeUpdate.php";
    private static String getTimeUrl = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/retrieveAttendance.php";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        sessionManager = new SessionManager(getContext());
        sessionManager.checkLogin();
        listView = root.findViewById(R.id.timeList);
        btn_timein = root.findViewById(R.id.btn_timeIn);
        btn_timeout = root.findViewById(R.id.btn_timeOut);
        User = root.findViewById(R.id.txt_username);
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        currentDate = dateFormat.format(calendar.getTime());
        HashMap<String, String> user = sessionManager.getUserDetail();
        empid = user.get(sessionManager.EMPID);
        User.setText(empid);
        adapter = new AttendanceAdapter(getContext(),attendances);
        btn_timein.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_timeout.setVisibility(v.VISIBLE);
                btn_timein.setVisibility(v.INVISIBLE);
                Calendar calendar =  Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("kk:mm:ss");
                String strDate = mdformat.format(calendar.getTime());
                timeIn = strDate;
                Toast.makeText(getContext(), "Time-in: "+timeIn, Toast.LENGTH_SHORT).show();
                //Store time in Session
                insertTime(timeIn);
                sessionManager.setTimedIn(timeIn);
                startActivity(new Intent(getContext(), BottomNav.class));
            }
        });
        btn_timeout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_timeout.setVisibility(v.INVISIBLE);
                btn_timein.setVisibility(v.VISIBLE);
                Calendar calendar =  Calendar.getInstance();
                SimpleDateFormat mdformat = new SimpleDateFormat("kk:mm:ss");
                String strDate = mdformat.format(calendar.getTime());
                timeOut = strDate;
                //Fetch time-in in Session
                HashMap<String, String> user = sessionManager.getTimeDetail();
                time = user.get(sessionManager.TIME);
                HashMap<String, String> user1 = sessionManager.getTimeID();
                String timeID = user1.get(sessionManager.TIMEID);

                try {
                    //Calculate diff in time
                    Date time1 = mdformat.parse(time);
                    Date time2 = mdformat.parse(timeOut);
                    if(time1.getTime() < time2.getTime()){
                        long diff = (time1.getTime() - time2.getTime())*-1;
                        long diffSeconds = diff / 1000 % 60;
                        long diffMinutes = diff / (60 * 1000) % 60;
                        long diffHours = diff / (60 * 60 * 1000) % 24;
                        String sec = String.valueOf(diffSeconds);
                        String min = String.valueOf(diffMinutes);
                        String hrs = String.valueOf(diffHours);
                        if(diffMinutes < 60){
                            res = hrs+":"+min;
                            int_hours = hrs;
                            int_min = min;
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //Reset Time-in value
                sessionManager.setTimedOut();
                //Send to database
                updateTime(timeID,timeOut,res,int_hours,int_min);
                startActivity(new Intent(getContext(), BottomNav.class));
            }
        });
        listView.setAdapter(adapter);
        retrieveTime();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                CharSequence[] item = {"Send Report","Dismiss"};
                builder.setTitle("Something wrong with inserted time?");
                builder.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch(i){
                            case 0:
                                //set value as invalid
                                Intent intent = new Intent(getContext(), TimeReport.class);
                                intent.putExtra("id",attendances.get(position).getMainid());
                                startActivity(intent);
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
        return root;
    }

    //Fetch time-in and time-out
    public void retrieveTime(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getTimeUrl, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        attendances.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if(success.equals("1")){
                                for(int i = 0;i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String mainid = object.getString("id");
                                    String empid = object.getString("empid");
                                    String fullname = object.getString("fullname");
                                    String timeIn = object.getString("timeIn");
                                    String timeOut = object.getString("timeOut");
                                    String Hours = object.getString("hours");
                                    String dateStamp = object.getString("dateStamp");
                                    if(timeOut == "null"){
                                        timeOut = "";
                                    }else{
                                        timeOut = object.getString("timeOut");
                                    }
                                    if(Hours == "null"){
                                        Hours = "";
                                    }else{
                                        Hours = object.getString("hours");
                                    }

                                    attendance = new Attendance(mainid,empid,fullname,timeIn,timeOut,Hours,dateStamp);
                                    attendances.add(attendance);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("empid",empid);
                params.put("dateStamp",currentDate);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                                    String userName = object.getString("username").trim();
                                    String fullName = object.getString("firstname").trim()+
                                            " "+object.getString("middlename").trim()+
                                            " "+object.getString("lastname").trim();
                                    String memail = object.getString("memail").trim();
                                    sessionManager.currentMEmail(memail);
                                    User.setText(userName);
                                    fullname = fullName;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error Reading detail!"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), "Error Reading detail!"+error, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    //Check if timed-in
    private void getTimeBool(){
        if(sessionManager.isTimedIn()){
            btn_timein.setVisibility(View.INVISIBLE);
            btn_timeout.setVisibility(View.VISIBLE);
        }
        else if(!sessionManager.isTimedIn()){
            btn_timein.setVisibility(View.VISIBLE);
            btn_timeout.setVisibility(View.INVISIBLE);
        }
    }
    //Insert time-in and time-out
    private void insertTime(String timestart){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Inserting time...");
        progressDialog.show();
        final String id = empid;
        final String date = currentDate;
        final String state = "1";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, TIME_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            String id = jsonObject.getString("id");
                            if(success.equals("1")){
                                sessionManager.setTimedID(id);
                                Toast.makeText(getContext(), "Time-in: "+timeIn, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> params = new HashMap<>();
                params.put("empid",id);
                params.put("fullname",fullname);
                params.put("timeIn",timestart);
                params.put("dateStamp",date);
                params.put("state",state);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
    private void updateTime(String id, String timestop, String hours, String comp_hours, String comp_min){
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Inserting time...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, TIMEOUT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(getContext(), "Time-out: "+timeOut, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> params = new HashMap<>();
                params.put("id",id);
                params.put("timeOut",timestop);
                params.put("hours",hours);
                params.put("int_hours",comp_hours);
                params.put("int_minutes",comp_min);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        getuserDetail();
        getTimeBool();
        retrieveTime();
    }
}
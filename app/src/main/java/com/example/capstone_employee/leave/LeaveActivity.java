package com.example.capstone_employee.leave;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.capstone_employee.JavaMailAPI;
import com.example.capstone_employee.R;
import com.example.capstone_employee.SessionManager;
import com.example.capstone_employee.mail.MailActivity;

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
import java.util.concurrent.TimeUnit;

public class LeaveActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String data_index,category_index,type_index,type_string, balance_id;
    Button back, compute, submit;
    EditText start, end; //reason;
    String Start, End, empid, userEmail, testDatelimit, Fullname;
    TextView res,balance;
    DatePickerDialog.OnDateSetListener dateSetListener1, dateSetListener2;
    SessionManager sessionManager;
    ProgressDialog progressDialog;
    Spinner spinnerCat, spinnerType;
    ArrayList<String> categoryList = new ArrayList<>();
    ArrayList<String> categoryId = new ArrayList<>();
    ArrayList<String> typeList = new ArrayList<>();
    ArrayList<String> typeId = new ArrayList<>();
    ArrayAdapter<String> categoryAdapter;
    ArrayAdapter<String> typeAdapter;
    RequestQueue leavetypeQueue;
    private static final String TAG = LeaveActivity.class.getSimpleName();


    private static String URL_READ = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/getuserDetail.php";
    private static String updateCredits = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/updateCredits.php";
    private static String updateTypeCredits = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/updateTypeCredit.php";
    //private static String fetchLeaveCredits = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/fetchleaveCredits.php";
    private static String fetchLeaveTypeCredits = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/f_leaveTypeBalance.php";
    private static String requestLeave = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/leaveRequest.php";
    private static String populateCat = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/pop_leaveCat.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_leave);
        getuserDetail();
        leavetypeQueue = Volley.newRequestQueue(this);
        sessionManager = new SessionManager(this);
        HashMap<String,String> getID = sessionManager.getUserDetail();
        HashMap<String,String> getEmail = sessionManager.getEmail();
        userEmail = getEmail.get(sessionManager.CMAIN_EMAIL);
        empid = getID.get(sessionManager.EMPID);
        back = findViewById(R.id.btn_back);
        compute = findViewById(R.id.btn_compute);
        start = findViewById(R.id.startDate);
        end = findViewById(R.id.endDate);
        res = findViewById(R.id.txt_result);
        balance = findViewById(R.id.leaveBalance);
        //reason = findViewById(R.id.txt_reason);
        submit = findViewById(R.id.BtnSubmit);
        start.setInputType(InputType.TYPE_NULL);
        end.setInputType(InputType.TYPE_NULL);
        spinnerCat = findViewById(R.id.spinnerCat);
        spinnerType = findViewById(R.id.spinnerType);
        progressDialog = new ProgressDialog(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LeaveActivity.this,LeaveStatus.class));
                finish();
            }
        });
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        LeaveActivity.this
                        , android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        , dateSetListener1,year,month,day
                );
                datePickerDialog.getWindow().setBackgroundDrawable(new
                        ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
                calendar1.setTime(new Date());
            }
        });
        dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = year + "-" + month + "-" + day;
                String sDate =  day + "/" + month + "/" + year;
                Start = sDate;
                start.setText(date);
            }
        };
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        LeaveActivity.this
                        , android.R.style.Theme_Holo_Light_Dialog_MinWidth
                        , dateSetListener2,year,month,day
                );
                datePickerDialog.getWindow().setBackgroundDrawable(new
                        ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });
        dateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                month = month + 1;
                String date = year + "-" + month + "-" + day;
                String sDate =  day + "/" + month + "/" + year;
                End = sDate;
                end.setText(date);
            }
        };
        compute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sDate = Start;
                String eDate = End;
                if(start.getText().toString().isEmpty() && end.getText().toString().isEmpty()){
                    Toast.makeText(LeaveActivity.this, "Error: Both date fields are empty!", Toast.LENGTH_SHORT).show();
                }else if(start.getText().toString().isEmpty()){
                    Toast.makeText(LeaveActivity.this, "Error: Start date is empty!", Toast.LENGTH_SHORT).show();
                }else if (end.getText().toString().isEmpty()){
                    Toast.makeText(LeaveActivity.this, "Error: End date is empty!", Toast.LENGTH_SHORT).show();
                }else {
                    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        Date date1 = simpleDateFormat1.parse(sDate);
                        Date date2 = simpleDateFormat1.parse(eDate);

                        long startDate = date1.getTime();
                        long endDate = date2.getTime();

                        if (startDate <= endDate) {
                            long diff = endDate - startDate;
                            long result = (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1);
                            String Res = String.valueOf(result);
                            res.setText(Res);
                        } else {
                            start.setText("");
                            end.setText("");
                            Toast.makeText(LeaveActivity.this, "The End date should not be smaller than the Starting date", Toast.LENGTH_SHORT).show();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    calendar1.add(Calendar.DATE, 5);
                    testDatelimit = simpleDateFormat1.format(calendar1.getTime());
                }
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String startDate = start.getText().toString().trim();
                String endDate = end.getText().toString().trim();
                String total = res.getText().toString().trim();
                //String reasonText = reason.getText().toString().trim();
                String creditsbalance = balance.getText().toString().trim();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                if(!startDate.isEmpty() && !endDate.isEmpty()
                        && !total.isEmpty()){
                    try {
                        Date testDate1 = sdf.parse(testDatelimit);
                        Date testDate2 = sdf.parse(Start);
                        if(!testDate2.after(testDate1)){
                            Toast.makeText(LeaveActivity.this, "Error: start date must be 5 days advance from current date", Toast.LENGTH_SHORT).show();
                            res.setText("");
                            start.setError("Must be 5 days advanced!");
                        }else{
                            float intBalance = Float.parseFloat(creditsbalance);
                            float intTotal = Float.parseFloat(total);
                            float remainingBalance = intBalance - intTotal;
                            String totalBalance = String.valueOf(remainingBalance);

                            requestLeave(startDate,endDate,total,type_string);
                            updateCredit(totalBalance);
                            //updateTypeCredit(totalTypeBalance);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    /*if(Start.compareTo(testDatelimit)<0){
                        //Toast.makeText(LeaveActivity.this,"Start date should be 5 days advanced from the day of request",Toast.LENGTH_SHORT).show();
                        //Toast.makeText(LeaveActivity.this, test1, Toast.LENGTH_SHORT).show();

                    }else{
                        float intBalance = Float.parseFloat(creditsbalance);
                        float intTotal = Float.parseFloat(total);
                        float remainingBalance = intBalance - intTotal;
                        String totalBalance = String.valueOf(remainingBalance);

                        //Toast.makeText(LeaveActivity.this, test1, Toast.LENGTH_SHORT).show();
                        //requestLeave(startDate,endDate,total,type_string);
                        //updateCredit(totalBalance);
                        //updateTypeCredit(totalTypeBalance);
                    }*/
                }else if(startDate.isEmpty()){
                    start.setError("Starting date is required!");
                }else if(endDate.isEmpty()){
                    end.setError("End date is required!");
                }else if(total.isEmpty()){
                    Toast.makeText(LeaveActivity.this, "Click the 'COMPUTE' button", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //fetching leave categories
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                populateCat, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("categories");
                    for(int i = 0; i<jsonArray.length();i++){
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String categoryID = jsonObject.optString("cat_id");
                        String categoryName = jsonObject.optString("category");
                        categoryList.add(categoryName);
                        categoryId.add(categoryID);
                        category_index = categoryID;
                        categoryAdapter = new ArrayAdapter<>(LeaveActivity.this,
                                android.R.layout.simple_spinner_item,categoryList);
                        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCat.setAdapter(categoryAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        leavetypeQueue.add(jsonObjectRequest);
        spinnerCat.setOnItemSelectedListener(this);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getSelectedItem().toString();
                String selectedId = typeId.get(position);
                type_index = selectedId;
                type_string  = selectedType;
                //fetchleaveTypeCredits(type_string);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //fetching leave categories END


    }
    public void requestLeave(String startDate, String endDate,
                             String total,String reasonText){
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, requestLeave, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                progressDialog.dismiss();
                                final String Title =  "Request for leave received.";
                                final String Message = "Greetings\n\n\tYour request has been received, please wait for the admin to respond for your request.";
                                JavaMailAPI javaMailAPI = new JavaMailAPI(LeaveActivity.this,userEmail,Title,Message);
                                javaMailAPI.execute();
                                if(javaMailAPI.isSent(true)){
                                    AlertDialog.Builder alert = new AlertDialog.Builder(LeaveActivity.this);
                                    alert.setTitle("Request successfully sent!");
                                    // alert.setMessage("Message");

                                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            start.setText("");
                                            end.setText("");
                                            res.setText("");
                                            //reason.setText("");
                                        }
                                    });
                                    alert.show();
                                }

                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(LeaveActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(LeaveActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("empid",empid);
                params.put("name",Fullname);
                params.put("email",userEmail);
                params.put("startDate",startDate);
                params.put("endDate",endDate);
                params.put("total",total);
                params.put("reason",reasonText);
                params.put("balance_id",balance_id);
                params.put("status","Pending");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void fetchCredits(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, fetchLeaveTypeCredits, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String Balance = object.getString("credits").trim();
                                    balance_id = object.getString("id").trim();
                                    balance.setText(Balance);
                                }
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(LeaveActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LeaveActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("empid",empid);
                params.put("cat_id",category_index);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    /*public void fetchleaveTypeCredits(String type){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, fetchLeaveTypeCredits, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String Balance = object.getString("credits").trim();
                                    balance_id = object.getString("id").trim();
                                }
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(LeaveActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LeaveActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("empid",empid);
                params.put("type",type);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }*/
    public void updateCredit(String balance){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateCredits, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(LeaveActivity.this, "Balance Updated!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(LeaveActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LeaveActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("empid",empid);
                params.put("balance_id",category_index);
                params.put("credits",balance);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    /*public void updateTypeCredit(String balance){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateTypeCredits, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                fetchCredits();
                                fetchleaveTypeCredits(type_string);
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(LeaveActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LeaveActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("empid",empid);
                params.put("credits",balance);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }*/

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent.getId() == R.id.spinnerCat){
            typeList.clear();
            String selectedCat = parent.getSelectedItem().toString();
            String selectedId = categoryId.get(position);
            category_index = selectedId;
            fetchCredits();
            String url = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/pop_leaveType.php?category_id="
                    +selectedId;
            leavetypeQueue = Volley.newRequestQueue(this);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray jsonArray = response.getJSONArray("leave_type");
                        for(int i = 0; i<jsonArray.length();i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String typeID = jsonObject.optString("type_id");
                            String typeName = jsonObject.optString("leave_type");
                            typeList.add(typeName);
                            typeId.add(typeID);
                            typeAdapter = new ArrayAdapter<>(LeaveActivity.this,
                                    android.R.layout.simple_spinner_item,typeList);
                            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerType.setAdapter(typeAdapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            leavetypeQueue.add(jsonObjectRequest);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                            Toast.makeText(LeaveActivity.this, "Error Reading detail!"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LeaveActivity.this, "Error Reading detail!"+error, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(LeaveActivity.this);
        requestQueue.add(stringRequest);
    }
}
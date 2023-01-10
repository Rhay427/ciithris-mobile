package com.example.capstone_employee.salary;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.example.capstone_employee.mail.Mails;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SalaryActivity extends AppCompatActivity {
    Button backBtn;
    Salary salary;
    SalaryAdapter salaryAdapter;
    ListView listView;
    String empid;
    SessionManager sessionManager;
    SwipeRefreshLayout swipeRefreshLayout;
    ShimmerFrameLayout shimmerFrameLayout;
    private static String getPayrollUrl = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/retrievePayroll.php";
    public static ArrayList<Salary> payrollList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_salary);
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        empid = user.get(sessionManager.EMPID);
        listView = findViewById(R.id.list_payroll);
        salaryAdapter = new SalaryAdapter(this,payrollList);
        listView.setAdapter(salaryAdapter);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        shimmerFrameLayout = findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmer();
        retrievePayroll();
        backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
                finish();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrievePayroll();
                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                CharSequence[] item = {"View Payslip","Dismiss"};
                builder.setTitle("What to do?");
                builder.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0:
                                Intent intent = new Intent(SalaryActivity.this,ViewPayslip.class);
                                intent.putExtra("empid",payrollList.get(position).getEmpid());
                                intent.putExtra("fullname",payrollList.get(position).getFullname());
                                intent.putExtra("branch",payrollList.get(position).getBranch());
                                intent.putExtra("department",payrollList.get(position).getDepartment());
                                intent.putExtra("designation",payrollList.get(position).getDesignation());
                                intent.putExtra("b_salary",payrollList.get(position).getB_salary());
                                intent.putExtra("range_start",payrollList.get(position).getRange_start());
                                intent.putExtra("range_end",payrollList.get(position).getRange_end());
                                intent.putExtra("hours_total",payrollList.get(position).getHours_total());
                                intent.putExtra("leave_total",payrollList.get(position).getLeave_total());
                                intent.putExtra("tax",payrollList.get(position).getTax());
                                intent.putExtra("sss",payrollList.get(position).getSss());
                                intent.putExtra("philhealth",payrollList.get(position).getPhilhealth());
                                intent.putExtra("pag_ibig",payrollList.get(position).getPag_ibig());
                                intent.putExtra("leave_deduct",payrollList.get(position).getLeave_deduct());
                                intent.putExtra("other_deduct",payrollList.get(position).getOther_deduct());
                                intent.putExtra("total_pay",payrollList.get(position).getTotal_pay());
                                intent.putExtra("datecreated",payrollList.get(position).getDatecreated());
                                startActivity(intent);
                                finish();
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
    public void retrievePayroll(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getPayrollUrl, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        payrollList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if(success.equals("1")){
                                for(int i = 0;i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String id = object.getString("id");
                                    String empid = object.getString("empid");
                                    String fullName = object.getString("fullName");
                                    String branch = object.getString("branch");
                                    String department = object.getString("department");
                                    String designation = object.getString("designation");
                                    String b_salary = object.getString("b_salary");
                                    String range_start = object.getString("range_start");
                                    String range_end = object.getString("range_end");
                                    String hours_total = object.getString("hours_total");
                                    String leave_total = object.getString("leave_total");
                                    String tax = object.getString("tax");
                                    String sss = object.getString("sss");
                                    String philhealth = object.getString("philhealth");
                                    String pag_ibig = object.getString("pag_ibig");
                                    String leave_deduct = object.getString("leave_deduct");
                                    String other_deduct = object.getString("other_deduct");
                                    String total_pay = object.getString("total_pay");
                                    String datecreated = object.getString("datecreated");
                                    salary = new Salary(id,empid,fullName,branch,department,designation,
                                            b_salary,range_start,range_end,hours_total,leave_total,
                                            tax,sss,philhealth,pag_ibig,leave_deduct,other_deduct,
                                            total_pay,datecreated);
                                    payrollList.add(salary);
                                    salaryAdapter.notifyDataSetChanged();
                                }
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                            Toast.makeText(SalaryActivity.this, e.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SalaryActivity.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("empid", empid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
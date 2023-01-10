package com.example.capstone_employee.recruit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.example.capstone_employee.mail.MailActivity;
import com.example.capstone_employee.ui.profile.AuthenticateActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RecruitActivity extends AppCompatActivity {
    Button back,submit;
    EditText jobSpec,fullName,gender,birthDate,contactNum,
    email,specialization,yearsOExp,currentComp,designation;
    SessionManager sessionManager;
    String empid,userEmail;
    ProgressDialog progressDialog;
    private static String recruitURL = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/recruit_API.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_recruit);
        back = findViewById(R.id.btn_back);
        submit = findViewById(R.id.BtnSubmit);
        jobSpec = findViewById(R.id.jobSpecification);
        fullName = findViewById(R.id.fullName);
        gender = findViewById(R.id.Gender);
        contactNum = findViewById(R.id.contactNum);
        email = findViewById(R.id.emailAdd);
        specialization = findViewById(R.id.specialization);
        yearsOExp = findViewById(R.id.yearsExp);
        currentComp = findViewById(R.id.currentComp);
        designation = findViewById(R.id.designation);
        birthDate = findViewById(R.id.birthDate);
        birthDate.setInputType(InputType.TYPE_NULL);
        sessionManager = new SessionManager(this);
        HashMap<String, String> userID = sessionManager.getUserDetail();
        empid = userID.get(sessionManager.EMPID);
        HashMap<String, String> user_email = sessionManager.getEmail();
        userEmail = user_email.get(sessionManager.CMAIN_EMAIL);
        progressDialog = new ProgressDialog(this);
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
                finish();
            }
        });
        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RecruitActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(0);
                        calendar.set(year, month,day,0,0,0);
                        Date chosenDate = calendar.getTime();
                        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
                        String date = dateFormat.format(chosenDate);
                        birthDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jobspec = jobSpec.getText().toString().trim();
                String name = fullName.getText().toString().trim();
                String Gender = gender.getText().toString().trim();
                String contact = contactNum.getText().toString().trim();
                String Email = email.getText().toString().trim();
                String spec = specialization.getText().toString().trim();
                String years = yearsOExp.getText().toString().trim();
                String currentC = currentComp.getText().toString().trim();
                String des = designation.getText().toString().trim();
                String birth = birthDate.getText().toString().trim();
                if(!jobspec.isEmpty() && !name.isEmpty() && !Gender.isEmpty() && !contact.isEmpty()
                && !Email.isEmpty() && !spec.isEmpty() && !years.isEmpty() && !currentC.isEmpty()
                && !des.isEmpty() && !birth.isEmpty()){
                    insertRecruit(jobspec,name,Gender,birth,contact,Email,spec,years,currentC,des);
                }else if(jobspec.isEmpty()){
                    jobSpec.setError("Job Specification is required");
                }
                else if(name.isEmpty()){
                    fullName.setError("Full Name is required");
                }else if(Gender.isEmpty()){
                    gender.setError("gender is required");
                }else if(contact.isEmpty()){
                    contactNum.setError("Contact Number is required");
                }else if(Email.isEmpty()){
                    email.setError("Email is required");
                }else if(spec.isEmpty()){
                    specialization.setError("Specialization is required");
                }else if(years.isEmpty()){
                    yearsOExp.setError("Years of Experience is required");
                }else if(currentC.isEmpty()){
                    currentComp.setError("Current Company is required");
                }else if(des.isEmpty()){
                    designation.setError("Designation is required");
                }else if(birth.isEmpty()){
                    birthDate.setError("Date of Birth is required");
                }else{
                    Toast.makeText(RecruitActivity.this, "All fields are required!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void insertRecruit(String jobspec,String name,String Gender,String birth,String contact,
                              String Email,String spec,String years,String currentC,String des){
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, recruitURL, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                final String Title =  "Recruitment received.";
                                final String Message = "Greetings\n\n\tYour recruitment has been received, please wait for the admin to respond.";
                                JavaMailAPI javaMailAPI = new JavaMailAPI(RecruitActivity.this,userEmail,Title,Message);
                                javaMailAPI.execute();
                                if(javaMailAPI.isSent(true)){
                                    AlertDialog.Builder alert = new AlertDialog.Builder(RecruitActivity.this);
                                    alert.setTitle("Recruitment successfully processed!");
                                    // alert.setMessage("Message");

                                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            jobSpec.setText("");
                                            fullName.setText("");
                                            gender.setText("");
                                            contactNum.setText("");
                                            email.setText("");
                                            specialization.setText("");
                                            yearsOExp.setText("");
                                            currentComp.setText("");
                                            designation.setText("");
                                            birthDate.setText("");
                                        }
                                    });
                                    alert.show();                                }                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            Toast.makeText(RecruitActivity.this, "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(RecruitActivity.this, "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("empid",empid);
                params.put("jobSpec",jobspec);
                params.put("name",name);
                params.put("gender",Gender);
                params.put("birthdate",birth);
                params.put("contact",contact);
                params.put("recEmail",Email);
                params.put("spec",spec);
                params.put("yearsOfExp",years);
                params.put("currentComp",currentC);
                params.put("designation",des);
                params.put("status","Pending");
                params.put("state","1");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
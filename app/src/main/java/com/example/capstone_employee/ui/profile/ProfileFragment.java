package com.example.capstone_employee.ui.profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private ProfileViewModel profileViewModel;
    String empid,
    mEmail,
    aEmail;
    Button logout,
        editPers,
        editAcc;
    ShapeableImageView imageView;
    SessionManager sessionManager;
    TextView Username,
        timeSched,
        department,
        designation,
        fullname,
        mainContact,
        altContact,
        maritalStatus,
        dateofBirth,
        Religion,
        Nationality,
        Present,
        Permanent,
        mainEmail,
        secondEmail,
        m_auth,
        a_auth,
        request;
    LinearLayout request_layout;
    SwipeRefreshLayout onswipe;
    ProgressDialog progressDialog;
    private static String URL_READ = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/getuserDetail.php";
    private static String URL_REQUEST = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/requestEdit.php";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        sessionManager = new SessionManager(getContext());
        sessionManager.checkLogin();
        progressDialog = new ProgressDialog(getContext());
        request_layout = root.findViewById(R.id.send_request);
        request = root.findViewById(R.id.btn_request);
        editPers = root.findViewById(R.id.btn_editPers);
        editAcc = root.findViewById(R.id.btn_editAcc);
        logout = root.findViewById(R.id.btn_logout);
        Username = root.findViewById(R.id.txt_username);
        department = root.findViewById(R.id.txt_dept);
        designation = root.findViewById(R.id.txt_job);
        fullname = root.findViewById(R.id.txt_fullName);
        timeSched = root.findViewById(R.id.txt_timeSched);
        mainContact = root.findViewById(R.id.txt_mainContact);
        altContact = root.findViewById(R.id.txt_altContact);
        maritalStatus = root.findViewById(R.id.txt_mStatus);
        dateofBirth = root.findViewById(R.id.txt_dBirth);
        Religion = root.findViewById(R.id.txt_religion);
        Nationality = root.findViewById(R.id.txt_nationality);
        Present = root.findViewById(R.id.txt_presentAddress);
        Permanent = root.findViewById(R.id.txt_permanentAddress);
        mainEmail = root.findViewById(R.id.txt_mainEmail);
        secondEmail = root.findViewById(R.id.txt_secondEmail);
        imageView = root.findViewById(R.id.circular);
        m_auth = root.findViewById(R.id.main_auth);
        a_auth = root.findViewById(R.id.alt_auth);
        onswipe = root.findViewById(R.id.swipeLayout);
        HashMap<String, String> user = sessionManager.getUserDetail();
        empid = user.get(sessionManager.EMPID);
        onswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getuserDetail();
                onswipe.setRefreshing(false);
            }
        });
        editPers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EditActivity.class));
            }
        });
        editAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AccountActivity.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                alert.setTitle("Do you want to logout?");
                // alert.setMessage("Message");

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        sessionManager.logout();
                    }
                });

                alert.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });

                alert.show();
            }
        });
        m_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AuthenticateActivity.class);
                intent.putExtra("email",mEmail);
                intent.putExtra("determine","main");
                startActivity(intent);
            }
        });
        a_auth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AuthenticateActivity.class);
                intent.putExtra("email",aEmail);
                intent.putExtra("determine","alter");
                startActivity(intent);
            }
        });
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRequest();
            }
        });
        return root;
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
                                    String timeIn = object.getString("time_in").trim();
                                    String timeOut = object.getString("time_out").trim();
                                    String sched = timeIn+" - "+timeOut;
                                    String mcontact = object.getString("mcontact").trim();
                                    String acontact = object.getString("acontact").trim();
                                    String mstatus = object.getString("mstatus").trim();
                                    String birthdate = object.getString("birthdate").trim();
                                    String religion = object.getString("religion").trim();
                                    String nationality = object.getString("nationality").trim();
                                    String present = object.getString("present").trim();
                                    String permanent = object.getString("permanent").trim();
                                    String memail = object.getString("memail").trim();
                                    String aemail = object.getString("aemail").trim();
                                    String userName = object.getString("username").trim();
                                    String Department = object.getString("department").trim();
                                    String Designation = object.getString("designation").trim();
                                    String profimage = object.getString("profImage").trim();
                                    String mAuth = object.getString("is_mauth").trim();
                                    String aAuth = object.getString("is_aauth").trim();
                                    String editAuth = object.getString("edit_auth").trim();
                                    String imagePath = "http://rhaysabaria-001-site1.ftempurl.com/"+profimage;
                                    if (editAuth.equals("1")) {
                                        request_layout.setVisibility(View.GONE);
                                        editAcc.setVisibility(View.VISIBLE);
                                    }else if(editAuth.equals("0")){
                                        request_layout.setVisibility(View.VISIBLE);
                                        editAcc.setVisibility(View.GONE);
                                    }else if(editAuth.equals("2")){
                                        request_layout.setVisibility(View.GONE);
                                    }
                                    Picasso.get().load(imagePath).fit().into(imageView);
                                    mEmail = memail;
                                    aEmail = aemail;
                                    fullname.setText(fullName);
                                    timeSched.setText(sched);
                                    mainContact.setText(mcontact);
                                    altContact.setText(acontact);
                                    maritalStatus.setText(mstatus);
                                    dateofBirth.setText(birthdate);
                                    Religion.setText(religion);
                                    Nationality.setText(nationality);
                                    Present.setText(present);
                                    Permanent.setText(permanent);
                                    mainEmail.setText(memail);
                                    secondEmail.setText(aemail);
                                    Username.setText(userName);
                                    designation.setText(Designation);
                                    department.setText(Department);
                                    if(mAuth.equals("0")){
                                        m_auth.setVisibility(View.VISIBLE);
                                    }else{
                                        m_auth.setVisibility(View.GONE);
                                    }
                                    if(aAuth.equals("0")){
                                        a_auth.setVisibility(View.VISIBLE);
                                    }else{
                                        a_auth.setVisibility(View.GONE);
                                    }
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
    public void updateRequest(){
        progressDialog.setTitle("Sending....");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REQUEST, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(getContext(),
                                        "Request Sent!", Toast.LENGTH_SHORT).show();
                                final String title =  "Request to edit account details.";
                                final String message = "Greetings\n\n\tYour request to edit your account details " +
                                        "has been sent successfully, please wait for the authorization from the admin.";
                                JavaMailAPI javaMailAPI = new JavaMailAPI(getContext(),mEmail,title,message);
                                javaMailAPI.execute();
                                request_layout.setVisibility(View.GONE);
                                progressDialog.dismiss();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new
                Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Error: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("empid",empid);
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
    }
}
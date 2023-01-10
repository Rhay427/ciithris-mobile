package com.example.capstone_employee.mail;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
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
import com.example.capstone_employee.ui.home.TimeReport;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MailStatus extends AppCompatActivity {
    Mails mails;
    MailsAdapter mailsAdapter;
    ListView listView;
    String empid,email;
    Button back;
    SessionManager sessionManager;
    FloatingActionButton fab;
    SwipeRefreshLayout onswipe;
    ShimmerFrameLayout shimmerFrameLayout;
    private static String getMailsUrl = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/retrieveMails.php";
    public static ArrayList<Mails> mailList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_mail_status);
        sessionManager = new SessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetail();
        empid = user.get(sessionManager.EMPID);
        HashMap<String, String> userEmail = sessionManager.getEmail();
        email = userEmail.get(sessionManager.CMAIN_EMAIL);
        listView = findViewById(R.id.list_statusMail);
        back = findViewById(R.id.btn_back);
        mailsAdapter = new MailsAdapter(this,mailList);
        listView.setAdapter(mailsAdapter);
        fab = findViewById(R.id.btn_fab);
        onswipe = findViewById(R.id.swipe_refresh);
        shimmerFrameLayout = findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmer();
        retrieveMails();
        onswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveMails();
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
                startActivity(new Intent(MailStatus.this,MailActivity.class));
                finish();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                CharSequence[] item = {"View Mail","Dismiss"};
                builder.setTitle("What to do?");
                builder.setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch(i){
                            case 0:
                                Intent intent = new Intent(MailStatus.this, ViewMail.class);
                                intent.putExtra("title",mailList.get(position).getTitle());
                                intent.putExtra("message",mailList.get(position).getMessage());
                                intent.putExtra("response",mailList.get(position).getResponse());
                                intent.putExtra("datestamp",mailList.get(position).getDatestamp());
                                intent.putExtra("mainID",mailList.get(position).getId());
                                intent.putExtra("empid",empid);
                                intent.putExtra("email",email);
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
    public void retrieveMails(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getMailsUrl, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        onswipe.setVisibility(View.VISIBLE);
                        mailList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if(success.equals("1")){
                                for(int i = 0;i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String id = object.getString("id");
                                    String fullname = object.getString("fullname");
                                    String email = object.getString("email");
                                    String title = object.getString("title");
                                    String message = object.getString("message");
                                    String Response = object.getString("response");
                                    String datestamp = object.getString("datestamp");
                                    String status = object.getString("status");
                                    mails = new Mails(id,fullname,email,title,message,Response,datestamp,status);
                                    mailList.add(mails);
                                    mailsAdapter.notifyDataSetChanged();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MailStatus.this, e.toString().trim(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MailStatus.this, error.toString().trim(), Toast.LENGTH_SHORT).show();
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
}
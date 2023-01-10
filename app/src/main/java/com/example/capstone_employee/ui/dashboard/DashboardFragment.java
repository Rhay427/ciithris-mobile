package com.example.capstone_employee.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.capstone_employee.attendance.AttendanceActivity;
import com.example.capstone_employee.leave.LeaveStatus;
import com.example.capstone_employee.mail.MailStatus;
import com.example.capstone_employee.R;
import com.example.capstone_employee.recruit.RecruitActivity;
import com.example.capstone_employee.ResignActivity;
import com.example.capstone_employee.salary.SalaryActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    AnnouncementAdapter adapter;
    Announcement announcement;
    public static ArrayList<Announcement> announcements = new ArrayList<>();
    ListView listView;
    CardView recruit,
            leave,
            //salary,
            resign,
            attendance,
            mail;
    SwipeRefreshLayout onswipe;
    ShimmerFrameLayout shimmerFrameLayout;
    private static String getAnnounceUrl = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/retrieveAnnounce.php";
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        recruit = root.findViewById(R.id.card_recruit);
        leave = root.findViewById(R.id.card_leave);
        attendance = root.findViewById(R.id.card_att);
        //salary = root.findViewById(R.id.card_salary);
        resign = root.findViewById(R.id.card_resign);
        mail = root.findViewById(R.id.card_mail);
        listView = root.findViewById(R.id.list_Announce);
        adapter = new AnnouncementAdapter(getContext(),announcements);
        onswipe = root.findViewById(R.id.swipe_refresh);
        shimmerFrameLayout = root.findViewById(R.id.shimmerFrameLayout);

        shimmerFrameLayout.startShimmer();
        onswipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                shimmerFrameLayout.startShimmer();
                shimmerFrameLayout.setVisibility(View.VISIBLE);
                retrieveAnnounce();
                onswipe.setRefreshing(false);
            }
        });
        recruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), RecruitActivity.class));
            }
        });
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LeaveStatus.class));
            }
        });
        attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AttendanceActivity.class));
            }
        });
        /*salary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), SalaryActivity.class));
            }
        });*/
        resign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), ResignActivity.class));
            }
        });
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MailStatus.class));
            }
        });
        listView.setAdapter(adapter);
        retrieveAnnounce();

        return root;
    }
    //Fetch announcements
    public void retrieveAnnounce(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, getAnnounceUrl, new
                Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        announcements.clear();
                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);
                        onswipe.setVisibility(View.VISIBLE);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if(success.equals("1")){
                                for(int i = 0;i<jsonArray.length();i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String title = object.getString("title");
                                    String author = object.getString("author");
                                    String desc = object.getString("description");
                                    String datestamp = object.getString("datestamp");

                                    announcement = new Announcement(title,author,desc,datestamp);
                                    announcements.add(announcement);
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
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
        retrieveAnnounce();
    }
}
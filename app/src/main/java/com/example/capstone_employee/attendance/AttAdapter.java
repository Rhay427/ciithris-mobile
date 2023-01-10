package com.example.capstone_employee.attendance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.capstone_employee.R;

import java.util.List;

public class AttAdapter extends ArrayAdapter<AttendanceList> {
    Context context;
    List<AttendanceList> AttList;

    public AttAdapter(@NonNull Context context, List<AttendanceList> Attlist) {
        super(context, R.layout.attendancelist, Attlist);
        this.context = context;
        this.AttList = Attlist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendancelist,null,true);
        TextView tv_date = view.findViewById(R.id.date);
        TextView tv_lateStatus = view.findViewById(R.id.txt_lateStatus);
        TextView tv_timein = view.findViewById(R.id.txt_timein);
        TextView tv_timeout = view.findViewById(R.id.txt_timeout);
        TextView tv_hours = view.findViewById(R.id.txt_hours);
        tv_date.setText(AttList.get(position).getDateStamp());
        tv_lateStatus.setText(AttList.get(position).getLateStatus());
        tv_timein.setText(AttList.get(position).getTimeIn());
        tv_timeout.setText(AttList.get(position).getTimeOut());
        tv_hours.setText(AttList.get(position).getHours());
        return view;
    }
}

package com.example.capstone_employee.ui.home;

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

public class AttendanceAdapter extends ArrayAdapter<Attendance>{
    Context context;
    List<Attendance> attendanceList;

    public AttendanceAdapter(@NonNull Context context, List<Attendance> attendanceList) {
        super(context, R.layout.attendance_layout, attendanceList);
        this.context = context;
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_layout,null,true);
        TextView tv_timeIn = view.findViewById(R.id.txt_timeIn);
        TextView tv_timeOut = view.findViewById(R.id.txt_timeOut);
        TextView tv_hours = view.findViewById(R.id.txt_hours);
        tv_timeIn.setText(attendanceList.get(position).getTimeIn());
        tv_timeOut.setText(attendanceList.get(position).getTimeOut());
        tv_hours.setText(attendanceList.get(position).getHours());
        return view;
    }
}

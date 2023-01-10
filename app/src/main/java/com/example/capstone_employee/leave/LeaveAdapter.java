package com.example.capstone_employee.leave;

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

public class LeaveAdapter extends ArrayAdapter<Leave> {
    Context context;
    List<Leave> leaveList;
    public LeaveAdapter(@NonNull Context context, List<Leave> leaveList) {
        super(context, R.layout.leavestatus_layout,leaveList);
        this.context = context;
        this.leaveList = leaveList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leavestatus_layout,null,true);
        TextView tv_date = view.findViewById(R.id.date);
        TextView tv_start = view.findViewById(R.id.txt_startDate);
        TextView tv_end = view.findViewById(R.id.txt_endDate);
        TextView tv_total = view.findViewById(R.id.txt_total);
        TextView tv_reason = view.findViewById(R.id.txt_reason);
        TextView tv_status = view.findViewById(R.id.txt_status);
        TextView tv_remarks = view.findViewById(R.id.txt_remarks);
        tv_date.setText(leaveList.get(position).getDatestamp());
        tv_start.setText(leaveList.get(position).getStartdate());
        tv_end.setText(leaveList.get(position).getEnddate());
        tv_total.setText(leaveList.get(position).getTotal());
        tv_reason.setText(leaveList.get(position).getReason());
        tv_status.setText(leaveList.get(position).getStatus());
        tv_remarks.setText(leaveList.get(position).getRemarks());
        return view;
    }
}

package com.example.capstone_employee.salary;

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

public class SalaryAdapter extends ArrayAdapter<Salary> {
    Context context;
    List<Salary> salarylist;
    public SalaryAdapter(@NonNull Context context, List<Salary> salarylist) {
        super(context, R.layout.salarystatus_layout,salarylist);
        this.context = context;
        this.salarylist = salarylist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.salarystatus_layout,null,true);
        TextView tv_range = view.findViewById(R.id.Range);
        TextView tv_pay = view.findViewById(R.id.txtpay);
        TextView tv_date = view.findViewById(R.id.date);
        tv_range.setText(salarylist.get(position).getRange_start()+ " | " +salarylist.get(position).getRange_end());
        tv_pay.setText(salarylist.get(position).getTotal_pay());
        tv_date.setText(salarylist.get(position).getDatecreated());
        return view;
    }
}

package com.example.capstone_employee.salary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.capstone_employee.R;

public class ViewPayslip extends AppCompatActivity {
    TextView empid, fullname, branch, department, designation,
    b_salary, rangestart, rangeend, totalhours, totalleave,
    tax, sss, philhealth, pagibig, leavededuct, otherdeduct,
    totalPay, datecreated;
    Button backBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_view_payslip);
        backBtn = findViewById(R.id.btn_back);
        empid = findViewById(R.id.tx_empid);
        fullname = findViewById(R.id.tx_fullname);
        branch = findViewById(R.id.tx_branch);
        department = findViewById(R.id.tx_department);
        designation = findViewById(R.id.tx_designation);
        b_salary = findViewById(R.id.tx_bsalary);
        rangestart = findViewById(R.id.tx_start);
        rangeend = findViewById(R.id.tx_end);
        totalhours = findViewById(R.id.tx_totalhours);
        totalleave = findViewById(R.id.tx_totalleave);
        tax = findViewById(R.id.tx_tax);
        sss = findViewById(R.id.tx_sss);
        philhealth = findViewById(R.id.tx_philhealth);
        pagibig = findViewById(R.id.tx_pagibig);
        leavededuct = findViewById(R.id.tx_leavededuct);
        otherdeduct = findViewById(R.id.tx_otherdeduct);
        totalPay = findViewById(R.id.tx_totalpay);
        datecreated = findViewById(R.id.tx_datecreated);

        //get intent data
        empid.setText(getIntent().getStringExtra("empid"));
        fullname.setText(getIntent().getStringExtra("fullname"));
        branch.setText(getIntent().getStringExtra("branch"));
        department.setText(getIntent().getStringExtra("department"));
        designation.setText(getIntent().getStringExtra("designation"));
        b_salary.setText(getIntent().getStringExtra("b_salary"));
        rangestart.setText(getIntent().getStringExtra("range_start"));
        rangeend.setText(getIntent().getStringExtra("range_end"));
        totalhours.setText(getIntent().getStringExtra("hours_total"));
        totalleave.setText(getIntent().getStringExtra("leave_total"));
        tax.setText(getIntent().getStringExtra("tax"));
        sss.setText(getIntent().getStringExtra("sss"));
        philhealth.setText(getIntent().getStringExtra("philhealth"));
        pagibig.setText(getIntent().getStringExtra("pag_ibig"));
        leavededuct.setText(getIntent().getStringExtra("leave_deduct"));
        otherdeduct.setText(getIntent().getStringExtra("other_deduct"));
        totalPay.setText(getIntent().getStringExtra("total_pay"));
        datecreated.setText(getIntent().getStringExtra("datecreated"));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ViewPayslip.this,SalaryActivity.class));
                finish();
            }
        });
    }
}
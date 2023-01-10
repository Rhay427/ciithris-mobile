package com.example.capstone_employee.salary;

public class Salary {
    private String id,empid,fullname,branch,department,
            designation,b_salary,range_start,range_end,
            hours_total,leave_total,tax,sss,philhealth,
            pag_ibig,leave_deduct,other_deduct,total_pay,
            datecreated;

    public Salary() {
    }

    public Salary(
            String id,
            String empid,
            String fullname,
            String branch,
            String department,
            String designation,
            String b_salary,
            String range_start,
            String range_end,
            String hours_total,
            String leave_total,
            String tax,
            String sss,
            String philhealth,
            String pag_ibig,
            String leave_deduct,
            String other_deduct,
            String total_pay,
            String datecreated){
        this.id = id;
        this.empid = empid;
        this.fullname = fullname;
        this.branch = branch;
        this.department = department;
        this.designation = designation;
        this.b_salary = b_salary;
        this.range_start = range_start;
        this.range_end = range_end;
        this.hours_total = hours_total;
        this.leave_total = leave_total;
        this.tax = tax;
        this.sss = sss;
        this.philhealth = philhealth;
        this.pag_ibig = pag_ibig;
        this.leave_deduct = leave_deduct;
        this.other_deduct = other_deduct;
        this.total_pay = total_pay;
        this.datecreated = datecreated;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getB_salary() {
        return b_salary;
    }

    public void setB_salary(String b_salary) {
        this.b_salary = b_salary;
    }

    public String getRange_start() {
        return range_start;
    }

    public void setRange_start(String range_start) {
        this.range_start = range_start;
    }

    public String getRange_end() {
        return range_end;
    }

    public void setRange_end(String range_end) {
        this.range_end = range_end;
    }

    public String getHours_total() {
        return hours_total;
    }

    public void setHours_total(String hours_total) {
        this.hours_total = hours_total;
    }

    public String getLeave_total() {
        return leave_total;
    }

    public void setLeave_total(String leave_total) {
        this.leave_total = leave_total;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getSss() {
        return sss;
    }

    public void setSss(String sss) {
        this.sss = sss;
    }

    public String getPhilhealth() {
        return philhealth;
    }

    public void setPhilhealth(String philhealth) {
        this.philhealth = philhealth;
    }

    public String getPag_ibig() {
        return pag_ibig;
    }

    public void setPag_ibig(String pag_ibig) {
        this.pag_ibig = pag_ibig;
    }

    public String getLeave_deduct() {
        return leave_deduct;
    }

    public void setLeave_deduct(String leave_deduct) {
        this.leave_deduct = leave_deduct;
    }

    public String getOther_deduct() {
        return other_deduct;
    }

    public void setOther_deduct(String other_deduct) {
        this.other_deduct = other_deduct;
    }

    public String getTotal_pay() {
        return total_pay;
    }

    public void setTotal_pay(String total_pay) {
        this.total_pay = total_pay;
    }

    public String getDatecreated() {
        return datecreated;
    }

    public void setDatecreated(String datecreated) {
        this.datecreated = datecreated;
    }
}

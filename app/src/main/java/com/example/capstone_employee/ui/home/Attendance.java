package com.example.capstone_employee.ui.home;

public class Attendance {
    private String mainid,empid,fullname,timeIn,timeOut,hours,dateStamp;

    public Attendance() {
    }

    public Attendance(String mainid,String empid, String fullname, String timeIn, String timeOut,String hours, String dateStamp) {
        this.mainid = mainid;
        this.empid = empid;
        this.fullname = fullname;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.hours = hours;
        this.dateStamp = dateStamp;
    }

    public String getMainid() {
        return mainid;
    }

    public void setMainid(String mainid) {
        this.mainid = mainid;
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

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(String dateStamp) {
        this.dateStamp = dateStamp;
    }
}

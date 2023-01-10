package com.example.capstone_employee.attendance;

public class AttendanceList {
    private String id, empid, fullname, timeIn, timeOut, hours, dateStamp, lateStatus;

    public AttendanceList() {
    }

    public AttendanceList(String id, String empid, String fullname, String timeIn,
                          String timeOut, String hours, String dateStamp, String lateStatus) {
        this.id             = id;
        this.empid          = empid;
        this.fullname       = fullname;
        this.timeIn         = timeIn;
        this.timeOut        = timeOut;
        this.hours          = hours;
        this.dateStamp      = dateStamp;
        this.lateStatus     = lateStatus;
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

    public String getLateStatus() {
        return lateStatus;
    }

    public void setLateStatus(String lateStatus) {
        this.lateStatus = lateStatus;
    }
}

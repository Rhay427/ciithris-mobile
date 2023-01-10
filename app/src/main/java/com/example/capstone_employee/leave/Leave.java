package com.example.capstone_employee.leave;

public class Leave {
    private String id,email,startdate,enddate,total,reason,status,datestamp,remarks;

    public Leave() {
    }
    public Leave(String id, String email, String startdate, String enddate,
                 String total, String reason, String status, String datestamp, String remarks){
        this.id = id;
        this.email = email;
        this.startdate = startdate;
        this.enddate = enddate;
        this.total = total;
        this.reason = reason;
        this.status = status;
        this.datestamp = datestamp;
        this.remarks = remarks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDatestamp() {
        return datestamp;
    }

    public void setDatestamp(String datestamp) {
        this.datestamp = datestamp;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

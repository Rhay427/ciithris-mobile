package com.example.capstone_employee.mail;

public class Mails {
    private String id,fullname,email,title,message,response,datestamp,status;

    public Mails() {
    }

    public Mails(String id, String fullname,String email, String title, String message,
                 String response, String datestamp, String status) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.title = title;
        this.message = message;
        this.response = response;
        this.datestamp = datestamp;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getDatestamp() {
        return datestamp;
    }

    public void setDatestamp(String datestamp) {
        this.datestamp = datestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

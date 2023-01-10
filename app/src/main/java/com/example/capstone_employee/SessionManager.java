package com.example.capstone_employee;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String EMPID = "EMPID";
    public static final String USERNAME = "USERNAME";
    public static final String TIMED_IN = "TIMEIN";
    public static final String TIME = "TIME";
    public static final String TIMEID = "TIMEID";
    public static final String CMAIN_EMAIL = "MAINEMAIL";
    public SessionManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor = sharedPreferences.edit();

    }
    public void createSession(String empid, String username){
        editor.putBoolean(LOGIN,true);
        editor.putString(EMPID,empid);
        editor.putString(USERNAME,username);
        editor.apply();
    }
    public boolean isLoggin(){
        return sharedPreferences.getBoolean(LOGIN,false);
    }
    public void checkLogin(){
        if(!this.isLoggin()){
            Intent intent = new Intent(context,Splashscreen.class);
            context.startActivity(intent);
            ((BottomNav) context).finish();
        }
    }
    public HashMap<String, String> getUserDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(EMPID,sharedPreferences.getString(EMPID,null));
        user.put(USERNAME,sharedPreferences.getString(USERNAME,null));
        return user;
    }
    public void logout(){
        editor.clear();
        editor.commit();
        Intent intent = new Intent(context,MainActivity.class);
        context.startActivity(intent);
        ((BottomNav) context).finish();
    }
    public void setTimedIn(String time){
        editor.putBoolean(TIMED_IN,true);
        editor.putString(TIME,time);
        editor.apply();
    }
    public void setTimedID(String id){
        editor.putString(TIMEID,id);
        editor.apply();
    }
    public void setTimedOut(){
        editor.putBoolean(TIMED_IN,false);
        editor.apply();
    }
    public boolean isTimedIn(){
        return sharedPreferences.getBoolean(TIMED_IN,false);
    }
    public HashMap<String, String> getTimeDetail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(TIME,sharedPreferences.getString(TIME,null));
        return user;
    }
    public HashMap<String, String> getTimeID(){
        HashMap<String, String> user = new HashMap<>();
        user.put(TIMEID,sharedPreferences.getString(TIMEID,null));
        return user;
    }
    public void currentMEmail(String main){
        editor.putString(CMAIN_EMAIL,main);
        editor.apply();
    }
    public HashMap<String, String> getEmail(){
        HashMap<String, String> user = new HashMap<>();
        user.put(CMAIN_EMAIL,sharedPreferences.getString(CMAIN_EMAIL,null));
        return user;
    }

}

package com.example.capstone_employee.ui.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.capstone_employee.R;
import com.example.capstone_employee.SessionManager;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditActivity extends AppCompatActivity {
    private static final String TAG = EditActivity.class.getSimpleName();
    private Button back,
        save,
        btn_upload;
    ShapeableImageView profImage;
    private EditText firstname,
        middlename,
        lastname,
        mainContact,
        altContact,
        maritalStatus,
        dateofBirth,
        Religion,
        Nationality,
        Present,
        Permanent;
    SessionManager sessionManager;
    String empid,
        username,
        dept,
        des;
    ProgressDialog progressDialog;
    private Bitmap bitmap;
    private static String URL_READ = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/getuserDetail.php";
    private static String URL_EDIT = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/editPers_Detail.php";
    private static String URL_UPLOAD = "http://rhaysabaria-001-site1.ftempurl.com/ciitmobile/uploadImage.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit);
        sessionManager = new SessionManager(getApplicationContext());
        sessionManager.checkLogin();
        HashMap<String, String> user = sessionManager.getUserDetail();
        empid = user.get(sessionManager.EMPID);
        username = user.get(sessionManager.USERNAME);
        back = findViewById(R.id.btn_back);
        profImage = findViewById(R.id.circular);
        dateofBirth = findViewById(R.id.txtdBirth);
        dateofBirth.setInputType(InputType.TYPE_NULL);
        firstname = findViewById(R.id.txtfirstName);
        middlename = findViewById(R.id.txtmiddleName);
        lastname = findViewById(R.id.txtlastname);
        mainContact = findViewById(R.id.txtmainContact);
        altContact = findViewById(R.id.txtaltContact);
        maritalStatus = findViewById(R.id.txtmStatus);
        Religion = findViewById(R.id.txtReligion);
        Nationality = findViewById(R.id.txtNation);
        Present = findViewById(R.id.txt_presentAddress);
        Permanent = findViewById(R.id.txt_permanentAddress);
        save = findViewById(R.id.Btn_Edit);
        btn_upload = findViewById(R.id.changePic);
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
                finish();
            }
        });
        dateofBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(0);
                        calendar.set(year, month,day,0,0,0);
                        String date = year + "-" + month + "-" + day;
                        dateofBirth.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = firstname.getText().toString().trim();
                String middleName = middlename.getText().toString().trim();
                String lastName = lastname.getText().toString().trim();
                String mainC = mainContact.getText().toString().trim();
                String altC = altContact.getText().toString().trim();
                String mstatus = maritalStatus.getText().toString().trim();
                String religion = Religion.getText().toString().trim();
                String nationality = Nationality.getText().toString().trim();
                String present = Present.getText().toString().trim();
                String permanent = Permanent.getText().toString().trim();
                String date = dateofBirth.getText().toString().trim();
                if(!firstName.isEmpty() && !lastName.isEmpty() && !mainC.isEmpty() && !altC.isEmpty() && !mstatus.isEmpty()
                        && !religion.isEmpty() && !nationality.isEmpty() && !present.isEmpty()
                        && !permanent.isEmpty() && !date.isEmpty()){
                    saveEditDetails();
                }else if(firstName.isEmpty()){
                    firstname.setError("First Name Name required!");
                }else if(lastName.isEmpty()){
                    lastname.setError("Last Name required!");
                }else if(mainC.isEmpty()){
                    mainContact.setError("Main Contact required!");
                }else if(altC.isEmpty()){
                    altContact.setError("Alternate Contact required!");
                }else if(mstatus.isEmpty()){
                    maritalStatus.setError("Marital Status required!");
                }else if(religion.isEmpty()){
                    Religion.setError("Religion required!");
                }else if(nationality.isEmpty()){
                    Nationality.setError("Nationality required!");
                }else if(present.isEmpty()){
                    Present.setError("Present Address required!");
                }else if(permanent.isEmpty()){
                    Permanent.setError("Permanent Address required!");
                }else if(date.isEmpty()){
                    dateofBirth.setError("Birth Date is required!");
                }else{
                    firstname.setError("First Name required!");
                    lastname.setError("Last Name required!");
                    mainContact.setError("Main Contact required!");
                    altContact.setError("Alternate Contact required!");
                    maritalStatus.setError("Marital Status required!");
                    Religion.setError("Religion required!");
                    Nationality.setError("Nationality required!");
                    Present.setError("Present Address required!");
                    Permanent.setError("Permanent Address required!");
                }
            }
        });
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFile();
            }
        });
    }
    private void getuserDetail(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_READ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("read");

                            if(success.equals("1")){
                                for(int i = 0; i < jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String firstName = object.getString("firstname").trim();
                                    String middleName = object.getString("middlename").trim();
                                    String lastName = object.getString("lastname").trim();
                                    String mcontact = object.getString("mcontact").trim();
                                    String acontact = object.getString("acontact").trim();
                                    String mstatus = object.getString("mstatus").trim();
                                    String birthdate = object.getString("birthdate").trim();
                                    String religion = object.getString("religion").trim();
                                    String nationality = object.getString("nationality").trim();
                                    String present = object.getString("present").trim();
                                    String permanent = object.getString("permanent").trim();
                                    String profimage = object.getString("profImage").trim();
                                    String imagePath = "http://rhaysabaria-001-site1.ftempurl.com/"+profimage;
                                    Picasso.get().load(imagePath).into(profImage);
                                    firstname.setText(firstName);
                                    middlename.setText(middleName);
                                    lastname.setText(lastName);
                                    mainContact.setText(mcontact);
                                    altContact.setText(acontact);
                                    maritalStatus.setText(mstatus);
                                    dateofBirth.setText(birthdate);
                                    Religion.setText(religion);
                                    Nationality.setText(nationality);
                                    Present.setText(present);
                                    Permanent.setText(permanent);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Error Reading detail!"+e,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Error Reading detail!"+error,
                                Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("empid",empid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onResume() {
        super.onResume();
        getuserDetail();
    }
    public void saveEditDetails(){
        progressDialog = new ProgressDialog(this);
        final String firstname = this.firstname.getText().toString().trim();
        final String middlename = this.middlename.getText().toString().trim();
        final String lastname = this.lastname.getText().toString().trim();
        final String maincontact = this.mainContact.getText().toString().trim();
        final String altcontact = this.altContact.getText().toString().trim();
        final String marital = this.maritalStatus.getText().toString().trim();
        final String dateofbirth = this.dateofBirth.getText().toString().trim();
        final String religion = this.Religion.getText().toString().trim();
        final String nationality = this.Nationality.getText().toString().trim();
        final String present = this.Present.getText().toString().trim();
        final String permanent = this.Permanent.getText().toString().trim();
        final String id = empid;
        final String user = username;
        progressDialog.setMessage("Saving...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST
                , URL_EDIT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(EditActivity.this,
                                        "Success!", Toast.LENGTH_SHORT).show();
                                sessionManager.createSession(id,user);
                                getuserDetail();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(EditActivity.this,
                                    "Error: "+jsonException.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(EditActivity.this, "Error: "+error.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("firstname",firstname);
                params.put("middlename",middlename);
                params.put("lastname",lastname);
                params.put("altcontact",altcontact);
                params.put("maincontact",maincontact);
                params.put("marital",marital);
                params.put("dateofbirth",dateofbirth);
                params.put("religion",religion);
                params.put("nationality",nationality);
                params.put("present",present);
                params.put("permanent",permanent);
                params.put("empid",empid);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void chooseFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            Uri filepath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                profImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadPicture(username,getStringImage(bitmap));
        }
    }
    private void uploadPicture(final String User, final String photo){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_UPLOAD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Log.i(TAG,response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            if(success.equals("1")){
                                Toast.makeText(EditActivity.this,
                                        "Success!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(
                                        EditActivity.this,EditActivity.class));
                                getuserDetail();
                                finish();
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            progressDialog.dismiss();
                            Toast.makeText(EditActivity.this,
                                    "Error try again!: "+jsonException.toString(),
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(EditActivity.this,
                                "Try again!: "+error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username",User);
                params.put("photo",photo);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public String getStringImage(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] imageByteArray = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageByteArray,Base64.DEFAULT);
        return encodedImage;
    }
}
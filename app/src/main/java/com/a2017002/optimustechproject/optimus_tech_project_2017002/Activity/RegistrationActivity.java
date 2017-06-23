package com.a2017002.optimustechproject.optimus_tech_project_2017002.Activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.a2017002.optimustechproject.optimus_tech_project_2017002.Interface.RegistrationRequest;
import com.a2017002.optimustechproject.optimus_tech_project_2017002.R;
import com.a2017002.optimustechproject.optimus_tech_project_2017002.models.LoginDataPOJO;
import com.a2017002.optimustechproject.optimus_tech_project_2017002.models.RegDataPOJO;
import com.a2017002.optimustechproject.optimus_tech_project_2017002.models.RegDataumPOJO;
import com.a2017002.optimustechproject.optimus_tech_project_2017002.networking.ServiceGenerator;
import com.a2017002.optimustechproject.optimus_tech_project_2017002.util.ColoredSnackbar;
import com.a2017002.optimustechproject.optimus_tech_project_2017002.util.DbHandler;
import com.a2017002.optimustechproject.optimus_tech_project_2017002.util.NetworkCheck;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegistrationActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText first_name,last_name,dob,username,passsword,mobile;
    ImageView img,male,female;
    String gender="J";
    AppCompatButton submit;
    Calendar myCalendar = Calendar.getInstance();
    ProgressDialog progressDialog;
    private ColoredSnackbar coloredSnackbar;
    Gson gson=new Gson();
    private View.OnClickListener snackbarListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            register();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        toolbar=(Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Join Us");

        first_name=(EditText)findViewById(R.id.first_name);
        last_name=(EditText)findViewById(R.id.last_name);
        dob=(EditText)findViewById(R.id.dob);
        username=(EditText)findViewById(R.id.username);
        passsword=(EditText)findViewById(R.id.password);
        mobile=(EditText)findViewById(R.id.mobile);

        submit=(AppCompatButton)findViewById(R.id.register);

        img=(ImageView)findViewById(R.id.img);
        male=(ImageView)findViewById(R.id.male);
        female=(ImageView)findViewById(R.id.female);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy";

                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                dob.setText(sdf.format(myCalendar.getTime()));

            }

        };


        dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegistrationActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male.setColorFilter(getResources().getColor(R.color.colorAccent));
                gender="M";
                female.setColorFilter(null);
                img.setImageDrawable(getResources().getDrawable(R.drawable.male_account));
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                female.setColorFilter(getResources().getColor(R.color.colorAccent));
                gender="F";
                male.setColorFilter(null);
                img.setImageDrawable(getResources().getDrawable(R.drawable.female_account));
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(first_name.getText().toString().equals("")){
                    first_name.setError("First name required");
                }
                if(last_name.getText().toString().equals("")){
                    last_name.setError("Last name required");
                }
                if(username.getText().toString().equals("")){
                    username.setError("Email required");
                }
                if(passsword.getText().toString().equals("")){
                    passsword.setError("Password required");
                }
                if(dob.getText().toString().equals("")){
                    dob.setError("DOB required");
                }
                if(mobile.getText().toString().equals("")){
                    mobile.setError("Mobile required");
                }
                if(gender.equals("J")){
                    Toast.makeText(RegistrationActivity.this,"Select your gender",Toast.LENGTH_LONG).show();
                }
                if(!first_name.getText().toString().equals("") && !last_name.getText().toString().equals("") && !username.getText().toString().equals("") && !passsword.getText().toString().equals("") && !dob.getText().toString().equals("") && !mobile.getText().toString().equals("") && !gender.equals("J")){
                    register();
                }
            }
        });
    }

    public void register(){
        if(NetworkCheck.isNetworkAvailable(RegistrationActivity.this)){
            progressDialog=new ProgressDialog(RegistrationActivity.this);
            progressDialog.setMessage("Loading....");
            progressDialog.setCancelable(false);
            progressDialog.show();

            final RegistrationRequest registrationRequest= ServiceGenerator.createService(RegistrationRequest.class);
            final Observable<RegDataPOJO> observable=registrationRequest.requestResponse(first_name.getText().toString(),last_name.getText().toString(),dob.getText().toString(),gender,mobile.getText().toString(),username.getText().toString(),passsword.getText().toString(), FirebaseInstanceId.getInstance().getToken());
            observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<RegDataPOJO>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable e) {
                    Log.e("errorreg",String.valueOf(e));

                    progressDialog.dismiss();
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Error connecting to server", Snackbar.LENGTH_SHORT);
                    coloredSnackbar.warning(snackbar).show();
                }

                @Override
                public void onNext(RegDataPOJO regDataPOJO) {
                    progressDialog.dismiss();
                    RegDataumPOJO data=regDataPOJO.getData();
                    Log.e("errorreg",String.valueOf(data));
                    if(!regDataPOJO.getError()){
                        Toast.makeText(RegistrationActivity.this,regDataPOJO.getMessage(),Toast.LENGTH_LONG).show();
                        DbHandler.setSession(RegistrationActivity.this,gson.toJson(data),data.getKey());
                    }
                    else{
                        new AlertDialog.Builder(RegistrationActivity.this)
                                .setMessage(regDataPOJO.getMessage())
                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // onBackPressed();
                                    }
                                });

                    }

                }
            });
        }
        else{
            Snackbar snackbar=Snackbar.make(findViewById(android.R.id.content),"No internet connection",Snackbar.LENGTH_LONG).setAction("Retry", snackbarListener);
            coloredSnackbar.alert(snackbar).show();
        }

    }

}
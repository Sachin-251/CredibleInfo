package com.example.credibleinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    Button signup;
    EditText email,password;
    public static String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signup=(Button)findViewById(R.id.btnSignup);
        email=(EditText)findViewById(R.id.edEmail);
        password=(EditText)findViewById(R.id.edPassword);
    }

    public void newUser(View v)
    {
        if(v==signup)
        {
            boolean validPass=true;
            boolean validEmail=true;
            final String validationEmail = email.getText().toString();
            final String validationPass = password.getText().toString();
            /*if (!isValidEmail(validationEmail)) {
                email.setError("invalid Email");
                validEmail=false;
            }
            else if (!isValidPassword(validationPass)) {
                password.setError("Password must be at least 6 characters");
                validPass=false;
            }
            else if(validEmail && validPass){*/
                    userSignup();
                    Intent personal = new Intent(MainActivity.this, PersonalDetails.class);
                    startActivity(personal);

           // }
            }
        }

    private boolean userSignup(){

        final String mail = email.getText().toString().trim();
        final String pass = password.getText().toString().trim();


        class UserSignup extends AsyncTask<Void,Void,String> {
        boolean result;
            ProgressDialog loading;
            String USERID;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MainActivity.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                USERID=getId(s);
                MainActivity.id=USERID;
               result=true;
               Toast.makeText(MainActivity.this,"Unique User Id is :"+USERID,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... v) {
                JSONObject params=new JSONObject();
                try {
                    params.put(Config.KEY_EMAIL,mail);
                    params.put(Config.KEY_PASSWORD,pass);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_ADD, params);
                return res;
            }
        }

        UserSignup us = new UserSignup();
        us.execute();
        return us.result;
    }

    private String getId(String json) {
        String id="";
        try {
            JSONObject jsonObject = new JSONObject(json);
            String data=jsonObject.getString("data");
            JSONObject result=new JSONObject(data);
            Object obj=result.get("id");
            id = obj.toString();
            //Toast.makeText(MainActivity.this,"Your Unique Id is"+id,Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }

}

package com.example.credibleinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static java.util.Arrays.asList;

public class EducationUpdate extends AppCompatActivity {

    EditText orgn,deg,loca;
    Spinner startYear,endYear;
    Button upd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_update);
        orgn=(EditText)findViewById(R.id.edIns);
        deg=(EditText)findViewById(R.id.edDeg);
        loca=(EditText)findViewById(R.id.edCit);
        startYear=(Spinner)findViewById(R.id.stYear1);
        endYear=(Spinner)findViewById(R.id.eYear1);
        upd=(Button)findViewById(R.id.btnUpDet);
        final ArrayList<String> year=new ArrayList<String>(asList("2000","2001 ","2002","2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019","2020","2021","2022","2023","2024"));
        ArrayAdapter<String> adp=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,year);
        adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        startYear.setAdapter(adp);
        endYear.setAdapter(adp);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        String txt1=bundle.getString("Organisation");
        String txt2=bundle.getString("Degree");
        String txt3=bundle.getString("Location");
        orgn.setText(txt1);
        deg.setText(txt2);
        loca.setText(txt3);
    }

    public void clickUpdate(View v)
    {
        if(v==upd)
        {
            updateDetails();
            Intent pub=new Intent(EducationUpdate.this,PublicView.class);
            startActivity(pub);
        }
    }




    private boolean updateDetails(){

        final String univer = orgn.getText().toString().trim();
        final String degree = deg.getText().toString().trim();
        final String location = loca.getText().toString().trim();
        final String sYear="01-01-"+startYear.getSelectedItem().toString();
        final String eYear="01-01-"+endYear.getSelectedItem().toString();

        class Educate extends AsyncTask<Void,Void,String> {
            boolean result;
            ProgressDialog loading;
            String USERID;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(EducationUpdate.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                USERID=getId(s);
                MainActivity.id=USERID;
                result=true;
                Toast.makeText(EducationUpdate.this,"Details updated",Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... v) {
                JSONObject params=new JSONObject();
                try {
                    params.put(Config.KEY_STYEAR,sYear);
                    params.put(Config.KEY_DEGREE,degree);
                    params.put(Config.KEY_ORGAN,univer);
                    params.put(Config.KEY_LOCATION,location);
                    params.put(Config.KEY_ENYEAR,eYear);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                 UpdateDetails ud = new UpdateDetails();
                String res = ud.sendPostRequest(Config.URL_EDUCATION, params);
                return res;
            }
        }

        Educate ed = new Educate();
        ed.execute();
        return ed.result;
    }

    private String getId(String json) {
        String id="";
        try {
            JSONObject jsonObject = new JSONObject(json);
            String data=jsonObject.getString("data");
            JSONObject result=new JSONObject(data);
            Object obj=result.get("uid");
            id = obj.toString();
            //Toast.makeText(MainActivity.this,"Your Unique Id is"+id,Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return id;
    }
}
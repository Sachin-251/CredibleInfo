package com.example.credibleinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.util.Arrays.asList;

public class ProfessionalDetails extends AppCompatActivity {

    Spinner sMonth,sYear,eMonth,eYear;
    String month[],end;
    Button save;
    CheckBox current;
    View divider;
    EditText organisation,designation;
    String stDate="01-";
    String endDate,enDate="01-";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_details);
        sMonth=(Spinner)findViewById(R.id.spMonth);
        sYear=(Spinner)findViewById(R.id.spYear);
        eMonth=(Spinner)findViewById(R.id.enMonth);
        eYear=(Spinner)findViewById(R.id.enYear);
        organisation=(EditText)findViewById(R.id.edOrganisation);
        designation=(EditText)findViewById(R.id.edDesignation);
        current=(CheckBox)findViewById(R.id.chkCurrent);
        save=(Button)findViewById(R.id.btnSave2);
        divider=findViewById(R.id.dvdEnd);
        current.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.isChecked())
                {
                    eMonth.setVisibility(View.INVISIBLE);
                    eYear.setVisibility(View.INVISIBLE);
                    divider.setVisibility(View.INVISIBLE);
                }
                else{
                    eMonth.setVisibility(View.VISIBLE);
                    eYear.setVisibility(View.VISIBLE);
                    divider.setVisibility(View.VISIBLE);
                }
            }
        });
        month=new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"};
        final ArrayList<String> year=new ArrayList<String>(asList("2000","2001 ","2002","2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019"));
        ArrayAdapter <String> adp=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,month);
        adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter <String> adp2=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,year);
        adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sMonth.setAdapter(adp);
        sYear.setAdapter(adp2);
        eMonth.setAdapter(adp);
        eYear.setAdapter(adp2);
    }

    public void eduActivity(View v)
    {
        if(v==save)
        {
            if(current.isChecked())
            {
                endDate="Currently working";
            }
            else
                endDate="01-"+eMonth.getSelectedItem().toString()+"-"+eYear.getSelectedItem().toString();
            proffesionalDetails();
            Intent education=new Intent(this,EducationDetails.class);
            startActivity(education);
        }
    }

    private boolean proffesionalDetails(){

        final String organ = organisation.getText().toString().trim();
        final String design = designation.getText().toString().trim();
        final String stDate = "01-"+sMonth.getSelectedItem().toString()+"-"+sYear.getSelectedItem().toString();
        final String enDate=endDate;

        class Prof extends AsyncTask<Void,Void,String> {
            boolean result;
            ProgressDialog loading;
            String USERID;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ProfessionalDetails.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                USERID=getId(s);
                MainActivity.id=USERID;
                result=true;
                Toast.makeText(ProfessionalDetails.this,"UID is :"+USERID,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... v) {
                JSONObject params=new JSONObject();
                try {
                    params.put(Config.KEY_END,enDate);
                    params.put(Config.KEY_ORGAN,organ);
                    params.put(Config.KEY_DESIGN,design);
                    params.put(Config.KEY_START,stDate);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_PROF, params);
                return res;
            }
        }

        Prof p = new Prof();
        p.execute();
        return p.result;
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

package com.example.credibleinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import static java.util.Arrays.asList;

public class EducationDetails extends AppCompatActivity {

    Spinner startYear,endYear;
    ImageView imgCert;
    EditText univ,deg,loc;
    String sYear,eYear;
    int request=1234;
    Button save;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_details);
        startYear=(Spinner)findViewById(R.id.stYear);
        endYear=(Spinner)findViewById(R.id.eYear);
        imgCert=(ImageView)findViewById(R.id.imgCertificate);
        save=(Button)findViewById(R.id.btnSave3);
        univ=(EditText)findViewById(R.id.edInstitute);
        deg=(EditText)findViewById(R.id.edDegree);
        loc=(EditText)findViewById(R.id.edCity);
        final ArrayList<String> year=new ArrayList<String>(asList("2000","2001 ","2002","2003","2004","2005","2006","2007","2008","2009","2010","2011","2012","2013","2014","2015","2016","2017","2018","2019","2020","2021","2022","2023","2024"));
        ArrayAdapter<String> adp=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,year);
        adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        startYear.setAdapter(adp);
        endYear.setAdapter(adp);
    }

    public void getImage(View v)
    {
        if(v==imgCert)
        {
            Intent implicit=new Intent(Intent.ACTION_PICK);
            File pic= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String path=pic.getPath();
            Uri data= Uri.parse(path);
            implicit.setDataAndType(data, "image/*");
            startActivityForResult(implicit, request);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode==RESULT_OK)
        {
            if (requestCode == request)
            {
                Uri imgUri = data.getData();
                InputStream stream;
                try {
                    stream = getContentResolver().openInputStream(imgUri);
                    Bitmap bit = BitmapFactory.decodeStream(stream);
                    imgCert.setImageBitmap(bit);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to Open image", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    public void publicView(View v)
    {
        if(v==save)
        {
            educationDetails();
            Intent pub = new Intent(this,PublicView.class);
            startActivity(pub);
        }

    }

    private boolean educationDetails(){

        final String university = univ.getText().toString().trim();
        final String degree = deg.getText().toString().trim();
        final String location = loc.getText().toString().trim();
        final String sYear="01-01-"+startYear.getSelectedItem().toString();
        final String eYear="01-01-"+endYear.getSelectedItem().toString();

        class Educate extends AsyncTask<Void,Void,String> {
            boolean result;
            ProgressDialog loading;
            String USERID;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(EducationDetails.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                USERID=getId(s);
                MainActivity.id=USERID;
                result=true;
                Toast.makeText(EducationDetails.this,"Unique User Id is :"+USERID,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... v) {
                JSONObject params=new JSONObject();
                try {
                    params.put(Config.KEY_STYEAR,sYear);
                    params.put(Config.KEY_DEGREE,degree);
                    params.put(Config.KEY_ORGAN,university);
                    params.put(Config.KEY_LOCATION,location);
                    params.put(Config.KEY_ENYEAR,eYear);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_EDUCATION, params);
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

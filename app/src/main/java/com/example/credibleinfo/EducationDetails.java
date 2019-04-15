package com.example.credibleinfo;

import android.Manifest;
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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class EducationDetails extends AppCompatActivity {

    Spinner startYear,endYear;
    ImageView imgCert;
    EditText univ,deg,loc;
    String sYear,eYear,ConvertImage;
    int request=1234;
    public static Button save;
    ByteArrayOutputStream byteArrayOutputStream;
    Bitmap bit;
    private RequestQueue rQueue;
    private ArrayList<HashMap<String, String>> arraylist;
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
                    bit = BitmapFactory.decodeStream(stream);
                    imgCert.setImageBitmap(bit);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to Open image", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private void uploadImage(final Bitmap bitmap){

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Config.URL_CERT,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.d("ressssssoo",new String(response.data));
                        rQueue.getCache().clear();
                        try {
                            JSONObject jsonObject = new JSONObject(new String(response.data));
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                            jsonObject.toString().replace("\\\\","");

                            if (jsonObject.getString("status").equals("true")) {

                                arraylist = new ArrayList<HashMap<String, String>>();
                                JSONArray dataArray = jsonObject.getJSONArray("data");

                                String url = "";
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject dataobj = dataArray.getJSONObject(i);
                                    url = dataobj.optString("pathToFile");
                                }
                                Picasso.get().load(url).into(imgCert);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // params.put("tags", "ccccc");  add string parameters
                return params;
            }

            /*
             *pass files using below method
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("filename", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };


        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(EducationDetails.this);
        rQueue.add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(

                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    public void publicView(View v)
    {
        if(v==save)
        {
            educationDetails();
            uploadImage(bit);
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

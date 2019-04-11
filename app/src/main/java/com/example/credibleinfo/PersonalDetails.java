package com.example.credibleinfo;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PersonalDetails extends AppCompatActivity {

    ImageView img;
    int request=1234;
    Button save;
    public static String UID;
    EditText name,email,mobile,location,links,skills;
    Bitmap FixBitmap;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage;
    Bitmap bit=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);
        img=(ImageView)findViewById(R.id.imgPersonal);
        save=(Button)findViewById(R.id.btnSave);
        name=(EditText)findViewById(R.id.edName);
        email=(EditText)findViewById(R.id.edEmail);
        mobile=(EditText)findViewById(R.id.edMobile);
        location=(EditText)findViewById(R.id.edLocation);
        links=(EditText)findViewById(R.id.edLinks);
        skills=(EditText)findViewById(R.id.edSkills);
    }

    public void getImage(View v)
    {
        if(v==img)
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
                    BitmapFactory.Options opt=new BitmapFactory.Options();
                    opt.inJustDecodeBounds=true;

                    while(bit==null)
                        bit =BitmapFactory.decodeStream(stream);
                    Bitmap.createScaledBitmap(bit, 100,100,true);
                    img.setImageBitmap(bit);
                    FixBitmap=((BitmapDrawable)img.getDrawable()).getBitmap();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to Open image", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean uploadImage(){

        FixBitmap.compress(Bitmap.CompressFormat.JPEG, 0, byteArrayOutputStream);
        byte[] br=byteArrayOutputStream.toByteArray();
        ConvertImage= Base64.encodeToString(br, Base64.DEFAULT);


        class ProfilePic extends AsyncTask<Void,Void,String> {
            boolean result;
            ProgressDialog loading;
            String USERID;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(PersonalDetails.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //USERID=getId(s);
                //MainActivity.id=USERID;
                result=true;
                Toast.makeText(PersonalDetails.this,s,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... v) {
                JSONObject params=new JSONObject();
                try {
                    params.put(Config.KEY_IMAGE,ConvertImage);
                    params.put(Config.KEY_UID,PersonalDetails.UID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_ADD, params);
                return res;
            }
        }

        ProfilePic pp = new ProfilePic();
        pp.execute();
        return pp.result;
    }

    public void saveInfo(View v)
    {
        if(v==save)
        {
            //savePersonal();
            //uploadImage();
            Intent professional=new Intent(this, ProfessionalDetails.class);
            startActivity(professional);
        }
    }

    private boolean savePersonal(){

        final String name = this.name.getText().toString().trim();
        final String email = this.email.getText().toString().trim();
        final String mobile = this.mobile.getText().toString().trim();
        final String location = this.location.getText().toString().trim();
        final String links = this.links.getText().toString().trim();
        final String skills = this.skills.getText().toString().trim();

        class PersonalDet extends AsyncTask<Void,Void,String> {
            boolean result;
            ProgressDialog loading;
            String USERID;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(PersonalDetails.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                USERID=getId(s);
                PersonalDetails.UID=USERID;
                result=true;
                Toast.makeText(PersonalDetails.this,"Information saved User Id is :"+USERID,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... v) {
                JSONObject params=new JSONObject();
                try {
                    params.put(Config.KEY_SKILLS,skills);
                    params.put(Config.KEY_MOBILE,mobile);
                    params.put(Config.KEY_NAME,name);
                    params.put(Config.KEY_LINKS,links);
                    params.put(Config.KEY_LOCATION,location);
                    params.put(Config.KEY_EMAIL,email);

                } catch (JSONException e) {
                    e.printStackTrace();
                }



                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Config.URL_PERSONAL, params);
                return res;
            }
        }

        PersonalDet pd = new PersonalDet();
        pd.execute();
        return pd.result;
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

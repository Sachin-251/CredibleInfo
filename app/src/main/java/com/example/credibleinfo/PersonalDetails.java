package com.example.credibleinfo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import java.net.URI;

public class PersonalDetails extends AppCompatActivity {

    ImageView img;
    int request=1234;
    public static Button save;
    public static String UID;
    EditText name,email,mobile,location,links,skills;
    Bitmap FixBitmap;
    ByteArrayOutputStream byteArrayOutputStream;
    byte[] byteArray;
    String ConvertImage,path;
    Bitmap bit=null;
    Uri fileUri;
    String picturePath;
    Uri selectedImage;
    Bitmap photo;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1)
        {
            if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getPhoto();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            }
        }
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

    public void getPhoto()
    {
        Intent implicit=new Intent(Intent.ACTION_PICK);
        File pic= Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String path=pic.getPath();
        Uri data= Uri.parse(path);
        implicit.setDataAndType(data, "image/*");
        startActivityForResult(implicit, request);
    }


    public void getImage(View v)
    {
        if(v==img)
        {
            getPhoto();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK)
        {
            if (requestCode == request)
            {
                Uri imgUri = data.getData();
                InputStream stream;
                try {
                    stream = getContentResolver().openInputStream(imgUri);
                    bit = BitmapFactory.decodeStream(stream);
                    img.setImageBitmap(bit);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to Open image", Toast.LENGTH_LONG).show();
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
            savePersonal();
         uploadImage();
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

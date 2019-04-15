package com.example.credibleinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class PersonalUpdate extends AppCompatActivity {

    ImageView img;
    int request=1234;
    public static Button save;
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
        setContentView(R.layout.activity_personal_update);
        img=(ImageView)findViewById(R.id.imgPer);
        save=(Button)findViewById(R.id.btnSave5);
        name=(EditText)findViewById(R.id.edNam);
        email=(EditText)findViewById(R.id.edmail);
        mobile=(EditText)findViewById(R.id.edMob);
        location=(EditText)findViewById(R.id.edLoca);
        links=(EditText)findViewById(R.id.edLink);
        skills=(EditText)findViewById(R.id.edSkill);
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        String txt1=bundle.getString("Name");
        String txt2=bundle.getString("Email");
        String txt3=bundle.getString("Mobile");
        String txt4=bundle.getString("Location");
        String txt5=bundle.getString("Links");
        String txt6=bundle.getString("Skills");
        name.setText(txt1);
        email.setText(txt2);
        mobile.setText(txt3);
        location.setText(txt4);
        links.setText(txt5);
        skills.setText(txt6);
    }


    public void clickUp(View v)
    {
        if(v==save)
        {
            updatePersonal();
            Intent in=new Intent(PersonalUpdate.this,PublicView.class);
            startActivity(in);
        }

    }

    public void getImage(View v)
    {
        if(v==img)
        {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.putExtra("crop", "true");
            intent.putExtra("scale", true);
            intent.putExtra("outputX", 256);
            intent.putExtra("outputY", 256);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("return-data", true);
            startActivityForResult(intent, 1);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 1) {
            final Bundle extras = data.getExtras();
            if (extras != null) {
                //Get image
                Bitmap FixBitmap = extras.getParcelable("data");
                img.setImageBitmap(FixBitmap);
            }
        }
    }

    public boolean updatePersonal(){

        final String name = this.name.getText().toString().trim();
        final String email = this.email.getText().toString().trim();
        final String mobile = this.mobile.getText().toString().trim();
        final String location = this.location.getText().toString().trim();
        final String links = this.links.getText().toString().trim();
        final String skills = this.skills.getText().toString().trim();

        class Educate extends AsyncTask<Void,Void,String> {
            boolean result;
            ProgressDialog loading;
            String USERID;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(PersonalUpdate.this,"Adding...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                USERID=getId(s);
                MainActivity.id=USERID;
                result=true;
                Toast.makeText(PersonalUpdate.this,"Details updated",Toast.LENGTH_LONG).show();

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



                UpdateDetails ud = new UpdateDetails();
                String res = ud.sendPostRequest(Config.URL_PERSONAL, params);
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

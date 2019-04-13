package com.example.credibleinfo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicView extends AppCompatActivity {

    CircleImageView img;
    int request = 1234;
    View divider;
    TextView personal, education, professional, opt, fName, fLoc, fOrgan;
    public static TextView test;
    ConstraintLayout optLayout, main;
    ImageView back;
    String loc, mob, links, skills, name, organ, design, eDate, sDate, univ, degree, eduLoc, sYear, eYear;
    Button update;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_view);
        img = (CircleImageView) findViewById(R.id.imgTest);
        divider = findViewById(R.id.div);
        personal = (TextView) findViewById(R.id.txtPersonal);
        professional = (TextView) findViewById(R.id.txtProfessional);
        education = (TextView) findViewById(R.id.txtEducation);
        opt = (TextView) findViewById(R.id.txtOptions);
        test = (TextView) findViewById(R.id.test);
        optLayout = (ConstraintLayout) findViewById(R.id.options);
        main = (ConstraintLayout) findViewById(R.id.main);
        optLayout.animate().translationX(800f).setDuration(1);
        back = (ImageView) findViewById(R.id.btnBack);
        fName = (TextView) findViewById(R.id.publicName);
        fLoc = (TextView) findViewById(R.id.publicLoc);
        fOrgan = (TextView) findViewById(R.id.publicOrgan);
        update=(Button)findViewById(R.id.btnUpdate);
        getPersonal();
        getEducation();
        getProfessional();
        //setPro();
//        fName.setText("Blah Blah");
        //fSet();
    }

    public void fSet() {
        fName.setText(this.name);
    }

    public void setClickable(View view) {
        if (view != null) {
            view.setClickable(false);
            if (view instanceof ViewGroup) {
                ViewGroup vg = ((ViewGroup) view);
                for (int i = 0; i < vg.getChildCount(); i++) {
                    setClickable(vg.getChildAt(i));
                }
            }
        }
    }

    public void setClickableTrue(View view) {
        if (view != null) {
            view.setClickable(true);
            if (view instanceof ViewGroup) {
                ViewGroup vg = ((ViewGroup) view);
                for (int i = 0; i < vg.getChildCount(); i++) {
                    setClickableTrue(vg.getChildAt(i));
                }
            }
        }
    }

    public void back(View v) {
        if (v == back) {
            optLayout.animate().translationX(800f).setDuration(500);
            setClickableTrue(main);
        }
    }

    public void show(View v) {
        if (v == opt) {
            optLayout.animate().translationX(30f).setDuration(500);
            setClickable(main);
            back.setClickable(true);
        }
    }

    public void trans(View v) {
        if (v == education) {
            divider.animate().translationX(-400f).setDuration(500);
            setEdu();
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in=new Intent(PublicView.this,EducationUpdate.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("Organisation", univ);
                    bundle.putString("Degree", degree);
                    bundle.putString("Location", eduLoc);
                    in.putExtras(bundle);
                    startActivity(in);
                }
            });
        } else if (v == professional) {
            divider.animate().translationX(-5f).setDuration(500);
            setPro();
        } else if (v == personal) {
            divider.animate().translationX(400f).setDuration(500);
            getPer();
            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in=new Intent(PublicView.this,PersonalUpdate.class);
                    startActivity(in);
                }
            });
        }
    }

    public void getImage(View v) {
        if (v == img) {
            Intent implicit = new Intent(Intent.ACTION_PICK);
            File pic = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String path = pic.getPath();
            Uri data = Uri.parse(path);
            implicit.setDataAndType(data, "image/*");
            startActivityForResult(implicit, request);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == request) {
                Uri imgUri = data.getData();
                InputStream stream;
                try {
                    stream = getContentResolver().openInputStream(imgUri);
                    Bitmap bit = BitmapFactory.decodeStream(stream);
                    img.setImageBitmap(bit);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unable to Open image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean getPersonal() {


        class GetPersonal extends AsyncTask<Void, Void, String> {
            boolean result;
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(PublicView.this, "Adding...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                first(s);
                //MainActivity.id=USERID;
                result = true;
                //Toast.makeText(MainActivity.this,"Unique User Id is :"+USERID,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... v) {

                GetRequestHandler grh = new GetRequestHandler();
                String res = grh.sendGetRequest(Config.URL_PERSONAL);
                return res;
            }
        }

        GetPersonal gp = new GetPersonal();
        gp.execute();
        return gp.result;
    }

    public void first(String json) {


        try {
            String data;
            JSONObject jsonObject = new JSONObject(json);
            data = jsonObject.getString("data");
            JSONObject result = new JSONObject(data);
            name = result.getString("name");
            loc = result.getString("location");
            mob = result.getString("mobile_no");
            links = result.getString("links");
            skills = result.getString("skills");
            fName.setText(name);
            fLoc.setText(loc);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getPer() {

        test.setText("\nName    :" + name);
        test.append("\nLocation :" + loc);
        test.append("\nMobile   :" + mob);
        test.append("\nLinks    :" + links);
        test.append("\nSkills   :" + skills);
    }


    private boolean getProfessional() {


        class GetProfessional extends AsyncTask<Void, Void, String> {
            boolean result;
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(PublicView.this, "Adding...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                getPro(s);
                //MainActivity.id=USERID;
                result = true;
                //Toast.makeText(MainActivity.this,"Unique User Id is :"+USERID,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... v) {

                GetRequestHandler grh = new GetRequestHandler();
                String res = grh.sendGetRequest(Config.URL_PROF);
                return res;
            }
        }

        GetProfessional gprof = new GetProfessional();
        gprof.execute();
        return gprof.result;
    }

    public void getPro(String json) {
        try {
            String data;
            JSONObject jsonObject = new JSONObject(json);
            data = jsonObject.getString("data");
            JSONObject result = new JSONObject(data);
            organ = result.getString("organisation");
            design = result.getString("designation");
            eDate = result.getString("end_date");
            sDate = result.getString("start_date");
            fOrgan.setText(organ);
            setPro();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setPro() {

        test.setText("Organisation    :" + organ);
        test.append("\nDesignation :" + design);
        test.append("\nStart Date   :" + sDate);
        test.append("\nEnd Date    :" + eDate);
    }

    private boolean getEducation()

    {


        class GetEducation extends AsyncTask<Void, Void, String> {
            boolean result;
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(PublicView.this, "Adding...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                getEdu(s);
                //MainActivity.id=USERID;
                result = true;
                //Toast.makeText(MainActivity.this,"Unique User Id is :"+USERID,Toast.LENGTH_LONG).show();

            }

            @Override
            protected String doInBackground(Void... v) {

                GetRequestHandler grh = new GetRequestHandler();
                String res = grh.sendGetRequest(Config.URL_EDUCATION);
                return res;
            }
        }

        GetEducation gEdu = new GetEducation();
        gEdu.execute();
        return gEdu.result;
    }

    public void getEdu(String json) {
        try {
            String data;
            JSONObject jsonObject = new JSONObject(json);
            data = jsonObject.getString("data");
            JSONObject result = new JSONObject(data);
            univ = result.getString("organisation");
            degree = result.getString("degree");
            eduLoc = result.getString("location");
            sYear = result.getString("start_year");
            eYear = result.getString("end_year");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setEdu() {

        test.setText("Organisation    :" + univ);
        test.append("\nDegree :" + degree);
        test.append("\nLocation :" + eduLoc);
        test.append("\nStart Year   :" + sYear);
        test.append("\nEnd Year    :" + eYear);
    }
}



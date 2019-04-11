package com.example.credibleinfo;

import android.app.VoiceInteractor;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicView extends AppCompatActivity {

    CircleImageView img;
    int request = 1234;
    View divider;
    TextView personal,education,professional,opt;
    public static TextView test;
    ConstraintLayout optLayout,main;
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_view);
        img = (CircleImageView) findViewById(R.id.imgTest);
        divider=findViewById(R.id.div);
        personal=(TextView)findViewById(R.id.txtPersonal);
        professional=(TextView)findViewById(R.id.txtProfessional);
        education=(TextView)findViewById(R.id.txtEducation);
        opt=(TextView)findViewById(R.id.txtOptions);
        test=(TextView)findViewById(R.id.test);
        optLayout=(ConstraintLayout) findViewById(R.id.options);
        main=(ConstraintLayout) findViewById(R.id.main);
        optLayout.animate().translationX(800f).setDuration(1);
        back=(ImageView) findViewById(R.id.btnBack);
       // String URL="http://139.59.65.145:9090/test";

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
    public void back(View v)
    {
        if(v==back)
        {
            optLayout.animate().translationX(800f).setDuration(500);
            setClickableTrue(main);
        }
    }

    public void show(View v)
    {
        if(v==opt)
        {
            optLayout.animate().translationX(30f).setDuration(500);
            setClickable(main);
            back.setClickable(true);
        }
    }

    public void trans(View v) {
        if (v == education) {
            divider.animate().translationX(-400f).setDuration(500);
            EduFetch process=new EduFetch();
            process.execute();
        }
        else if(v==professional) {
            divider.animate().translationX(-5f).setDuration(500);
        }
        else if(v==personal)
        {
            divider.animate().translationX(400f).setDuration(500);
        }
    }

   /* public void professional(View v) {
        if (v == professional) {
            divider.animate().translationX(-5f).setDuration(500);
        }
    }

    public void personal(View v)
    {
        if(v==personal)
        {
            divider.animate().translationX(400f).setDuration(500);
        }
    }
*/
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
}

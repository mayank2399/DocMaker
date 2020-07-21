package com.example.docmaker;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    ImageView Image;
    Boolean f,f2;
    EditText FileName;
    Button ADDPDF;
    Uri Imageurl;
    int index=1;
    private String directory = Environment.getExternalStorageDirectory()+File.separator+"DocMaker";
    Bitmap bitmap;
    PdfDocument pdfDocument;
    private  static final int PickIndex=1;
    private  static final int CamIndex=2;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public MainActivity() {
        pdfDocument = new PdfDocument();
        f=false;
        f2=false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, PackageManager.PERMISSION_GRANTED);
        Image=findViewById(R.id.img_pdf);
        FileName=findViewById(R.id.Name);

        ADDPDF=findViewById(R.id.ADD_PDF);

        ADDPDF.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                ADD();
            }
        });

    }

    //@RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void Converted() {
//        Toast.makeText(MainActivity.this,bitmap.toString(),Toast.LENGTH_LONG).show();
        String Filename=FileName.getText().toString();
        if(f==false)
        {   System.out.println("Yo yO");
            Toast.makeText(MainActivity.this,"Please add some pages",Toast.LENGTH_SHORT).show();
        }
        else if(Filename.isEmpty())
        {   Toast.makeText(MainActivity.this,"Please Enter File Name",Toast.LENGTH_LONG).toString();
        }
        else {
            File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "DocMaker");
            if (!folder.exists()) {
                folder.mkdirs();
            }
            String pdfFile = directory + "/"+Filename+".pdf";//"/myPDFFile_3.pdf";
            File myPDFFile = new File(pdfFile);

            try {
                pdfDocument.writeTo(new FileOutputStream(myPDFFile));
                Toast.makeText(MainActivity.this,"File Created Successfully",Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this,"FAIL",Toast.LENGTH_SHORT).show();
            }

            pdfDocument.close();
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void ADD() {
        f=true;
        if(f2==false)
        {   Toast.makeText(MainActivity.this,"Please capture or add image from galary",Toast.LENGTH_SHORT).show();
        }
        else {
            BitmapDrawable img = (BitmapDrawable) Image.getDrawable();
            bitmap = img.getBitmap();
            PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(595, 842, index).create();
            PdfDocument.Page page = pdfDocument.startPage(myPageInfo);
            index++;
            page.getCanvas().drawBitmap(bitmap, 0, 0, null);
            pdfDocument.finishPage(page);
            Image.setImageResource(0);
            Toast.makeText(MainActivity.this, "New Page is added", Toast.LENGTH_SHORT).show();
            f2=false;
        }
    }

    private void OpenGallery()
    {Intent gallery =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery,PickIndex);
        f2=true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK&& requestCode==PickIndex)
        {
            Imageurl=data.getData();
            Image.setImageURI(Imageurl);
            BitmapDrawable photo=(BitmapDrawable)Image.getDrawable();
            Bitmap p=photo.getBitmap();
            p=Bitmap.createScaledBitmap(p,595,842,true);
            Image.setImageBitmap(p);
        }
        if(resultCode==RESULT_OK&& requestCode==CamIndex)
        {
            Bitmap photo=(Bitmap) data.getExtras().get("data");
            photo=Bitmap.createScaledBitmap(photo,595,842,true);
            Image.setImageBitmap(photo);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.choose)
        {   OpenGallery();
        }
        if(item.getItemId()==R.id.capture)
        {Intent camera=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera,CamIndex);
            f2=true;
        }
        if(item.getItemId()==R.id.Convert)
            Converted();
        return  true;
    }
}

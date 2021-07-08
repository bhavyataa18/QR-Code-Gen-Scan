package com.example.finalqr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class MainActivity extends AppCompatActivity {
    EditText qrvalue;
    Button generateBtn,scanBtn,saveqr;
    ImageView qrImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrvalue = findViewById(R.id.qrInput);
        generateBtn = findViewById(R.id.generateBtn);
        scanBtn = findViewById(R.id.scanBtn);
        qrImage = findViewById(R.id.qrPlaceHolder);
        saveqr = findViewById(R.id.bt_downlaod);

        // ask for runtime permission
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        //save  QR to the gallary..

        saveqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable)qrImage.getDrawable(); // here we create a bitmap drawable and get the qr code which will genrate on the  image view.
                Bitmap bitmap = bitmapDrawable.getBitmap(); // create a object of Bitmap and get the image from bitmap drawable ..
                saveImageToGallery(bitmap); // call the saveImageToGallery function the click event of save button..

            }
        });

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = qrvalue.getText().toString();
                if(data.isEmpty()){
                    qrvalue.setError("Value Required.");
                }else {
                    QRGEncoder qrgEncoder = new QRGEncoder(data,null, QRGContents.Type.TEXT,500);
                    try {
                        Bitmap qrBits = qrgEncoder.encodeAsBitmap();
                        qrImage.setImageBitmap(qrBits);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ScannerActivity.class));
            }
        });



    }
    private void saveImageToGallery(Bitmap bitmap){

        FileOutputStream fos;

        try{
            //For devices running  android > = Q
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                ContentResolver resolver = getContentResolver(); //getting the content resolver
                ContentValues contentValues = new ContentValues(); // content resolver will produce the contant value

                // putting file information in the content values
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME,"IMAGE_"+".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE,"IMAGE/jpg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+ File.separator+"TestFolder");

                // inserting the contentvalue to content resolver and getting the uri
                Uri imageUri=resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);

                //opening an output stream with the uri that we got..
                fos = (FileOutputStream) resolver.openOutputStream(Objects.requireNonNull(imageUri));

                // writing the bitmap to the output stream that we opened
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                Objects.requireNonNull(fos);
                Toast.makeText(this,"Image saved !!",Toast.LENGTH_SHORT).show(); // display the message on the successfull image saved
            }

        }catch (Exception e){
            Toast.makeText(this,"Image not saved \n !!",Toast.LENGTH_SHORT).show(); // display the message when image is not downloaded successfully..

        }
    }


// validate text box

    private boolean validate()
    {

        if(qrvalue.length()==0)
        {
            qrvalue.setError("This field is required !! ");
            return false;
        }

        return true;
    }

}


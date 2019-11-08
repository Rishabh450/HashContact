package com.example.litereria.Support;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.litereria.MainActivity;
import com.example.litereria.R;
import com.facebook.Profile;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class QRcode extends AppCompatActivity {
    SurfaceView cameraPreview;boolean isFlash = true;
    CameraSource cameraSource;Button chooser;
    ImageView flashbutton;
  //  Button upload;

  public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";

    private String cameraId = CAMERA_BACK;
    private boolean isFlashSupported=true;
    private boolean isTorchOn;
    BarcodeDetector barcodeDetector;FirebaseDatabase database;
    DatabaseReference databaseReference;
    TextView decoded;String query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        setContentView(R.layout.activity_qrcode);
        flashbutton=findViewById(R.id.flashbutton);
        cameraPreview=findViewById(R.id.cameraPreview);
        //upload=findViewById(R.id.upload);

       /* upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });*/
       chooser=findViewById(R.id.chooser);
       chooser.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               selectImage();
           }
       });
        decoded=findViewById(R.id.textView3);
        setupFlashButton();
        flashbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFlash();
            }
        });
        barcodeDetector=new BarcodeDetector.Builder(QRcode.this)
                .setBarcodeFormats(Barcode.QR_CODE).build();
        cameraSource=new CameraSource.Builder(QRcode.this,barcodeDetector)
                .setRequestedPreviewSize(640,480).build();
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if(ContextCompat.checkSelfPermission(QRcode.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                {
                    // Log.e(TAG, "setxml: peremission prob");
                    ActivityCompat.requestPermissions(QRcode.this,new String[]{Manifest.permission.CAMERA},0);
                    if(ContextCompat.checkSelfPermission(QRcode.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
                        recreate();
                    else
                        startActivity(new Intent(QRcode.this,MainActivity.class));


                }else {
                    try {
                        Log.d("ak47","test");
                        Vibrator vibrator=(Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(300);
                        cameraSource.start(holder);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcode=detections.getDetectedItems();
                if(qrcode.size()>0)
                {



                    decoded.post(new Runnable() {
                        @Override
                        public void run() {

                           // Toast.makeText(QRcode.this,"Scan successful", Toast.LENGTH_SHORT).show();
                           // decoded.setText(qrcode.valueAt(0).displayValue);
                            query=qrcode.valueAt(0).displayValue;
                            barcodeDetector.release();
                            upload();






                        }
                    });
                }

            }
        });
    }
    public void switchFlash() {
        if (cameraId.equals(CAMERA_BACK)) {
            if (isFlashSupported) {
                if (isTorchOn) {

                    isTorchOn = false;
                } else {

                    isTorchOn = true;
                }
            }
        }
    }

    public void setupFlashButton() {
        if (cameraId.equals(CAMERA_BACK) && isFlashSupported) {
            flashbutton.setVisibility(View.VISIBLE);

            if (isTorchOn) {
                flashbutton.setImageResource(R.drawable.flashoff);
            } else {
                flashbutton.setImageResource(R.drawable.flashon);
            }

        } else {
            flashbutton.setVisibility(View.GONE);
        }
    }
    public void getQRfromImage(Bitmap im)
    {
        Log.d("imagewa","GHUSA");
        Bitmap generatedQRCode=im;
        int width = generatedQRCode.getWidth();
        int height = generatedQRCode.getHeight();
        int[] pixels = new int[width * height];
        generatedQRCode.getPixels(pixels, 250, width, 250, 250, width, height);
        Log.d("image","QR generated");

        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);

        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();
        Result result = null;
        try {
            result = reader.decode(binaryBitmap);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        String text = result.getText();
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
    }
    private void selectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);

    }
    /*public void changeFlashStatus() {
        Camera.Parameters params;
        Camera camera;
        CameraSource cameraSource;
        SurfaceView cameraView;
        boolean isFlash = false;
        Field[] declaredFields = CameraSource.class.getDeclaredFields();

        for (Field field : declaredFields) {
            if (field.getType() == Camera.class) {
                field.setAccessible(true);
                try {
                    camera = (Camera) field.get(cameraSource);
                    if (camera != null) {
                        params = camera.getParameters();
                        if (!isFlash) {
                            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            flashImage.setColorFilter(getResources().getColor(R.color.yellow));
                            isFlash = true;
                        } else {
                            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            flashImage.setColorFilter(getResources().getColor(R.color.greyLight));
                            isFlash = false;
                        }
                        camera.setParameters(params);
                    }

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                break;
            }
        }
    }*/


    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode== RESULT_OK && data!=null &&data.getData()!=null){

            {
                final Bundle extras = data.getExtras();
                if (extras != null) {
                    //Get image
                    Bitmap newProfilePic = extras.getParcelable("data");
                    Log.d("imagewaa","GHUSA");
                    getQRfromImage(newProfilePic);
                }
                /*if(data!=null){
                    Bitmap b=(Bitmap) data.getExtras().get("imagebitmap");;
                  // getQRfromImage(b);

                }*/


            }

        }


    }
    public void upload()
    {

        String[] lines = query.split(System.getProperty("line.separator"));
        Log.d("bharwa",lines[0]);
        try {
            final Map<String,Object> bookdata=new HashMap<>();
            bookdata.put("Email",lines[2].substring(1));

            bookdata.put("Instagram",lines[0].substring(10));
            bookdata.put("github",lines[1].substring(7));
            bookdata.put("Twitter",lines[10].substring(7));
            bookdata.put("PhoneNumber",lines[8].substring(5));
            bookdata.put("Facebook",lines[4].substring(8));
            bookdata.put("photo",lines[9].substring(8));
            bookdata.put("HackerEarth",lines[5].substring(11));
            bookdata.put("HackerRank",lines[6].substring(10));
            bookdata.put("Codechef",lines[3].substring(8));
            bookdata.put("Linkedin",lines[7].substring(8));
            String key=String.valueOf(System.currentTimeMillis());
            bookdata.put("key",key);
            bookdata.put("Name",lines[11].substring(4));
            database = FirebaseDatabase.getInstance();

            databaseReference=database.getReference();
            String  emailids= FirebaseAuth.getInstance().getCurrentUser().getEmail();
            String id= Profile.getCurrentProfile().getId();

            databaseReference.child(Profile.getCurrentProfile().getId()).child("Contact").child(key).setValue(bookdata);

            Toast.makeText(this,"Contact uploaded",Toast.LENGTH_LONG).show();
            Handler handler = new Handler();
            final Intent i = new Intent(this, MainActivity.class);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(i);

                    finish();
                }
            }, 2000);
           // startActivity(new Intent(this, MainActivity.class));
        }
        catch (Exception e) {
            // Google Sign In failed, update UI appropriately
            Log.w("thiswa", "Google sign in failed", e);
            Toast.makeText(this,"Invalid QR",Toast.LENGTH_LONG).show();
            Handler handler = new Handler();
            final Intent i = new Intent(this, MainActivity.class);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(i);

                    finish();
                }
            }, 2000);
            //startActivity(new Intent(this, MainActivity.class));
            // ...
        }






    }
}

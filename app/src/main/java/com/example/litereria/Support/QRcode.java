package com.example.litereria.Support;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.Surface;
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
import com.journeyapps.barcodescanner.CompoundBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.Policy;
import java.util.HashMap;
import java.util.Map;

import static android.hardware.camera2.CameraManager.*;
import static com.example.litereria.R.id.cameraPreview;

public class QRcode extends AppCompatActivity {
    SurfaceView cameraPreview;boolean isFlash = false;
    CameraSource cameraSource;Button chooser;
    ImageView flashbutton;private CameraManager mCameraManager;
    private String mCameraId; private CaptureRequest.Builder mPreviewRequestBuilder;




  public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";

    private String cameraId = CAMERA_BACK;
    private boolean isFlashSupported=true;
    private boolean isTorchOn=false;
    BarcodeDetector barcodeDetector;FirebaseDatabase database;
    DatabaseReference databaseReference;
    String uri;

    TextView decoded;String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        setContentView(R.layout.activity_qrcode);
        final boolean isFlashAvailable = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!isFlashAvailable) {
            showNoFlashError();
        }


        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        flashbutton=findViewById(R.id.flashbutton);

        cameraPreview=findViewById(R.id.cameraPreview);

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
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                isTorchOn=!isTorchOn;
                switchFlashLight(isTorchOn);
                setupFlashButton();

            }
        });
        barcodeDetector=new BarcodeDetector.Builder(QRcode.this)
                .setBarcodeFormats(Barcode.QR_CODE).build();


        displayCamer();
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

    public void displayCamer()
    {
        CameraSource.Builder mah=new CameraSource.Builder(QRcode.this,barcodeDetector)
                .setRequestedPreviewSize(640,480);
        cameraSource=mah.setAutoFocusEnabled(true).build();
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
                        vibrator.vibrate(200);
                        // cameraSource.getCameraFacing();
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
    }

    public void setupFlashButton() {
        if (cameraId.equals(CAMERA_BACK) && isFlashSupported) {
            flashbutton.setVisibility(View.VISIBLE);

            if (isTorchOn) {
                flashbutton.setImageResource(R.drawable.flashon);
            } else {
                flashbutton.setImageResource(R.drawable.flashoff);
            }

        } else {
            flashbutton.setVisibility(View.GONE);
        }
    }
    public void showNoFlashError() {
        AlertDialog alert = new AlertDialog.Builder(this)
                .create();
        alert.setTitle("Oops!");
        alert.setMessage("Flash not available in this device...");
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void switchFlashLight(boolean status) {
        try {

            cameraSource.stop();
            mCameraManager.setTorchMode(mCameraId, status);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    protected Result doInBackground(Bitmap bitmap)
    {

        if (bitmap == null)
        {

            Toast.makeText(this,"Invalid image file",Toast.LENGTH_LONG);
            Log.e("ak100", "uri is not a bitmap," + uri.toString());
            return null;
        }
        int width = bitmap.getWidth(), height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.recycle();
        bitmap = null;
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
        BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
        MultiFormatReader reader = new MultiFormatReader();
        try
        {


            Log.d("ak200","ghua");
            Result result = reader.decode(bBitmap);
            Log.d("ak200", String.valueOf(result));
            query=String.valueOf(result);
            upload();
            return result;
        }
        catch (NotFoundException e)
        {   Toast.makeText(this,"Invalid HashContact QR",Toast.LENGTH_LONG).show();
            Log.e("ak100", "decode exception", e);
            return null;
        }
    }

    private void selectImage() {

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, 1);

    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);


            cursor.close();
            doInBackground(bitmap);
        }

        }

    public static String getPath( Context context, Uri uri ) {
        String result = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver( ).query( uri, proj, null, null, null );
        if(cursor != null){
            if ( cursor.moveToFirst( ) ) {
                int column_index = cursor.getColumnIndexOrThrow( proj[0] );
                result = cursor.getString( column_index );
            }
            cursor.close( );
        }
        if(result == null) {
            result = "Not found";
        }
        return result;
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

            Toast.makeText(this,"Contact uploaded",Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();
            final Intent i = new Intent(this, MainActivity.class);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(i);

                    finish();
                }
            }, 1000);

        }
        catch (Exception e) {
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

        }






    }
}

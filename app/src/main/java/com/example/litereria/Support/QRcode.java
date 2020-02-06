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
import android.app.Dialog;
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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import com.onesignal.OneSignal;

import org.w3c.dom.Text;

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
    SurfaceView cameraPreview;
    boolean isFlash = false;
    CameraSource cameraSource;
    Button chooser;

    ImageView flashbutton;
    private CameraManager mCameraManager;
    private String mCameraId;
    private CaptureRequest.Builder mPreviewRequestBuilder;
Button fromid;

    public static final String CAMERA_FRONT = "1";
    public static final String CAMERA_BACK = "0";

    private String cameraId = CAMERA_BACK;
    private boolean isFlashSupported = true;
    private boolean isTorchOn = false;
    BarcodeDetector barcodeDetector;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    String uri;String provider;

    TextView decoded;
    String query,currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        setContentView(R.layout.activity_qrcode);
        for (UserInfo user:FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                provider="facebook";
                currentUser=Profile.getCurrentProfile().getId();
            }
            else {
                provider = "google";
                UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();

                currentUser=userInfo.getUid();
            }
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

        cameraPreview = findViewById(R.id.cameraPreview);
        fromid=findViewById(R.id.fromid);
        fromid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(QRcode.this);
                LayoutInflater inflater = QRcode.this.getLayoutInflater();



                final View view = inflater.inflate(R.layout.addcont, null);
                builder.setView(view);
                final Dialog dialog=builder.create();

                dialog.setContentView(R.layout.addcont);
                dialog.getWindow().getAttributes().windowAnimations=R.style.MyAnimation_Window;
                dialog.getWindow().setBackgroundDrawableResource(R.color.trans);
                // (0x80000000, PorterDuff.Mode.MULTIPLY);
                dialog.show();
                final EditText id=dialog.findViewById(R.id.hashid);
                TextView add=dialog.findViewById(R.id.addc);
                TextView cancel=dialog.findViewById(R.id.cancelc);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(id.getText().toString().equals(""))
                            Toast.makeText(QRcode.this,"Enter ID",Toast.LENGTH_SHORT).show();
                        else
                        {
                            query=id.getText().toString();
                            try{
                            upload();} catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(QRcode.this, "Invalid Hash ID", Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();

                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        OneSignal.setSubscription(true);
        chooser = findViewById(R.id.chooser);
        chooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(QRcode.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    // Log.e(TAG, "setxml: peremission prob");
                    ActivityCompat.requestPermissions(QRcode.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0);


                }
                else

                selectImage();
            }
        });
        decoded = findViewById(R.id.textView3);


        barcodeDetector = new BarcodeDetector.Builder(QRcode.this)
                .setBarcodeFormats(Barcode.QR_CODE).build();


        displayCamer();
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrcode = detections.getDetectedItems();
                if (qrcode.size() > 0) {


                    decoded.post(new Runnable() {
                        @Override
                        public void run() {

                            // Toast.makeText(QRcode.this,"Scan successful", Toast.LENGTH_SHORT).show();
                            // decoded.setText(qrcode.valueAt(0).displayValue);
                            query = qrcode.valueAt(0).displayValue;
                            barcodeDetector.release();
                            upload();


                        }
                    });
                }

            }
        });
    }

    public void displayCamer() {
        CameraSource.Builder mah = new CameraSource.Builder(QRcode.this, barcodeDetector)
                .setRequestedPreviewSize(640, 480);
        cameraSource = mah.setAutoFocusEnabled(true).build();
        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ContextCompat.checkSelfPermission(QRcode.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Log.e(TAG, "setxml: peremission prob");
                    ActivityCompat.requestPermissions(QRcode.this, new String[]{Manifest.permission.CAMERA}, 0);
                    if (ContextCompat.checkSelfPermission(QRcode.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        recreate();
                    else
                        startActivity(new Intent(QRcode.this, MainActivity.class));


                } else {
                    try {
                        Log.d("ak47", "test");
                        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
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


    protected Result doInBackground(Bitmap bitmap) {

        if (bitmap == null) {

            Toast.makeText(this, "Invalid image file", Toast.LENGTH_LONG);
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
        try {


            Log.d("ak200", "ghua");
            Result result = reader.decode(bBitmap);
            Log.d("ak200", String.valueOf(result));
            query = String.valueOf(result);
            upload();
            return result;
        } catch (NotFoundException e) {
            Toast.makeText(this, "Invalid HashContact QR", Toast.LENGTH_LONG).show();
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
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, 1);

    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            Uri pickedImage = data.getData();

            String[] filePath = {MediaStore.Images.Media.DATA};
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

    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }


    public void upload() {

        database = FirebaseDatabase.getInstance();
        final int[] f1 = {0};
        databaseReference = database.getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    String ke=dataSnapshot1.getKey();
                    if(ke.equals(query))
                    {
                        try {
                            databaseReference.child(currentUser).child("Contact").child(query).setValue(query);
                            databaseReference.child("Communication").child(currentUser).child("Messege").child(query).child("isTyping").setValue("false");
                            databaseReference.child("Communication").child(currentUser).child("Messege").child(query).child("lastMessege").setValue("0");
                            databaseReference.child("Communication").child(currentUser).child("Messege").child(query).child("lastMessegee").setValue("0");
                            databaseReference.child("Communication").child(currentUser).child("Messege").child(query).child("seen").setValue("0");


                            final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Communication");
                            ref.child(currentUser).child("Messege").child(query).child("chattingAt").setValue("0");

                            ref.child(currentUser).child("Messege").child(query).child("Notification").setValue("Yes");
                            ref.child(currentUser).child("Messege").child(query).child("Block").setValue("No");
                            final DatabaseReference ref1=FirebaseDatabase.getInstance().getReference().child(query).child("ChattingWith");
                            ref1.setValue("12345");

                            f1[0] =1;
                            Toast.makeText(QRcode.this, "Contact uploaded", Toast.LENGTH_SHORT).show();
                            Handler handler = new Handler();
                            final Intent i = new Intent(QRcode.this, MainActivity.class);

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(i);

                                    finish();
                                }
                            }, 500);

                        } catch (Exception e) {
                            Toast.makeText(QRcode.this,"Invaid HashContact QR code",Toast.LENGTH_LONG).show();

                        }
                    }
                }
                if(f1[0]==0){
                    Toast.makeText(QRcode.this,"Invalid Hash ID",Toast.LENGTH_SHORT).show();      }           // Toas

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}


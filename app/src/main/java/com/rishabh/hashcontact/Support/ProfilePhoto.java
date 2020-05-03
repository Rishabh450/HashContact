package com.rishabh.hashcontact.Support;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rishabh.hashcontact.MainActivity;
import com.rishabh.hashcontact.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OneSignal;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class ProfilePhoto extends AppCompatActivity {
    EditText avatar;
    Button def,upload,proceed;
    String provider;
    String id;
    int flagger;
    public   String av;
   public String url;
   boolean imageSelected=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_photo);
        avatar=findViewById(R.id.avatar);
        proceed=findViewById(R.id.proceed);
        avatar.getBackground().setAlpha(50);
        def=findViewById(R.id.defaultpic);
        upload=findViewById(R.id.uploadfromgall);
        proceed.getBackground().setAlpha(50);
        def.getBackground().setAlpha(50);
        upload.getBackground().setAlpha(50);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            for (UserInfo user:FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                if (user.getProviderId().equals("facebook.com")) {
                    provider="facebook";
                }
                else
                    provider="google";
            }


            UserInfo user = FirebaseAuth.getInstance().getCurrentUser();


                if (provider.equals("facebook")) {
                    System.out.println("User is signed in with Facebook");

                    id=Profile.getCurrentProfile().getId();
                    Log.d("helloface",id);
                    url= String.valueOf(Profile.getCurrentProfile().getProfilePictureUri(400 , 400));
                    av=Profile.getCurrentProfile().getName();

                    OneSignal.startInit(this)


                            .unsubscribeWhenNotificationsAreDisabled(false)
                            .init();


                    OneSignal.setSubscription(true);
                    OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                        @Override
                        public void idsAvailable(String userId, String registrationId) {
                            FirebaseDatabase.getInstance().getReference().child(id).child("Personal").child("Notification").setValue(userId);
                        }
                    });
                    OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);




                } else {

                    id=user.getUid();
                    Log.d("hellogoogle",id);
                    url= String.valueOf(user.getPhotoUrl());
                    av=user.getDisplayName();
                    Toast.makeText(ProfilePhoto.this,id+" gooogle",Toast.LENGTH_SHORT);
                    OneSignal.startInit(this)


                            .unsubscribeWhenNotificationsAreDisabled(false)
                            .init();


                    OneSignal.setSubscription(true);
                    OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
                        @Override
                        public void idsAvailable(String userId, String registrationId) {
                            FirebaseDatabase.getInstance().getReference().child(id).child("Personal").child("Notification").setValue(userId);
                        }
                    });
                    OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);

                }

        }
        def.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mProgress = new ProgressDialog(ProfilePhoto.this);
                mProgress.setTitle("Uploading");


                mProgress.setCancelable(false);

                mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgress.show();

                       FirebaseDatabase database = FirebaseDatabase.getInstance();
                       DatabaseReference databaseReference=database.getReference();


                        databaseReference.child(id).child("Personal").child("Photo").setValue(url).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mProgress.dismiss();
                                imageSelected=true;
                                Toast.makeText(ProfilePhoto.this,"Image Uploaded",Toast.LENGTH_SHORT).show();
                            }
                        });






            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(avatar.getText().toString().equals("")) {


                    Toast.makeText(ProfilePhoto.this,"Enter Avatar",Toast.LENGTH_LONG).show();


                }
                else if(avatar.getText().toString().trim().contains(" "))
                {
                    Toast.makeText(ProfilePhoto.this,"Avatar can't contain spaces",Toast.LENGTH_LONG).show();

                }
                else if(imageSelected==false)
                {
                    Toast.makeText(ProfilePhoto.this,"Add Profile Picture",Toast.LENGTH_LONG).show();

                }
                else
                {  final ProgressDialog mProgress = new ProgressDialog(ProfilePhoto.this);
                    mProgress.setTitle("Uploading");


                    mProgress.setCancelable(false);

                    mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgress.show();

                    FirebaseDatabase.getInstance().getReference().child(id).child("Personal").child("Name").setValue(avatar.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String ava=avatar.getText().toString().trim();
                        final Intent intent = new Intent(ProfilePhoto.this, MainActivity.class);
                        intent.putExtra("avatar", ava);
                        intent.putExtra("url", url);
                        FirebaseDatabase.getInstance().getReference().child(id).child("isReg").setValue("yes").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mProgress.dismiss();
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                });

                }


              /*  final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference databaseReference=database.getReference();


                databaseReference.child(id).child("Personal").child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String name=dataSnapshot.getValue(String.class);
                        databaseReference.child(id).child("Personal").child("Photo").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                                String url=dataSnapshot1.getValue(String.class);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/

               // startActivity(new Intent(ProfilePhoto.this,MainActivity.class));
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ProfilePhoto.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    // Log.e(TAG, "setxml: peremission prob");
                    ActivityCompat.requestPermissions(ProfilePhoto.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);


                }
                else
                    upload();

            }
        });
    }
    public void upload()
    {

        openGallery();

    }
    public String getAvatar()
    {
        return av;
    }
    public String getImage()
    {
        return url;
    }
    private void openGallery() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(ProfilePhoto.this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            Bitmap bitmap;
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                final long ts = (long) System.currentTimeMillis();
                String selectedImage =resultUri.toString();
                final FirebaseDatabase[] database1 = {FirebaseDatabase.getInstance()};
                final DatabaseReference databaseReference1 = database1[0].getReference();
                final String mediaId = String.valueOf(ts);
                final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ProfilePictures/").child(String.valueOf(ts));
                final UploadTask uploadTask;
                uploadTask = filePath.putFile(Uri.parse(selectedImage));
                Log.d("sender", selectedImage);
                final ProgressDialog mProgress = new ProgressDialog(ProfilePhoto.this);
                mProgress.setTitle("Uploading");


                mProgress.setCancelable(false);

                mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgress.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        uploadTask.cancel();
                    }
                });

                //  mProgress.setInverseBackgroundForced(setColorFilter(0x80000000, PorterDuff.Mode.MULTIPLY));
                mProgress.show();







                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                if(avatar.getText().toString().equals(""))
                                    Toast.makeText(ProfilePhoto.this,"Enter Avatar name",Toast.LENGTH_SHORT).show();
                                else
                                {
                                    flagger =1;
                                    av=avatar.getText().toString();

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReference=database.getReference();


                                    //databaseReference.child(id).child("Personal").child("Name").setValue(avatar.getText().toString());
                                    databaseReference.child(id).child("Personal").child("Photo").setValue(uri.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mProgress.dismiss();
                                            imageSelected=true;
                                            url=uri.toString();
                                            Toast.makeText(ProfilePhoto.this,"Image Uploaded",Toast.LENGTH_SHORT).show();


                                        }
                                    });
                                    DatabaseReference reference =FirebaseDatabase.getInstance().getReference().child(id).child("Personal").child("Photo");


                                }





                            }
                        });


                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress =(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        mProgress.setMessage("Uploaded: "+(int)progress+"%");
                        mProgress.setProgress((int) progress);

                    }
                });






            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if(resultCode == RESULT_OK&&(requestCode==1)) {


        }
    }

}

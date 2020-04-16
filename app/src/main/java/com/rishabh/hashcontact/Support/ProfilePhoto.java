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
import android.net.Uri;
import android.os.Bundle;
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

public class ProfilePhoto extends AppCompatActivity {
    EditText avatar;
    Button def,upload,proceed;
    String provider;
    String id;
    int flagger;
    public   String av;
   public String url;


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
                if(avatar.getText().toString().equals(""))
                    Toast.makeText(ProfilePhoto.this,"Enter Avatar name",Toast.LENGTH_SHORT).show();
                else
                {
                        av=avatar.getText().toString();
                       FirebaseDatabase database = FirebaseDatabase.getInstance();
                       DatabaseReference databaseReference=database.getReference();

                        flagger=1;
                        databaseReference.child(id).child("Personal").child("Name").setValue(avatar.getText().toString());
                        databaseReference.child(id).child("Personal").child("Photo").setValue(url).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(ProfilePhoto.this,"Press Proceed to continue",Toast.LENGTH_LONG).show();
                            }
                        });




                }

            }
        });
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flagger==1) {
                    final Intent intent = new Intent(ProfilePhoto.this, MainActivity.class);
                    intent.putExtra("avatar", av);
                    intent.putExtra("url", url);


                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(ProfilePhoto.this,"Enter the details",Toast.LENGTH_LONG).show();
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
        if(avatar.getText().toString().equals(""))
            Toast.makeText(ProfilePhoto.this,"Enter Avatar name",Toast.LENGTH_SHORT).show();
        else
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    1);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK&&(requestCode==1)) {

            final long ts = (long) System.currentTimeMillis();
            String selectedImage = data.getData().toString();
            final FirebaseDatabase[] database1 = {FirebaseDatabase.getInstance()};
            final DatabaseReference databaseReference1 = database1[0].getReference();
            final String mediaId = String.valueOf(ts);
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ProfilePictures/").child(String.valueOf(ts));
            final UploadTask uploadTask;
            uploadTask = filePath.putFile(Uri.parse(selectedImage));
            Log.d("sender", selectedImage);
            final ProgressDialog mProgress = new ProgressDialog(ProfilePhoto.this);
            mProgress.setTitle("Uploading");


            mProgress.setCancelable(true);

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
                        public void onSuccess(Uri uri) {
                            if(avatar.getText().toString().equals(""))
                                Toast.makeText(ProfilePhoto.this,"Enter Avatar name",Toast.LENGTH_SHORT).show();
                            else
                            {
                                flagger =1;
                                av=avatar.getText().toString();

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference databaseReference=database.getReference();


                                databaseReference.child(id).child("Personal").child("Name").setValue(avatar.getText().toString());
                                databaseReference.child(id).child("Personal").child("Photo").setValue(uri.toString());
                                DatabaseReference reference =FirebaseDatabase.getInstance().getReference().child(id).child("Personal").child("Photo");


                            }


                            mProgress.dismiss();
                            Toast.makeText(ProfilePhoto.this,"Press proceed to continue",Toast.LENGTH_LONG).show();


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
        }
    }

}

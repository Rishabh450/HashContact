package com.rishabh.hashcontact.Support;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadStatus extends AppCompatActivity {
    String provider,currentUser;
    EditText cont;
    ImageView pic;
    Button addimage;
    Uri pickedImage;
    CardView feedImage;
    Button uploadst;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_status);
        cont=findViewById(R.id.status_content_text);
        pic=findViewById(R.id.statuspic);
        addimage=findViewById(R.id.add_image_to_status);
        feedImage=findViewById(R.id.feedcar);
        uploadst=findViewById(R.id.upload_st);

        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                provider="facebook";
                currentUser= Profile.getCurrentProfile().getId();
            }
            else {
                provider = "google";
                UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();

                currentUser=userInfo.getUid();
            }
        }
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(UploadStatus.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    // Log.e(TAG, "setxml: peremission prob");
                    ActivityCompat.requestPermissions(UploadStatus.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);


                }
                else
                    addImage();

            }
        });



uploadst.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        upload();
    }
});




            }
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            pickedImage = data.getData();

            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            Drawable mDrawable = new BitmapDrawable(getResources(), bitmap);
            feedImage.setVisibility(View.VISIBLE);
            pic.setImageDrawable(mDrawable);


            cursor.close();
        }
    }
    public void addImage() {

        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        startActivityForResult(chooserIntent, 1);

    }
    public void upload()
    {   if(pickedImage!=null) {
        final long ts = (long) System.currentTimeMillis();
        final FirebaseDatabase[] database1 = {FirebaseDatabase.getInstance()};
        final DatabaseReference databaseReference1 = database1[0].getReference();
        final String mediaId = String.valueOf(ts);
        final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("StatusImage/").child(String.valueOf(ts));
        final UploadTask uploadTask;
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), pickedImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] dat = baos.toByteArray();
        Log.d("kamwa kiya", "true");
        uploadTask = filePath.putBytes(dat);

        // uploadTask = filePath.putFile(Uri.parse(selectedImage));
        Log.d("sender", String.valueOf(pickedImage));
        final ProgressDialog mProgress = new ProgressDialog(UploadStatus.this);
        mProgress.setTitle("Uploading...");


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
                        String url = String.valueOf(uri);

                        databaseReference1.child("Status").child(String.valueOf(ts)).child("url").setValue(url);
                        //databaseReference1.child("Feeds").child(String.valueOf(ts)).child("subEvent").setValue("");
                       /* DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(currentUser).child("Personal").child("Photo");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String profilePictureUri1 = dataSnapshot.getValue(String.class);
                                databaseReference1.child("Feeds").child(String.valueOf(ts)).child("senderURL").setValue(profilePictureUri1);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });*/
                       /* DatabaseReference ref = databaseReference1.child(currentUser).child("Personal").child("Name");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String profilePictureUri1 = dataSnapshot.getValue(String.class);
                                databaseReference1.child("Feeds").child(String.valueOf(ts)).child("event").setValue(profilePictureUri1);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
*/
                        databaseReference1.child("Status").child(String.valueOf(ts)).child("seen").setValue("");

                      //  databaseReference1.child("Sa").child(String.valueOf(ts)).child("likes").setValue("");
                        databaseReference1.child("Status").child(String.valueOf(ts)).child("userid").setValue(currentUser);


                        if (!cont.getText().toString().equals(""))
                            databaseReference1.child("Status").child(String.valueOf(ts)).child("text").setValue(cont.getText().toString());

                        else
                            databaseReference1.child("Status").child(String.valueOf(ts)).child("text").setValue("");


                        mProgress.dismiss();
                        Toast.makeText(UploadStatus.this, "Uploaded", Toast.LENGTH_LONG).show();


                    }
                });


            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                mProgress.setMessage("Uploaded: " + (int) progress + "%");
                mProgress.setProgress((int) progress);

            }
        });
    }
   /* else{
        if(feedContent.getText().toString().equals(""))
            Toast.makeText(getContext(),"No content to post",Toast.LENGTH_SHORT).show();
        else
        {
            final long ts = (long) System.currentTimeMillis();
            final DatabaseReference databaseReference1=FirebaseDatabase.getInstance().getReference();
            databaseReference1.child("Feeds").child(String.valueOf(ts)).child("comments").setValue("");
            databaseReference1.child("Feeds").child(String.valueOf(ts)).child("senderID").setValue(currentUser);





            databaseReference1.child("Feeds").child(String.valueOf(ts)).child("imageURL").setValue("");
            databaseReference1.child("Feeds").child(String.valueOf(ts)).child("subEvent").setValue("");
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(currentUser).child("Personal").child("Photo");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String profilePictureUri1 = dataSnapshot.getValue(String.class);
                    databaseReference1.child("Feeds").child(String.valueOf(ts)).child("senderURL").setValue(profilePictureUri1);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            DatabaseReference ref = databaseReference1.child(currentUser).child("Personal").child("Name");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String profilePictureUri1 = dataSnapshot.getValue(String.class);
                    databaseReference1.child("Feeds").child(String.valueOf(ts)).child("event").setValue(profilePictureUri1);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            databaseReference1.child("Feeds").child(String.valueOf(ts)).child("senderID").setValue(currentUser);


            databaseReference1.child("Feeds").child(String.valueOf(ts)).child("likes").setValue("");


            if (!feedContent.getText().toString().equals(""))
                databaseReference1.child("Feeds").child(String.valueOf(ts)).child("content").setValue(feedContent.getText().toString());



            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_LONG).show();


        }
    }*/
    }
    public  Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;


        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;

    }


}
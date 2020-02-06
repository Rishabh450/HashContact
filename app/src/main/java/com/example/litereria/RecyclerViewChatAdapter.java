package com.example.litereria;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.facebook.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import pl.droidsonroids.gif.GifImageView;

import static android.content.Context.DOWNLOAD_SERVICE;

public class RecyclerViewChatAdapter extends RecyclerView.Adapter<RecyclerViewChatAdapter.ChatViewHolder>{
    ArrayList<Messege> messeges=new ArrayList<Messege>();
    Context context;
    public RecyclerViewChatAdapter(Context context, ArrayList<Messege> messeges)
    {
        this.messeges=messeges;
        this.context=context;

    }
    public String getEmojiByUnicodee(){
        int unicode= 0x2714;
        return new String(Character.toChars(unicode));
    }

    public static boolean isRecursionEnable = true;

    void runInBackground() {
        if (!isRecursionEnable)
            // Handle not to start multiple parallel threads
            return;

        // isRecursionEnable = false; when u want to stop
        // on exception on thread make it true again
        new Thread(new Runnable() {
            @Override
            public void run() {
                // DO your work here

            }
        }).start();
    }
    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d("ak47", "onCreateViewHolder: chatadapter");
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_chat_messege,viewGroup,false);
        ChatViewHolder chatViewHolder=new ChatViewHolder(v);
        return chatViewHolder;
    }
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
    private static boolean isEmoji(String message){
        return message.matches("(?:[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83E\uDD00-\uD83E\uDDFF]|" +
                "[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|" +
                "[\u2600-\u26FF]\uFE0F?|[\u2700-\u27BF]\uFE0F?|\u24C2\uFE0F?|" +
                "[\uD83C\uDDE6-\uD83C\uDDFF]{1,2}|" +
                "[\uD83C\uDD70\uD83C\uDD71\uD83C\uDD7E\uD83C\uDD7F\uD83C\uDD8E\uD83C\uDD91-\uD83C\uDD9A]\uFE0F?|" +
                "[\u0023\u002A\u0030-\u0039]\uFE0F?\u20E3|[\u2194-\u2199\u21A9-\u21AA]\uFE0F?|[\u2B05-\u2B07\u2B1B\u2B1C\u2B50\u2B55]\uFE0F?|" +
                "[\u2934\u2935]\uFE0F?|[\u3030\u303D]\uFE0F?|[\u3297\u3299]\uFE0F?|" +
                "[\uD83C\uDE01\uD83C\uDE02\uD83C\uDE1A\uD83C\uDE2F\uD83C\uDE32-\uD83C\uDE3A\uD83C\uDE50\uD83C\uDE51]\uFE0F?|" +
                "[\u203C\u2049]\uFE0F?|[\u25AA\u25AB\u25B6\u25C0\u25FB-\u25FE]\uFE0F?|" +
                "[\u00A9\u00AE]\uFE0F?|[\u2122\u2139]\uFE0F?|\uD83C\uDC04\uFE0F?|\uD83C\uDCCF\uFE0F?|" +
                "[\u231A\u231B\u2328\u23CF\u23E9-\u23F3\u23F8-\u23FA]\uFE0F?)+");
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder chatViewHolder, final int i) {
        Log.d("ak47", "onBindViewHolder: "+i);
        String[] arr= messeges.get(i).messege.trim().split("\n");

        if(messeges.get(i).messege.trim().indexOf("https://fire")==0)
        {   char[] emoji=Character.toChars(0x1F4C1);
            String emojii = getEmojiByUnicode(0x1F4C1);
            final String imgname=messeges.get(i).messege.trim().substring(messeges.get(i).messege.indexOf(" ")+1).trim();
            final String extension=imgname.substring(imgname.lastIndexOf(".")+1).trim();

            if(extension.equalsIgnoreCase("png")||extension.equalsIgnoreCase("jpg")||extension.equalsIgnoreCase("jpeg"))
            {


                { if (messeges.get(i).sent) {
                    chatViewHolder.sent.setVisibility(View.GONE);
                    chatViewHolder.recived.setVisibility(View.GONE);
                    chatViewHolder.sendImage.setVisibility(View.VISIBLE);
                    chatViewHolder.recievedImage.setVisibility(View.GONE);
                    chatViewHolder.recievedgif.setVisibility(View.GONE);
                    chatViewHolder.sendgif.setVisibility(View.GONE);
                }
                else

                { chatViewHolder.sendImage.setVisibility(View.GONE);
                    chatViewHolder.recievedImage.setVisibility(View.VISIBLE);
                    chatViewHolder.recievedgif.setVisibility(View.GONE);
                    chatViewHolder.sendgif.setVisibility(View.GONE);
                    chatViewHolder.sent.setVisibility(View.GONE);
                    chatViewHolder.recived.setVisibility(View.GONE);
                }
                }

                CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(context);
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(30f) ;
                circularProgressDrawable.start();
              /*  final Drawable[] img = new Drawable[1];
                Glide.with(context)
                        .load(messeges.get(i).messege.trim().substring(0,messeges.get(i).messege.indexOf(" ")))
                        .placeholder(circularProgressDrawable).into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        img[0] =resource;
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });*/

                Glide.with(context)
                        .load(messeges.get(i).messege.trim().substring(0,messeges.get(i).messege.indexOf(" ")))
                        .placeholder(circularProgressDrawable).into(chatViewHolder.recievedImage);
                Glide.with(context)
                        .load(messeges.get(i).messege.trim().substring(0,messeges.get(i).messege.indexOf(" ")))
                        .placeholder(circularProgressDrawable).into(chatViewHolder.sendImage);
                chatViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FileOutputStream outStream = null;
                        //Bitmap bit=drawableToBitmap(img[0]);

                        //FileOutputStream outStream = null;
                        try {
                            final File sdCard = Environment.getExternalStorageDirectory();
                            File file = new File(sdCard.getAbsolutePath() + "/HashContact"+"/Pictures/"+imgname);
                            if(!file.exists()) {
                                //Bitmap bit = BitmapFactory.decodeResource(getResources(), R.drawable.chatback);
                                Log.d("pathssss", "onPictureTaken - wrote to ");


                                File dir = new File(sdCard.getAbsolutePath() + "/HashContact" + "/Pictures");
                                dir.mkdirs();
                                long t = System.currentTimeMillis();
                                String time = String.valueOf(t);

                                String fileName = imgname;
                                fileName.trim();
                                Log.d("pathsssss", "onPictureTaken - wrote to " + fileName);

                                File outFile = new File(dir, fileName);
                                Log.d("pathsssss", "onPictureTaken - wrote to " + fileName + dir);
                                URL url = null;
                                try {
                                    url = new URL(messeges.get(i).messege.trim().substring(0, messeges.get(i).messege.indexOf(" ")));
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                //File f = null;
                                //String url1 = ;
                                Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show();

                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(String.valueOf(url)));
                                request.setDescription("Download HashFile");
                                request.setTitle(imgname);
// in order for this if to run, you must use the android 3.2 to compile your app
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    request.allowScanningByMediaScanner();
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                }
                                Log.d("bhaiwa",Environment.DIRECTORY_DOWNLOADS);
                                request.setDestinationInExternalPublicDir( "HashContact/Pictures", imgname);

// get download service and enqueue file
                                DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                                // manager.enqueue(request);
                                final long downloadID = manager.enqueue(request);
                                Log.d("downid", String.valueOf(downloadID));
                                // final long downloadID = downloadManager.enqueue(request);
                                BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context context, Intent intent) {
                                        //Fetching the download id received with the broadcast
                                        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                                        Log.d("downid1", String.valueOf(downloadID)+" "+id);

                                        //Checking if the received broadcast is for our enqueued download by matching download id
                                        if (downloadID == id) {
                                            Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
                                            Intent intenti = new Intent(Intent.ACTION_VIEW);
                                            intenti.setDataAndType(Uri.parse(sdCard.getAbsolutePath() + "/HashContact"+"/Pictures/"+imgname), getMimeType(sdCard.getAbsolutePath() + "/HashContact"+"/Pictures/"+imgname));

                                            context.startActivity(intenti);
                                        }
                                    }

                                };
                                context.registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


                                /*try {
                                    FileUtils.copyURLToFile(url, outFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }*/

                          /*  outStream = new FileOutputStream(outFile);
                           // bit.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                            outStream.flush();
                            outStream.close();
*/
                                Log.d("pathssss", "onPictureTaken - wrote to " + outFile.getAbsolutePath());
                            }
                            else {
                                Intent intenti = new Intent(Intent.ACTION_VIEW);
                                intenti.setDataAndType(Uri.parse(sdCard.getAbsolutePath() + "/HashContact"+"/Pictures/"+imgname), getMimeType(sdCard.getAbsolutePath() + "/HashContact"+"/Pictures/"+imgname));

                                context.startActivity(intenti);
                            }



                        } finally {
                        }




                      /*  // Write to SD Card
                        try {
                            //Bitmap bit = BitmapFactory.decodeResource(getResources(), R.drawable.chatback);
                            Log.d("pathssss", "onPictureTaken - wrote to " );

                            File sdCard = Environment.getExternalStorageDirectory();
                            File dir = new File(sdCard.getAbsolutePath() + "/HashContact"+"/Pictures");
                            dir.mkdirs();
                            long t = System.currentTimeMillis();
                            String time = String.valueOf(t);

                            String fileName = imgname;
                            fileName.trim();
                            Log.d("pathssss", "onPictureTaken - wrote to " +fileName);

                            File outFile = new File(dir, fileName);
                            Log.d("pathssss", "onPictureTaken - wrote to " +fileName+dir);

                            outStream = new FileOutputStream(outFile);
                            bit.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                            outStream.flush();
                            outStream.close();

                            Log.d("pathssss", "onPictureTaken - wrote to " + outFile.getAbsolutePath());

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(outFile.getAbsolutePath()), getMimeType(outFile.getAbsolutePath()));

                            context.startActivity(intent);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                        }
*/

                    }
                });

            }
          else if(extension.equalsIgnoreCase("gif"))
            { if (messeges.get(i).sent) {
                chatViewHolder.sent.setVisibility(View.GONE);
                chatViewHolder.recived.setVisibility(View.GONE);
chatViewHolder.sendImage.setVisibility(View.GONE);
chatViewHolder.recievedImage.setVisibility(View.GONE);
                chatViewHolder.sendgif.setVisibility(View.VISIBLE);
                chatViewHolder.recievedgif.setVisibility(View.GONE);
            }
            else {
                chatViewHolder.sent.setVisibility(View.GONE);
                chatViewHolder.recived.setVisibility(View.GONE);
                chatViewHolder.sendImage.setVisibility(View.GONE);
                chatViewHolder.recievedImage.setVisibility(View.GONE);
                chatViewHolder.recievedgif.setVisibility(View.VISIBLE);
                chatViewHolder.sendgif.setVisibility(View.GONE);

            }
                CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(context);
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(30f) ;
                circularProgressDrawable.start();

                Glide.with(context)
                        .asGif()
                        .load(messeges.get(i).messege.trim().substring(0,messeges.get(i).messege.indexOf(" ")))

                        .placeholder(circularProgressDrawable).into(chatViewHolder.recievedgif);
                Glide.with(context)
                        .asGif()
                        .load(messeges.get(i).messege.trim().substring(0,messeges.get(i).messege.indexOf(" ")))
                        .placeholder(circularProgressDrawable).into(chatViewHolder.sendgif);
                chatViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messeges.get(i).messege.trim().substring(0, messeges.get(i).messege.indexOf(" "))));
                        context.startActivity(intent);
                    }
                });

            } else {
                chatViewHolder.sendImage.setVisibility(View.GONE);
                chatViewHolder.recievedImage.setVisibility(View.GONE);

                chatViewHolder.recievedgif.setVisibility(View.GONE);
                chatViewHolder.sendgif.setVisibility(View.GONE);
                if (messeges.get(i).sent) {
                    chatViewHolder.sent.setVisibility(View.VISIBLE);
                    chatViewHolder.recived.setVisibility(View.GONE);
                } else {
                    chatViewHolder.recived.setVisibility(View.VISIBLE);
                    chatViewHolder.sent.setVisibility(View.GONE);
                }

                chatViewHolder.recived.setText("\n" + "\t" + emojii + "\t" + Uri.parse(messeges.get(i).messege.trim().substring(messeges.get(i).messege.indexOf(" ") + 1)) + "\t" + "\n");
                chatViewHolder.sent.setText("\n" + "\t" + emojii + "\t" + Uri.parse(messeges.get(i).messege.trim().substring(messeges.get(i).messege.indexOf(" ") + 1)) + "\t" + "\n");

                chatViewHolder.recived.setTextSize(15.5f);
                chatViewHolder.sent.setTextSize(15.5f);

                chatViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FileOutputStream outStream = null;
                        try {
                            final File sdCard = Environment.getExternalStorageDirectory();
                            File file = new File(sdCard.getAbsolutePath() + "/HashContact"+"/Files/"+imgname);
                            if(!file.exists()) {
                                //Bitmap bit = BitmapFactory.decodeResource(getResources(), R.drawable.chatback);
                                Log.d("pathssss", "onPictureTaken - wrote to ");


                                File dir = new File(sdCard.getAbsolutePath() + "/HashContact" + "/Files");
                                dir.mkdirs();
                                long t = System.currentTimeMillis();
                                String time = String.valueOf(t);

                                String fileName = imgname;
                                fileName.trim();
                                Log.d("pathsssss", "onPictureTaken - wrote to " + fileName);

                                File outFile = new File(dir, fileName);
                                Log.d("pathsssss", "onPictureTaken - wrote to " + fileName + dir);
                                URL url = null;
                                try {
                                    url = new URL(messeges.get(i).messege.trim().substring(0, messeges.get(i).messege.indexOf(" ")));
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                }
                                //File f = null;
                                //String url1 = ;
                                Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show();
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(String.valueOf(url)));
                                request.setDescription("Download HashFile");
                                request.setTitle(imgname);
// in order for this if to run, you must use the android 3.2 to compile your app
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                    request.allowScanningByMediaScanner();
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                }
                                Log.d("bhaiwa",Environment.DIRECTORY_DOWNLOADS);
                                request.setDestinationInExternalPublicDir( "HashContact/Files", imgname);

// get download service and enqueue file
                                DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
                               // manager.enqueue(request);
                                final long downloadID = manager.enqueue(request);
                                Log.d("downid", String.valueOf(downloadID));
                                // final long downloadID = downloadManager.enqueue(request);
                                BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context context, Intent intent) {
                                        //Fetching the download id received with the broadcast
                                        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                                        Log.d("downid1", String.valueOf(downloadID)+" "+id);

                                        //Checking if the received broadcast is for our enqueued download by matching download id
                                        if (downloadID == id) {
                                            Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show();
                                            Intent intenti = new Intent(Intent.ACTION_VIEW);
                                            intenti.setDataAndType(Uri.parse(sdCard.getAbsolutePath() + "/HashContact"+"/Files/"+imgname), getMimeType(sdCard.getAbsolutePath() + "/HashContact"+"/Files/"+imgname));
                                            intenti.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                            try {
                                                context.startActivity(intenti);

                                               }catch (ActivityNotFoundException e){
                                            Toast.makeText(context, "No Application found to open this type of file.", Toast.LENGTH_LONG).show();

                                        }


                                        }
                                    }

                                };
                             context.registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


                                /*try {
                                    FileUtils.copyURLToFile(url, outFile);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }*/

                          /*  outStream = new FileOutputStream(outFile);
                           // bit.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                            outStream.flush();
                            outStream.close();
*/
                                Log.d("pathssss", "onPictureTaken - wrote to " + outFile.getAbsolutePath());
                            }
                            else {
                                Intent intenti = new Intent(Intent.ACTION_VIEW);
                                intenti.setDataAndType(Uri.parse(sdCard.getAbsolutePath() + "/HashContact"+"/Files/"+imgname), getMimeType(sdCard.getAbsolutePath() + "/HashContact"+"/Files/"+imgname));

                                try {
                                    context.startActivity(intenti);

                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, "No Application found to open this type of file.", Toast.LENGTH_LONG).show();

                                }
                            }



                        } finally {
                        }




                        /*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messeges.get(i).messege.trim().substring(0, messeges.get(i).messege.indexOf(" "))));
                        context.startActivity(intent);*/
                    }
                });

            }


        } else if(isEmoji(arr[0])) {
            chatViewHolder.sendImage.setVisibility(View.GONE);
            chatViewHolder.recievedImage.setVisibility(View.GONE);
            chatViewHolder.recievedgif.setVisibility(View.GONE);
            chatViewHolder.sendgif.setVisibility(View.GONE);
            String s= messeges.get(i).messege+" ";
            SpannableString ss1=  new SpannableString(s);
            ss1.setSpan(new RelativeSizeSpan(0.3f), s.length()-9,s.length()-1, 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.GRAY), s.length()-9, s.length()-1, 0);// set color
            if (messeges.get(i).sent) {
                chatViewHolder.sent.setVisibility(View.VISIBLE);
                chatViewHolder.recived.setVisibility(View.GONE);
            } else {
                chatViewHolder.recived.setVisibility(View.VISIBLE);
                chatViewHolder.sent.setVisibility(View.GONE);
            }
            chatViewHolder.recived.setText(ss1);
            chatViewHolder.sent.setText(ss1);
            chatViewHolder.recived.setTextSize(30f);
            chatViewHolder.sent.setTextSize(30f);

        }
        else {


            if (messeges.get(i).messege.trim().indexOf("https://tse") !=0) {
                chatViewHolder.sendImage.setVisibility(View.GONE);
                chatViewHolder.recievedImage.setVisibility(View.GONE);
                chatViewHolder.recievedgif.setVisibility(View.GONE);
                chatViewHolder.sendgif.setVisibility(View.GONE);
Log.d("nahimila",messeges.get(i).messege.trim());
            String s = messeges.get(i).messege + " ";
            SpannableString ss1 = new SpannableString(s);
            ss1.setSpan(new RelativeSizeSpan(0.6f), s.length() - 9, s.length() - 1, 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.GRAY), s.length() - 9, s.length() - 1, 0);// set color
                if (messeges.get(i).sent) {
                    chatViewHolder.sent.setVisibility(View.VISIBLE);
                    chatViewHolder.recived.setVisibility(View.GONE);
                } else {
                    chatViewHolder.recived.setVisibility(View.VISIBLE);
                    chatViewHolder.sent.setVisibility(View.GONE);
                }
            chatViewHolder.recived.setText(ss1);
            chatViewHolder.sent.setText(ss1);
            chatViewHolder.recived.setTextSize(17.5f);
            chatViewHolder.sent.setTextSize(17.5f);
            if (messeges.get(i).messege.trim().indexOf("https://") == 0) {

                chatViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(messeges.get(i).messege.trim().substring(0, messeges.get(i).messege.length() - 1 - 7)));
                        context.startActivity(intent);
                    }
                });


            }
                if(messeges.get(i).messege.trim().indexOf("LOCATION:-")==0)
                {
                    final Double lat,lon;
                    String m =messeges.get(i).messege.trim().substring(0,s.length()-9);
                    lat=Double.parseDouble(m.substring(m.indexOf(":-")+2,m.indexOf("||")));
                    lon=Double.parseDouble(m.substring(m.indexOf("||")+2,m.length()));
                     s ="Tap to open location"+"\n"+ messeges.get(i).messege.substring(s.length()-9,s.length()-1) + " ";

                     ss1 = new SpannableString(s);
                    ss1.setSpan(new RelativeSizeSpan(0.6f), s.length() - 9, s.length() - 1, 0); // set size
                    ss1.setSpan(new ForegroundColorSpan(Color.GRAY), s.length() - 9, s.length() - 1, 0);// set color
                    if (messeges.get(i).sent) {
                        chatViewHolder.sent.setVisibility(View.VISIBLE);
                        chatViewHolder.recived.setVisibility(View.GONE);
                    } else {
                        chatViewHolder.recived.setVisibility(View.VISIBLE);
                        chatViewHolder.sent.setVisibility(View.GONE);
                    }
                    chatViewHolder.recived.setText(ss1);
                    chatViewHolder.sent.setText(ss1);
                    chatViewHolder.recived.setTextSize(17.5f);
                    chatViewHolder.sent.setTextSize(17.5f);
                    Log.d("ak47",lat+" "+lon);
                    chatViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + lon + " ("+")";

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                            context.startActivity(intent);
                        }
                    });


                }

            boolean isNumber = true;
            final long d;
            try {
                d = Long.parseLong(messeges.get(i).messege.trim().substring(0, messeges.get(i).messege.length() - 1 - 7).trim());
                chatViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + d));
                        context.startActivity(intent);


                    }
                });
            } catch (NumberFormatException nfe) {
                Log.d("omegaa", String.valueOf(isNumber));
                isNumber = false;
            }



        }
            else{

                Log.d("load nhi liya",messeges.get(i).messege.trim());
                String gifsite=messeges.get(i).messege.trim().substring(0, messeges.get(i).messege.length() - 1 - 7).trim();
                if (messeges.get(i).sent) {
                    chatViewHolder.sent.setVisibility(View.GONE);
                    chatViewHolder.recived.setVisibility(View.GONE);
                    chatViewHolder.sendImage.setVisibility(View.GONE);
                    chatViewHolder.recievedImage.setVisibility(View.GONE);
                    chatViewHolder.sendgif.setVisibility(View.VISIBLE);
                    chatViewHolder.recievedgif.setVisibility(View.GONE);
                }
                else{
                    chatViewHolder.sent.setVisibility(View.GONE);
                    chatViewHolder.recived.setVisibility(View.GONE);
                    chatViewHolder.sendImage.setVisibility(View.GONE);
                    chatViewHolder.recievedImage.setVisibility(View.GONE);
                    chatViewHolder.recievedgif.setVisibility(View.VISIBLE);
                    chatViewHolder.sendgif.setVisibility(View.GONE);
                }

                CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(context);
                circularProgressDrawable.setStrokeWidth(5f);
                circularProgressDrawable.setCenterRadius(30f) ;
                circularProgressDrawable.start();

                Glide.with(context)
                        .asGif()
                        .load(gifsite.trim())

                        .placeholder(circularProgressDrawable).into(chatViewHolder.recievedgif);
                Glide.with(context)
                        .asGif()
                        .load(gifsite.trim())
                        .placeholder(circularProgressDrawable).into(chatViewHolder.sendgif);


            }
        }



    }
    private String getMimeType(String url)
    {
        String parts[]=url.split("\\.");
        String extension=parts[parts.length-1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    public int getItemCount() {
        Log.d("ak47", "getItemCount: "+messeges.size());
        return messeges.size();
    }
    public static Bitmap drawableToBitmap (Drawable drawable) {
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
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        EmojiconTextView sent,recived;
        LinearLayout linearLayout;
        ImageView sendImage,recievedImage;

        GifImageView sendgif,recievedgif;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            sent=itemView.findViewById(R.id.sendMessegeBubble);

            recived=itemView.findViewById(R.id.recivedMessegeBubble);
            linearLayout=itemView.findViewById(R.id.ChatBubbleContainer);
            sent.setEmojiconSize(120);
            recived.setEmojiconSize(120);
            sendImage=itemView.findViewById(R.id.sendMessegeImage);
            recievedImage=itemView.findViewById(R.id.recivedMessegeImage);
            sendgif=itemView.findViewById(R.id.sendMessegegif);
            recievedgif=itemView.findViewById(R.id.recivedMessegegif);

        }
    }

}

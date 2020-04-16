package com.rishabh.hashcontact;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ablanco.zoomy.DoubleTapListener;
import com.ablanco.zoomy.LongPressListener;
import com.ablanco.zoomy.Zoomy;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rishabh.hashcontact.Models.FeedPost;
import com.rishabh.hashcontact.Support.CallBackClick;
import com.rishabh.hashcontact.Support.FeedComments;
import com.rishabh.hashcontact.Support.Likes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rishabh.hashcontact.Support.MyCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<FeedPost> feedPosts;
    private String mcurrentuid;
    public static int apos;
    private boolean is_already_liked=false;
    private String mpost_id="";



    private FragmentManager manager;

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout feedLayout,del;
        public TextView subevent_name,like_text,eventname,content,sendername,timeago;
        public ImageView like_icon,sender_image,saveicon,zoom;
        View thumbqView;LinearLayout root;
        ImageView postImage;
        public LinearLayout like_layout,comment_layout,share_layout;
        RelativeLayout postImageView;
        ImageView heart;
        ProgressBar progressBar;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            feedLayout = (LinearLayout) itemView;
            progressBar=itemView.findViewById(R.id.feed_post_image_progress_bar);
            postImageView=itemView.findViewById(R.id.feed_post_image_parent);
            postImage=itemView.findViewById(R.id.feed_post_image);
            //PhotoViewAttacher yourAttacher = new PhotoViewAttacher(postImage);

            thumbqView=itemView.findViewById(R.id.feed_post_image);
            heart=itemView.findViewById(R.id.heart);

            subevent_name=itemView.findViewById(R.id.feed_post_sub_event_name);
            eventname=itemView.findViewById(R.id.feed_post_event_name);
            content=itemView.findViewById(R.id.feed_post_content);
            root=itemView.findViewById(R.id.containerroot);
            sendername=itemView.findViewById(R.id.senderuser);
            zoom=itemView.findViewById(R.id.expanded_image);
            del=itemView.findViewById(R.id.del);
            sender_image=itemView.findViewById(R.id.feed_post_event_icon);
            like_text=itemView.findViewById(R.id.like_textview);
            timeago=itemView.findViewById(R.id.timeago);
            saveicon=itemView.findViewById(R.id.save_post);
            like_icon=itemView.findViewById(R.id.like_icon);
            like_layout=itemView.findViewById(R.id.feed_post_upvote);
            comment_layout=itemView.findViewById(R.id.comments_post);
            share_layout=itemView.findViewById(R.id.feed_post_share);
        }
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout feedLayout = (LinearLayout) (LayoutInflater.from(context).inflate(
                R.layout.feed_item, parent, false));
        return new CustomViewHolder(feedLayout);
    }

    public FeedAdapter(Context context, FragmentManager manager, ArrayList<FeedPost> mfeedPosts, String currentuid) {
        this.context = context;
        this.feedPosts = mfeedPosts;
        this.mcurrentuid = currentuid;
        this.manager = manager;

        Log.e("VIVZ", "FeedAdapter: called COUNT = " + mfeedPosts.size());
    }
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int WEEK_MILLIS = 7 * DAY_MILLIS ;

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now =System.currentTimeMillis();;


        long diff = now - time;
        if(diff>0) {


            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes ago";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours ago";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else if (diff < 7 * DAY_MILLIS) {
                return diff / DAY_MILLIS + " days ago";
            } else if (diff < 2 * WEEK_MILLIS) {
                return "a week ago";
            } else if (diff < WEEK_MILLIS * 3) {
                return diff / WEEK_MILLIS + " weeks ago";
            } else {
                Date date = new Date(time); // *1000 is to convert seconds to milliseconds
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy "); // the format of your date
                sdf.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));

                return sdf.format(date);


            }

        }
        else {

            diff=time-now;
            if (diff < MINUTE_MILLIS) {
                return "this minute";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute later";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " minutes later";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour later";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " hours later";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "tomorrow";
            } else if (diff < 7 * DAY_MILLIS) {
                return diff / DAY_MILLIS + " days later";
            } else if (diff < 2 * WEEK_MILLIS) {
                return "a week later";
            } else if (diff < WEEK_MILLIS * 3) {
                return diff / WEEK_MILLIS + " weeks later";
            } else {
                java.util.Date date = new java.util.Date((long) time);
                return date.toString();
            }
        }

    }


    @Override
    public int getItemCount() {
        return feedPosts.size();
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, final int position) {
        holder.eventname.setText(feedPosts.get(position).getEvent());
        holder.sendername.setText(feedPosts.get(position).getEvent());

        //PhotoViewAttacher yourAttacher = new PhotoViewAttacher(holder.postImage);
        Zoomy.Builder builder = new Zoomy.Builder((Activity) context).longPressListener(new LongPressListener() {
            @Override
            public void onLongPress(View v) {
                if(mcurrentuid.equals(feedPosts.get(position).getSenderId())) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    LayoutInflater inflater = ((Activity) context).getLayoutInflater();


                    final View view = inflater.inflate(R.layout.deletedialog, null);
                    builder.setView(view);
                    final Dialog dialog = builder.create();

                    dialog.setContentView(R.layout.deletedialog);
                    dialog.getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
                    dialog.getWindow().setBackgroundDrawableResource(R.color.trans);
                    // (0x80000000, PorterDuff.Mode.MULTIPLY);
                    dialog.show();

                    ImageView delete = dialog.findViewById(R.id.delete);
                    TextView cancelButton = (TextView) dialog.findViewById(R.id.cancel);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final DatabaseReference dref = FirebaseDatabase.getInstance().getReference().child("Feeds");
                            dref.child(feedPosts.get(position).getPostid()).removeValue();
                            dialog.dismiss();


                        }
                    });
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        }).doubleTapListener(new DoubleTapListener() {
            @Override
            public void onDoubleTap(View v) {
                Log.d("doubleTap","hehe");
                holder.like_layout.callOnClick();
            }
        }).target(holder.postImage);
        builder.register();

        holder.timeago.setText(getTimeAgo(Long.parseLong(feedPosts.get(position).getPostid())) );

        holder.subevent_name.setText(feedPosts.get(position).getSubEvent());
        if(feedPosts.get(position).getContent().equals(""))
            holder.content.setVisibility(View.GONE);
        else
        holder.content.setText(feedPosts.get(position).getContent());
        Locale locale = new Locale("en","IN");


        holder.like_text.setText(feedPosts.get(position).getLikes().size()+" likes");
        holder.postImageView.setVisibility(View.VISIBLE);
        holder.progressBar.setVisibility(View.VISIBLE);
        Log.e("Hey", feedPosts.get(position).getImageURL());
        CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(context);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(10f) ;
        circularProgressDrawable.start();
        holder.like_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, Likes.class);
                intent.putExtra("feedid",feedPosts.get(position).getPostid());
                Log.d("bhejedata",feedPosts.get(position).getPostid());
                context.startActivity(intent);            }
        });
        Glide.with(context)
                .load(feedPosts.get(position).getSender_url())
                .placeholder(circularProgressDrawable)
                .apply(RequestOptions.circleCropTransform())
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                })
                .into(holder.sender_image);
        if (feedPosts.get(position).getImageURL().equals("")) {
            holder.saveicon.setVisibility(View.GONE);

            holder.postImageView.setVisibility(View.GONE);

        } else {
            Glide.with(context)
                    .load(feedPosts.get(position).getImageURL())

.fitCenter()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.postImage);
        }
        mpost_id=feedPosts.get(position).getPostid();

        is_already_liked=feedPosts.get(position).isIs_already_liked();
        if(is_already_liked==true){
            holder.like_icon.setImageResource(R.drawable.upvoted);
        }

        Log.i( "onBindViewHolder: ",is_already_liked+"");



        final DatabaseReference dref= FirebaseDatabase.getInstance().getReference().child("Feeds");

        holder.comment_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, FeedComments.class);
                intent.putExtra("feedid",feedPosts.get(position).getPostid());
                context.startActivity(intent);
            }
        });

     /*  holder.thumbqView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(holder.thumbqView, holder.postImage.getDrawable(),holder.zoom,holder.root);
            }
        });
*/
        // Retrieve and cache the system's default "short" animation time.
        shortAnimationDuration = context.getResources().getInteger(
                android.R.integer.config_shortAnimTime);

       holder.saveicon.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FileOutputStream outStream = null;
               try {


                   final File sdCard = Environment.getExternalStorageDirectory();
                   File file = new File(sdCard.getAbsolutePath() + "/HashContact"+"/FeedImages/"+feedPosts.get(position).getPostid()+"."+"jpg");
                   if(!file.exists()) {
                       //Bitmap bit = BitmapFactory.decodeResource(getResources(), R.drawable.chatback);
                       Log.d("pathssss", "onPictureTaken - wrote to ");
                        String imgname=feedPosts.get(position).getPostid()+"."+"jpg";

                       File dir = new File(sdCard.getAbsolutePath() + "/HashContact" + "/FeedImages");
                       dir.mkdirs();
                       long t = System.currentTimeMillis();
                       String time = String.valueOf(t);

                       String fileName = feedPosts.get(position).getPostid()+"."+"jpg";
                       fileName.trim();
                       Log.d("pathsssss", "onPictureTaken - wrote to " + fileName);

                       File outFile = new File(dir, fileName);
                       Log.d("pathsssss", "onPictureTaken - wrote to " + fileName + dir);
                       URL url = null;
                       try {
                           url = new URL(feedPosts.get(position).getImageURL());
                       } catch (MalformedURLException e) {
                           e.printStackTrace();
                       }
                       //File f = null;
                       //String url1 = ;
                       Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show();
                       DownloadManager.Request request = new DownloadManager.Request(Uri.parse(String.valueOf(url)));
                       request.setDescription("Download HashFile");
                       request.setTitle(feedPosts.get(position).getPostid());
// in order for this if to run, you must use the android 3.2 to compile your app
                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                           request.allowScanningByMediaScanner();
                           request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                       }
                       Log.d("bhaiwa",Environment.DIRECTORY_DOWNLOADS);
                       request.setDestinationInExternalPublicDir( "HashContact/FeedImages", feedPosts.get(position).getPostid()+"."+"jpg");

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
                                   holder.saveicon.setImageResource(R.drawable.ic_bookmark_black_24dp);
                                   Intent intenti = new Intent(Intent.ACTION_VIEW);
                                   intenti.setDataAndType(Uri.parse(Uri.parse(sdCard.getAbsolutePath() + "/HashContact"+"/FeedImages/"+feedPosts.get(position).getPostid())+"."+"jpg"), getMimeType(sdCard.getAbsolutePath() + "/HashContact"+"/Files/"+feedPosts.get(position).getPostid()+"."+"jpg"));
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
                       intenti.setDataAndType(Uri.parse(sdCard.getAbsolutePath() + "/HashContact"+"/FeedImages/"+feedPosts.get(position).getPostid()+"."+"jpg"), getMimeType(sdCard.getAbsolutePath() + "/HashContact"+"/FeedImages/"+feedPosts.get(position).getPostid()+"."+"jpg"));

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
        holder.share_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri imgUri = null;
                Drawable dr = ((ImageView) holder.postImage).getDrawable();
                if(dr!=null) {
                    Bitmap imgBitmap = drawableToBitmap(dr);
                  String  imgBitmapPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), imgBitmap, "title", null);
                   imgUri = Uri.parse(imgBitmapPath);

                      

                   


                }
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_STREAM, imgUri);

                sendIntent.putExtra(Intent.EXTRA_TEXT,feedPosts.get(position).getEvent()+"\n"+
                        feedPosts.get(position).getSubEvent()+"\n"+
                        feedPosts.get(position).getContent() );
                sendIntent.setType("*/*");
                context.startActivity(Intent.createChooser(sendIntent,"Share this article via:"));

            }
        });

        holder.like_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 setter(holder.getAdapterPosition());
                 Log.d("prevpossrec", String.valueOf(holder.getAdapterPosition()));

                HashMap<String,Object> nc=new HashMap<>();
                if(feedPosts.get(position).isIs_already_liked()){
                    Log.e("TAG", "onClick: level 2");
                    nc.put(mcurrentuid,null);
                    dref.child(feedPosts.get(position).getPostid()).child("likes").child(mcurrentuid).setValue(null);
                    for (com.rishabh.hashcontact.Models.Likes lk : feedPosts.get(position).getLikes()) {
                        if (lk.getUser_id().equals(mcurrentuid)) {
                            feedPosts.get(position).getLikes().remove(lk);
                            break;
                        }
                    }

                    holder.like_text.setText(feedPosts.get(position).getLikes().size() + " Likes");
                    feedPosts.get(position).setIs_already_liked(false);

                    holder.like_icon.setImageResource(R.drawable.upvote);

                }
                else{
                    holder.heart.setVisibility(View.VISIBLE);
                    holder.heart.animate()
                            .scaleX(0.3f).scaleY(0.3f)//scale to quarter(half x,half y)
                            .alpha(0.5f) // make it less visible
                            .rotation(360f) // one round turns
                            .setDuration(500) // all take 1 seconds
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    //animation ended
                                    holder.heart.setVisibility(View.GONE);
                                    dref.child(feedPosts.get(position).getPostid()).child("likes").child(mcurrentuid).setValue(mcurrentuid);

                                }
                            });
                    dref.child(feedPosts.get(position).getPostid()).child("likes").child(mcurrentuid).setValue(mcurrentuid);


                    feedPosts.get(position).getLikes().add(new com.rishabh.hashcontact.Models.Likes(mcurrentuid));
                    holder.like_text.setText(feedPosts.get(position).getLikes().size() + " Likes");
                    feedPosts.get(position).setIs_already_liked(true);
                    holder.like_icon.setImageResource(R.drawable.upvoted);
                    Log.e("TAG", "onClick: level 6");
                }

            }
        });

    }
    public void setter(int pos)
    {
        apos=pos;
    }
    private Animator currentAnimator;

    // The system "short" animation time duration, in milliseconds. This
    // duration is ideal for subtle animations or animations that occur
    // very frequently.
    private int shortAnimationDuration;
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
    private void zoomImageFromThumb(final View thumbView, Drawable imageResId,ImageView zoom,LinearLayout roo) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.

        if (currentAnimator != null) {
            currentAnimator.cancel();
        }


        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) zoom;
        expandedImageView.setImageDrawable(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
                roo.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        shortAnimationDuration = context.getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
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

    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    String savedItemClicked;

   /* @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub

        ImageView view = (ImageView) v;
        dumpEvent(event);

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d("draggg", "mode=DRAG");
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                Log.d("drap", "oldDist=" + oldDist);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d("vb", "mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                Log.d("hjj", "mode=NONE");
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // ...
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    Log.d("hk", "newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true;
    }*/

    private void dumpEvent(MotionEvent event) {
        String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
        sb.append("]");
        Log.d("gdg", sb.toString());
    }

    /** Determine the space between the first two fingers */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /** Calculate the mid point of the first two fingers */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
    public void readData(final CallBackClick myCallback, int postid) {
     /*   DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        databaseReference.child(userID).child("Contact").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                myCallback.onCallback(dataSnapshot.hasChild(currentUser));
            }

    @Override
    public void onCancelled(DatabaseError databaseError) {}
});*/
        myCallback.onCallback(postid);
    }
}

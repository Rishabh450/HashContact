package com.example.litereria;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsHolder> {

    ArrayList<String> arrayList=new ArrayList<String>();
    String x;


    String feedid;
    String current;

    Context context;

    public CommentsAdapter(ArrayList<String> arrayList, Context context,String feedid,String current) {
        this.arrayList = arrayList;
        this.context = context;
        this.feedid = feedid;
        this.current=current;
    }

    @NonNull
    @Override
    public CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_comment,parent,false);
        CommentsAdapter.CommentsHolder commentsHolder=new CommentsAdapter.CommentsHolder(v);

        return commentsHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final CommentsHolder holder, final int position) {
        final DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Feeds").child(feedid).child("comments").child(arrayList.get(position));
        Log.d("commentadaptk",arrayList.get(position));
        holder.root.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Feeds").child(feedid).child("comments").child(arrayList.get(position)).child("id");
                ref.addListenerForSingleValueEvent ( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String id=dataSnapshot.getValue(String.class);
                        Log.d("commentidd",id+" "+current);
                        if(id.equals(current)) {
                            Log.d("commentidd",id+" "+current +"ghusa");
                            AlertDialog.Builder builder=new AlertDialog.Builder(context);
                            Log.d("commentidd",id+" "+current +"ghusa1");
                            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                            Log.d("commentidd",id+" "+current +"ghusa1");


                            final View view = inflater.inflate(R.layout.deletedialog, null);
                            builder.setView(view);
                            final Dialog dialog=builder.create();
                            Log.d("commentidd",id+" "+current +"ghusa2");

                            dialog.setContentView(R.layout.deletedialog);
                            dialog.getWindow().getAttributes().windowAnimations=R.style.MyAnimation_Window;
                            dialog.getWindow().setBackgroundDrawableResource(R.color.trans);

                            dialog.show();

                            ImageView delete =  dialog.findViewById(R.id.delete);
                            TextView cancelButton = (TextView) dialog.findViewById(R.id.cancel);
                            delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    reference.removeValue();
                                    dialog.dismiss();

                                   // return false;
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            return true;

            }
        });
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String comm;
                for(DataSnapshot ds:dataSnapshot.getChildren())

                {



                     if(ds.getKey().equals("time"))
                        holder.time.setText(getTimeAgo(Long.parseLong(ds.getValue(String.class))) );
                    else if(ds.getKey().equals("id"))
                    {
                        String id=ds.getValue(String.class);
                        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child(id).child("Personal");
                        ref.child("Photo").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String url=dataSnapshot.getValue(String.class);
                                CircularProgressDrawable circularProgressDrawable = new  CircularProgressDrawable(context);
                                circularProgressDrawable.setStrokeWidth(5f);
                                circularProgressDrawable.setCenterRadius(10f) ;
                                circularProgressDrawable.start();
                                Glide.with(context)
                                        .load(url)
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
                                        .into(holder.commenterpic);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        ref.child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String name=dataSnapshot.getValue(String.class);
                                Log.d ("commenterName",name);
                                int l=name.length();
                               // DatabaseReference myref=FirebaseDatabase.getInstance().getReference().child("Feeds").child(feedid).child("comments").child(arrayList.get(position)).child("content");
                                reference.child("content").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String cont=dataSnapshot.getValue(String.class);
                                        String sourceString = "<b>" +name + "</b> " +"  "+ cont;
                                        holder.comment.setText(Html.fromHtml(sourceString));
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class CommentsHolder extends RecyclerView.ViewHolder{
        ImageView commenterpic,likecomment;
        TextView comment;
        TextView time;
        LinearLayout root;


        public CommentsHolder(@NonNull View itemView) {
            super(itemView);
            root=itemView.findViewById(R.id.rootComment);
            commenterpic=itemView.findViewById(R.id.commenter_pic);
            likecomment=itemView.findViewById(R.id.like_comment);
            comment=itemView.findViewById(R.id.commentername);
            time=itemView.findViewById(R.id.timecom);
        }
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
                return (diff/SECOND_MILLIS)+"s";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "1m";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + "m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "1h";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS +"h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else if (diff < 7 * DAY_MILLIS) {
                return diff / DAY_MILLIS + "d";
            } else if (diff < 2 * WEEK_MILLIS) {
                return "1w";
            } else if (diff < WEEK_MILLIS * 3) {
                return diff / WEEK_MILLIS + "w";
            } else {
                java.util.Date date = new java.util.Date((long) time);
                return date.toString();
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
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = formatter.format(new Date(Long.parseLong(String.valueOf(time))));

                return dateString;
            }
        }

    }
}

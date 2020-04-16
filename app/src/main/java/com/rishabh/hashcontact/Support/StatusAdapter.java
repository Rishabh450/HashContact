package com.rishabh.hashcontact.Support;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.rishabh.hashcontact.Models.Status;
import com.rishabh.hashcontact.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusHolder> {
    Context context;
    ArrayList<Status> status=new ArrayList<>();
    String currentuser;

    public StatusAdapter(Context context, ArrayList<Status> status, String currentuser) {
        this.context = context;
        this.status = status;
        this.currentuser = currentuser;
    }

    @NonNull
    @Override
    public StatusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.statusitem,parent,false);
        StatusHolder statusHolder=new StatusHolder(v);
        return statusHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull final StatusHolder holder, final int position) {
        String userid=status.get(position).getUser();
        long diff= Math.abs(Long.parseLong(status.get(position).getStatus_id())-System.currentTimeMillis());//-System.currentTimeMillis();
        if(diff>86400000)
        {
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Status").child(status.get(position).getStatus_id());
            ref.removeValue();

        }
        if(status.get(position).isSeen()) {
            holder.statuspic.setBackgroundColor(context.getResources().getColor(R.color.darkgray));
            holder.statuspic.setPadding(3,3,3,3);
        }
        holder.statuspic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!status.get(position).isSeen()) {
                    String time = String.valueOf(System.currentTimeMillis());
                    DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Status").child(status.get(position).getStatus_id()).child("seen").child(currentuser).child("time");
                    firebaseDatabase.setValue(time).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent=new Intent(context,SeeStatus.class);
                            intent.putExtra("text",status.get(position).getStext());
                            intent.putExtra("url",status.get(position).getUrl());
                            intent.putExtra("statusid",status.get(position).getStatus_id());
                            intent.putExtra("id",status.get(position).getUser());
                            intent.putParcelableArrayListExtra("seenlist",status.get(position).getSeen());
                            context.startActivity(intent);

                        }
                    });
                }
                else
                {
                    Intent intent=new Intent(context,SeeStatus.class);
                    intent.putExtra("text",status.get(position).getStext());
                    intent.putExtra("url",status.get(position).getUrl());
                    intent.putExtra("statusid",status.get(position).getStatus_id());
                    intent.putExtra("id",status.get(position).getUser());
                    intent.putParcelableArrayListExtra("seenlist",status.get(position).getSeen());
                    context.startActivity(intent);
                }


            }
        });
        DatabaseReference firebaseDatabase=FirebaseDatabase.getInstance().getReference().child(userid).child("Personal");
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String text=dataSnapshot.child("Name").getValue(String.class);
                holder.statusText.setText(text);
                String url=dataSnapshot.child("Photo").getValue(String.class);

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
                        .into(holder.statuspic);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return status.size();
    }

    public class StatusHolder extends RecyclerView.ViewHolder{
        TextView statusText;
        ImageView statuspic;

        public StatusHolder(@NonNull View itemView) {
            super(itemView);
            statuspic=itemView.findViewById(R.id.statuspic);
            statusText=itemView.findViewById(R.id.statususer);

        }
    }
}

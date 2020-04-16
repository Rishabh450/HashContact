package com.rishabh.hashcontact;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

public class PostLikeAdapter extends RecyclerView.Adapter<PostLikeAdapter.ChatListViewHolder> {
    ArrayList<String> contacts=new ArrayList<String>();
    Context context;int lastPosition;
    public PostLikeAdapter(ArrayList<String> contacts, Context context)
    {
        this.contacts=contacts;
        this.context=context;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull PostLikeAdapter.ChatListViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public PostLikeAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_like,viewGroup,false);
        PostLikeAdapter.ChatListViewHolder chatListViewHolder=new PostLikeAdapter .ChatListViewHolder(v);
        return chatListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder holder, final int position) {
        String likerkey=contacts.get(position);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child(likerkey).child("Personal");
        reference.child("Photo").addListenerForSingleValueEvent(new ValueEventListener() {
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
                        .into(holder.likerpic);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        reference.child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name=dataSnapshot.getValue(String.class);
                holder.title.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ChatListViewHolder extends RecyclerView.ViewHolder {
        LinearLayout item;
        ImageView likerpic;
        TextView title;
        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);

            likerpic=itemView.findViewById(R.id.like_pic);
            title=itemView.findViewById(R.id.likername);
        }
    }
}

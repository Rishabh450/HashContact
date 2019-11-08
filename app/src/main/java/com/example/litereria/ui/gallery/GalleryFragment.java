package com.example.litereria.ui.gallery;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.litereria.R;
import com.example.litereria.RecyclerViewChatList;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GalleryFragment extends Fragment implements NavHost {
    ArrayList<Map<String, String>> contacts = new ArrayList<Map<String, String>>();

    private FirebaseDatabase database;
    private DatabaseReference myRef;Map<String, String> mp = new HashMap<String, String>();

    RecyclerViewChatList recyclerViewChatList;

    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View vh = inflater.inflate(R.layout.fragment_gallery, container, false);

        getdata();
        intit(vh);


        return vh;

    }

    private void getdata() {
        Log.d("ak47", "getdata: ");
        String c = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        String currentUser = Profile.getCurrentProfile().getId();;
        database = FirebaseDatabase.getInstance();
        String email = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        myRef = database.getReference().child(currentUser).child("Contact");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                contacts.clear();
                //for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    String user = dataSnapshot1.getKey();

                    mp = (Map<String, String>) dataSnapshot1.getValue();
                    mp.put("key",dataSnapshot1.getKey());

                    Log.d("ak46", "onDataChange: " + mp + " added");

                    contacts.add(mp);
                    Log.d("lognaa", "onDataChange: " + contacts);


                }
                recyclerViewChatList.notifyDataSetChanged();




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void intit(View vh) {
        RecyclerView recyclerView = vh.findViewById(R.id.chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d("choka", "onDataChange: " + contacts);
        recyclerViewChatList = new RecyclerViewChatList(contacts, getContext());
        recyclerView.setAdapter(recyclerViewChatList);
    }

    @NonNull
    @Override
    public NavController getNavController() {
        return null;
    }
}

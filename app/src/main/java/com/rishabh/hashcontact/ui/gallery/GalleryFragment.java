package com.rishabh.hashcontact.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rishabh.hashcontact.R;
import com.rishabh.hashcontact.RecyclerViewChatList;
import com.rishabh.hashcontact.Support.QRcode;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GalleryFragment extends Fragment {
    ArrayList<Map<String, String>> contacts = new ArrayList<Map<String, String>>();
    ImageView addcontact;String provider;
    private FirebaseDatabase database;
    private DatabaseReference myRef;Map<String, String> mp = new HashMap<String, String>();

    RecyclerViewChatList recyclerViewChatList;

    private GalleryViewModel galleryViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View vh = inflater.inflate(R.layout.fragment_gallery, container, false);
        addcontact=vh.findViewById(R.id.addcontact);
      // FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        getdata();
        intit(vh);
        addcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), QRcode.class);
                startActivity(intent);
            }
        });

        return vh;

    }

    private void getdata() {
        Log.d("ak47", "getdata: ");
      //  String c = FirebaseAuth.getInstance().getCurrentUser().getEmail().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUser = null;

        for (UserInfo useer:FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (useer.getProviderId().equals("facebook.com")) {
                provider="facebook";
                currentUser=Profile.getCurrentProfile().getId();
            }
            else {
                provider = "google";
                UserInfo userInfo = FirebaseAuth.getInstance().getCurrentUser();

                currentUser=userInfo.getUid();
            }
        }


        database = FirebaseDatabase.getInstance();
       // database.setPersistenceEnabled(true);
        String email = String.valueOf(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        myRef = database.getReference().child(currentUser).child("Contact");
        myRef.keepSynced(true);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                contacts.clear();
                //for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()) {
                    String user = dataSnapshot1.getKey();
                    Log.w("babaji",user);
                    Map<String ,String> mp1=new HashMap<>();


                    // mp = (Map<String, String>) dataSnapshot1.getValue();
                    mp1.put("key",user);

                    Log.d("ak46", "onDataChange: " + mp1 + " added");

                    contacts.add(mp1);
                    Log.d("lognaa", "onDataChange: " + contacts);


                }
                if(contacts.isEmpty()) {
                    addcontact.setVisibility(View.VISIBLE);

                   // Toast.makeText(getContext(), "No contacts", Toast.LENGTH_SHORT).show();
                }
                else
                    addcontact.setVisibility(View.GONE);

                recyclerViewChatList.notifyDataSetChanged();




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Log.d("madar", "onDataChange: " + contacts);



    }

    private void intit(View vh) {

        RecyclerView recyclerView = vh.findViewById(R.id.chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Log.d("choka", "onDataChange: " + contacts);



        recyclerViewChatList = new RecyclerViewChatList(contacts, getContext());
        recyclerView.setAdapter(recyclerViewChatList);
    }



}

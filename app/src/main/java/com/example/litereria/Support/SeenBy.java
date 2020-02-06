package com.example.litereria.Support;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.litereria.Models.Seen;
import com.example.litereria.R;
import com.example.litereria.SeenByAdapter;

import java.util.ArrayList;

public class SeenBy extends AppCompatActivity {
    RecyclerView seenby;
    SeenByAdapter seenByAdapter;
    ArrayList<Seen> seens=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seen_by);
        seenby=findViewById(R.id.seenList);
        Intent intent=getIntent();
       seens= intent.getParcelableArrayListExtra("seenList");
       setUpRecycler();

    }
    public void setUpRecycler()
    {
        seenby.setLayoutManager(new LinearLayoutManager(this));
        seenByAdapter=new SeenByAdapter(seens,this);
        seenby.setAdapter(seenByAdapter);
    }
}

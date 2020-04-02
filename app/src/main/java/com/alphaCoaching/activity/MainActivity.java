package com.alphaCoaching.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.R;
import com.alphaCoaching.fragment.ListItem;
import com.alphaCoaching.fragment.MainFragment;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {




    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItem> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems =new ArrayList<>();


        for(int i=0;i<=10;i++)
        {
            ListItem listItem=new ListItem("Heading " + (i+1),"Description is the dummy text");
            listItems.add(listItem);
        }
        adapter=new MainFragment(listItems,this);
        recyclerView.setAdapter(adapter);

    }
}

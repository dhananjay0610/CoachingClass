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
    private ArrayList<ListItem> listItems=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new MainFragment(listItems,this);
        recyclerView.setAdapter(adapter);


  // listItems =new ArrayList<>();
        for(int i=0;i<=10;i++)
        {
            ListItem listItem=new ListItem("Heading " + (i+1),"Description 1afsdjfhjakshkfhklashkjdfhkajshdfsdflasdfalkhsdjfkahjfhaskljdhfkalhsjdhfkalskjdhfkljahksdkfjshakjfhkjsahkfhkjsahkjfhl","2 april");
            listItems.add(listItem);
        }


//                listItems.add(new ListItem("heading 1","disfdshaskdhfkasjhdfkjsah","2 april"));
//        listItems.add(new ListItem("heading 1","disfdshaskdhfkasjhdfkjsah","2 april"));
//        listItems.add(new ListItem("heading 1","disfdshaskdhfkasjhdfkjsah","2 april"));
//        listItems.add(new ListItem("heading 1","disfdshaskdhfkasjhdfkjsah","2 april"));
//        listItems.add(new ListItem("heading 1","disfdshaskdhfkasjhdfkjsah","2 april"));
//        listItems.add(new ListItem("heading 1","disfdshaskdhfkasjhdfkjsah","2 april"));
//        listItems.add(new ListItem("heading 1","disfdshaskdhfkasjhdfkjsah","2 april"));
//        listItems.add(new ListItem("heading 1","disfdshaskdhfkasjhdfkjsah","2 april"));
      // adapter.notifyDataSetChanged();
    }
}

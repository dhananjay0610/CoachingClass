package com.alphaCoaching.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.Model.PDFModel;
import com.alphaCoaching.Model.SpinnerModel;
import com.alphaCoaching.R;
import com.alphaCoaching.Utils.UserSharedPreferenceManager;
import com.alphaCoaching.adapter.PDFAdapter;
import com.alphaCoaching.adapter.SpinnerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

public class PdfListActivity extends AppCompatActivity implements PDFAdapter.OnPdfItemClick {

    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private PDFAdapter adapter;
    private FirebaseFirestore mFireBaseDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_list);
        mFireBaseDB = FirebaseFirestore.getInstance();

        //toolbar
        toolbar = findViewById(R.id.ToolbarOfPdfListActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("PDF ");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recycler_viewPdf);

        String standard = getCurrentUserStandard();
        Query query = mFireBaseDB.collection("PDF")
                .whereEqualTo(Constant.UserCollectionFields.STANDARD, standard);

        //recycler options
        FirestoreRecyclerOptions<PDFModel> options = new FirestoreRecyclerOptions.Builder<PDFModel>()
                .setQuery(query, PDFModel.class)
                .build();
        adapter = new PDFAdapter(options, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);


        //for checklist data
        final String[] Subjects = {"Maths", "Chemistry", "Biology", "Physics"};
        Spinner spinner = findViewById(R.id.dropdown_menu);

        ArrayList<SpinnerModel> list = new ArrayList<>();

        for (String s : Subjects) {
            SpinnerModel object = new SpinnerModel();
            object.setTitle(s);
            object.setSelected(false);
            list.add(object);
        }
        SpinnerAdapter myAdapter = new SpinnerAdapter(PdfListActivity.this, 0, list);
        spinner.setAdapter(myAdapter);

        //


    }

    private String getCurrentUserStandard() {
        return UserSharedPreferenceManager.getUserInfo(getApplicationContext(), UserSharedPreferenceManager.userInfoFields.USER_STANDARD);

    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onItemClick(DocumentSnapshot snapshot, int position) {

        // final String[] url1 = {""};
        DocumentReference documentReference = mFireBaseDB.collection("PDF").document(snapshot.getId());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    assert document != null;
                    if (document.exists()) {
                        String url = (String) document.get("url");
                        //  url1[0] = urls;
                        Log.d("PdfListActivity", document.getId() + "url is : " + url);
                        Intent intent1 = new Intent(PdfListActivity.this, PdfViewActivity.class);
                        intent1.putExtra("url", url);
                        startActivity(intent1);
                    }
                }
            }
        });
    }
}

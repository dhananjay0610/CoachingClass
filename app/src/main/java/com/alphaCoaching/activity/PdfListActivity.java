package com.alphaCoaching.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.Model.PDFModel;
import com.alphaCoaching.Model.SpinnerModel;
import com.alphaCoaching.Model.SubjectModel;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

import static com.alphaCoaching.AlphaApplication.getAppContext;

public class PdfListActivity extends AppCompatActivity implements PDFAdapter.OnPdfItemClick {

    private static RecyclerView recyclerView;
    private Toolbar toolbar;
    private static PDFAdapter adapter;
    private static FirebaseFirestore mFireBaseDB;
    private Spinner spinner;
    private static FirestoreRecyclerOptions<PDFModel> options, optionsTemp;
    private static ArrayList<PDFModel> pdfs;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_list);
        mFireBaseDB = FirebaseFirestore.getInstance();
        mContext = getAppContext();

        //toolbar
        toolbar = findViewById(R.id.ToolbarOfPdfListActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("PDF ");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recycler_viewPdf);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        spinner = findViewById(R.id.dropdown_menu);
        getAllPdfs();
        getAllSubjects();
    }

    private void getAllPdfs() {
        pdfs = new ArrayList<>();
        mFireBaseDB.collection(Constant.PDF_COLLECTION)
                .whereEqualTo(Constant.UserCollectionFields.STANDARD, UserSharedPreferenceManager.getUserInfo(getAppContext(), UserSharedPreferenceManager.userInfoFields.USER_STANDARD))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
//                            UserSharedPreferenceManager.storeUserSubjects(getApplicationContext(), snapshot.getId(), snapshot.get("name").toString());
                            PDFModel model = snapshot.toObject(PDFModel.class);
                            model.setId(snapshot.getId());
                            pdfs.add(model);
                        }
                        adapter = new PDFAdapter(pdfs, this);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                        recyclerView.setAdapter(adapter);
                    }
                });
    }

    private void getAllSubjects() {
        ArrayList<SubjectModel> subjects = new ArrayList<>();
        mFireBaseDB.collection(Constant.SUBJECT_COLLECTION)
                .whereEqualTo(Constant.UserCollectionFields.STANDARD, UserSharedPreferenceManager.getUserInfo(getAppContext(), UserSharedPreferenceManager.userInfoFields.USER_STANDARD))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int i = 0;
                        for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                            UserSharedPreferenceManager.storeUserSubjects(getApplicationContext(), snapshot.getId(), snapshot.get("name").toString());
                            subjects.add(snapshot.toObject(SubjectModel.class));
                            subjects.get(i).setId(snapshot.getId());
                            i++;
                        }
                        setSpinner(subjects);
                    }
                });
    }

    private void setSpinner(ArrayList<SubjectModel> Subjects) {
        ArrayList<SpinnerModel> list = new ArrayList<>();

        for (SubjectModel s : Subjects) {
            SpinnerModel object = new SpinnerModel();
            object.setTitle(s.getName());
            object.setSelected(false);
            object.setId(s.getId());
            list.add(object);
        }
        SpinnerAdapter myAdapter = new SpinnerAdapter(PdfListActivity.this, 0, list);
        spinner.setAdapter(myAdapter);
    }

    public ArrayList<PDFModel> getOptions() {
        return pdfs;
    }

    public FirestoreRecyclerOptions<PDFModel> getOptionsTemp() {
        return optionsTemp;
    }


    public void updateAdapter(ArrayList<PDFModel> options) {
        adapter = new PDFAdapter(options, this);
        recyclerView.setAdapter(adapter);
    }

    private String getCurrentUserStandard() {
        return UserSharedPreferenceManager.getUserInfo(getApplicationContext(), UserSharedPreferenceManager.userInfoFields.USER_STANDARD);

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onItemClick(PDFModel snapshot, int position) {

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

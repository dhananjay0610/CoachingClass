package com.alphaCoaching.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import static com.alphaCoaching.AlphaApplication.getAppContext;

public class PdfListActivity extends AppCompatActivity implements PDFAdapter.OnPdfItemClick, NavigationView.OnNavigationItemSelectedListener {

    private static RecyclerView recyclerView;
    private static PDFAdapter adapter;
    private DrawerLayout drawerLayout;
    private FirebaseAuth fireAuth;
    private static FirebaseFirestore mFireBaseDB;
    private Spinner spinner;
    Toolbar toolbar;
    private static ArrayList<PDFModel> pdfs;
    private NavigationView navigationView;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_list);
        mFireBaseDB = FirebaseFirestore.getInstance();
        fireAuth = FirebaseAuth.getInstance();

        //toolbar
        toolbar = findViewById(R.id.ToolbarOfPdfListActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("PDF ");
        recyclerView = findViewById(R.id.recycler_viewPdf);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        spinner = findViewById(R.id.dropdown_menu);
        drawerLayout = findViewById(R.id.pdfDrawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        mContext = getApplicationContext();
        toggle.syncState();
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
                            UserSharedPreferenceManager.storeUserSubjects(getApplicationContext(), snapshot.getId(), Objects.requireNonNull(snapshot.get("name")).toString());
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
        SpinnerAdapter myAdapter = new SpinnerAdapter(mContext, 0, list, SpinnerAdapter.PDF_TYPE);
        spinner.setAdapter(myAdapter);
    }

    public ArrayList<PDFModel> getOptions() {
        return pdfs;
    }

//    public FirestoreRecyclerOptions<PDFModel> getOptionsTemp() {
//        return optionsTemp;
//    }


    public void updateAdapter(Context context, ArrayList<PDFModel> options) {
        adapter = new PDFAdapter(options, this);
        recyclerView.setAdapter(adapter);
    }

//    private String getCurrentUserStandard() {
//        return UserSharedPreferenceManager.getUserInfo(getApplicationContext(), UserSharedPreferenceManager.userInfoFields.USER_STANDARD);
//
//    }

    @Override
    public void onStart() {
        super.onStart();
        navigationView = findViewById(R.id.nav_viewOfPdfActivity);
        navigationView.getMenu().getItem(3).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onItemClick(PDFModel snapshot, int position) {
        String url = snapshot.getUrl();
        Intent intent1 = new Intent(mContext, PdfViewActivity.class);
        intent1.putExtra("url", url);
        mContext.startActivity(intent1);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_quiz) {
            loadQuizData();
        } else if (id == R.id.nav_home) {
            loadHomeActivity();
        } else if (id == R.id.nav_Pdf) {
//            do nothing.
//            onBackPressed();
        } else if (id == R.id.nav_userProfile) {
            loadUserData();
        } else if (id == R.id.nav_logout) {
            new LoginActivity().logoutUser(UserSharedPreferenceManager.getUserInfo(getApplicationContext(), UserSharedPreferenceManager.userInfoFields.USER_UUID));
            fireAuth.signOut();
            UserSharedPreferenceManager.removeUserData(getApplicationContext());
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_videos) {
            startVideoActivity();
        } else if (id == R.id.nav_tutos) {
            openTutosActivity();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadUserData() {
        Intent i = new Intent(PdfListActivity.this, UserProfileActivity.class);
        startActivity(i);
    }

    private void loadHomeActivity() {
        Intent i = new Intent(PdfListActivity.this, MainActivity.class);
        startActivity(i);
    }

    private void loadQuizData() {
        Intent i = new Intent(PdfListActivity.this, QuizListActivity.class);
        startActivity(i);
    }

    private void startVideoActivity() {
        Intent intent = new Intent(PdfListActivity.this, VideoCategoryListActivity.class);
        startActivity(intent);
    }

    private void openTutosActivity() {
        Intent intent = new Intent(this, TutosActivity.class);
        startActivity(intent);
    }
}
package com.alphaCoaching.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alphaCoaching.Constant.Constant;
import com.alphaCoaching.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RequestCredentialsActivity extends AppCompatActivity {

    private EditText mNameEt;
    private EditText mClassEt;
    private EditText mContactNumberEt;
    private EditText mAddressEt;
    private EditText mPercentageEt;
    private EditText mReferenceEt;
    private Button mSubmitButton;
    private LinearLayout mProgressBar;
    private FirebaseFirestore mFirestore;

    private String mName, mClass, mContact, mAddress, mPercentage, mRefernce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_credentials);
        mNameEt = findViewById(R.id.name);
        mClassEt = findViewById(R.id.standard);
        mContactNumberEt = findViewById(R.id.contact);
        mAddressEt = findViewById(R.id.address);
        mPercentageEt = findViewById(R.id.percentage);
        mReferenceEt = findViewById(R.id.reference);
        mSubmitButton = findViewById(R.id.submitButton);
        mProgressBar = findViewById(R.id.pregressBar);
        mFirestore = FirebaseFirestore.getInstance();

        mSubmitButton.setOnClickListener(v -> checkData());

    }

    private void checkData() {
        mName = mNameEt.getText().toString();
        mClass = mClassEt.getText().toString();
        mContact = mContactNumberEt.getText().toString();
        mAddress = mAddressEt.getText().toString();
        mPercentage = mPercentageEt.getText().toString();
        mRefernce = mReferenceEt.getText().toString();

        if (mName.isEmpty()) {
            mNameEt.setError("Fill this field");
            return;
        }
        if (mClass.isEmpty()) {
            mClassEt.setError("Fill this field");
            return;
        }
        if (mContact.isEmpty()) {
            mContactNumberEt.setError("Fill this field");
            return;
        }
        if (mAddress.isEmpty()) {
            mAddressEt.setError("Fill this field");
            return;
        }

        saveData();
    }

    private void saveData() {
        mProgressBar.setVisibility(View.VISIBLE);
        Map<String, Object> data = new HashMap<>();
        data.put(Constant.RequestedUserFields.NAME, mName);
        data.put(Constant.RequestedUserFields.CLASS, mClass);
        data.put(Constant.RequestedUserFields.ADDRESS, mAddress);
        data.put(Constant.RequestedUserFields.CONTACT, mContact);
        data.put(Constant.RequestedUserFields.PERCNTAGE, mPercentage);
        data.put(Constant.RequestedUserFields.REFERENCE, mRefernce);

        mFirestore.collection(Constant.REQUESTED_USER_LIST)
                .add(data)
                .addOnSuccessListener(documentReference -> openLoginScreen())
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Facing some issue. Please try again later.", Toast.LENGTH_SHORT).show();
                    openLoginScreen();
                });
    }

    private void openLoginScreen() {
        mProgressBar.setVisibility(View.GONE);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}

package com.alphaCoaching.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.alphaCoaching.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;


public class PdfViewActivity extends AppCompatActivity {
    PDFView pdfView;
    String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);
        Toolbar toolbar = findViewById(R.id.toolbarOfViewActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        pdfView = findViewById(R.id.PdfViewer);
        Intent intent = getIntent();
        link = intent.getStringExtra("url");
        Log.d("PdfViewActivity", "url : " + link);
        if (!isConnected()) {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(PdfViewActivity.this);
            builder.setTitle("NoInternet Connection Alert")
                    .setMessage("GO to Setting ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, which) -> startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)))
                    .setNegativeButton("No", (dialog, which) -> Toast.makeText(PdfViewActivity.this, "Go Back TO HomePage!", Toast.LENGTH_SHORT).show());
            //Creating dialog box
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        new RetrievePDFStream().execute(link);
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", Objects.requireNonNull(e.getMessage()));
        }
        return connected;
    }

     class RetrievePDFStream extends AsyncTask<String, Void, InputStream> {
        ProgressDialog progressDialog;

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL urlx = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) urlx.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;
        }

        protected void onPreExecute() {
            Log.d("PdfViewActivity", "before execution :" + link);
            progressDialog = new ProgressDialog(PdfViewActivity.this);
            progressDialog.setTitle("getting the book content...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
            progressDialog.dismiss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)//means home default hai kya yesok
        {
            onBackPressed();
            return true;
        }
        return false;
    }
}


//public class PdfViewActivity extends AppCompatActivity {
//    PDFView pdfView;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pdf_view);
//
//        Toolbar toolbar = findViewById(R.id.toolbarOfViewActivity);
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
//
//        pdfView = findViewById(R.id.PdfViewer);
//        //    progressBar = findViewById(R.id.progressbar);
//
//        if (getIntent() != null) {
//            String ViewType = getIntent().getStringExtra("Viewtype");
//            String url = getIntent().getStringExtra("url");
//            if (ViewType != null) {
//
////                if (ViewType.equals("assets")) {
////                    pdfView.fromAsset("Shivjayanti_Aptitiude_Test.pdf")
////                            .password(null)
////                            .defaultPage(0)
////                            .enableSwipe(true)
////                            .swipeHorizontal(false)
////                            .enableDoubletap(true)
////                            .onDraw(new OnDrawListener() {
////                                @Override
////                                public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
////
////                                }
////                            })
////                            .onDrawAll(new OnDrawListener() {
////                                @Override
////                                public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
////
////                                }
////                            })
////                            .onPageError(new OnPageErrorListener() {
////                                @Override
////                                public void onPageError(int page, Throwable t) {
////                                    Toast.makeText(PdfViewActivity.this, "Error" + page, Toast.LENGTH_SHORT).show();
////                                }
////                            })
////                            .onTap(new OnTapListener() {
////                                @Override
////                                public boolean onTap(MotionEvent e) {
////                                    return true;
////                                }
////                            })
////                            .onRender(new OnRenderListener() {
////                                @Override
////                                public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
////                                    pdfView.fitToWidth();
////                                }
////                            })
////                            .enableAnnotationRendering(true)
////                            .load();
////                }
////                else if (ViewType.equals("storage")) {
////                    Uri pdfFile = Uri.parse(getIntent().getStringExtra("FileUri"));
////                    pdfView.fromUri(pdfFile)
////                            .password(null)
////                            .defaultPage(0)
////                            .enableSwipe(true)
////                            .swipeHorizontal(false)
////                            .enableDoubletap(true)
////                            .onDraw(new OnDrawListener() {
////                                @Override
////                                public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
////
////                                }
////                            })
////                            .onDrawAll(new OnDrawListener() {
////                                @Override
////                                public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
////
////                                }
////                            })
////                            .onPageError(new OnPageErrorListener() {
////                                @Override
////                                public void onPageError(int page, Throwable t) {
////                                    Toast.makeText(PdfViewActivity.this, "Error" + page, Toast.LENGTH_SHORT).show();
////                                }
////                            })
////                            .onTap(new OnTapListener() {
////                                @Override
////                                public boolean onTap(MotionEvent e) {
////                                    return true;
////                                }
////                            })
////                            .onRender(new OnRenderListener() {
////                                @Override
////                                public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
////                                    pdfView.fitToWidth();
////                                }
////                            })
////                            .enableAnnotationRendering(true)
////                            .load();
////
////
////                }
////
//                //for internet
//                final LoadingDialog loadingDialog = new LoadingDialog(PdfViewActivity.this);
//                if (ViewType.equals("internet")) {
//                    // progressBar.setVisibility(View.VISIBLE);
//                    loadingDialog.startLoadingDialog();
//                    Log.d("PdfViewActivity", "Url  is : " + url);
//                    FileLoader.with(this)
//                            .load(url)
//                            .fromDirectory("PDFFiles", FileLoader.DIR_EXTERNAL_PUBLIC)
//                            .asFile(new FileRequestListener<File>() {
//                                @Override
//                                public void onLoad(FileLoadRequest request, FileResponse<File> response) {
////                                    progressBar.setVisibility(View.GONE);
//                                    loadingDialog.dismiss();
//
//                                    File pdfFile = response.getBody();
//
//                                    pdfView.fromFile(pdfFile)
//                                            .password(null)
//                                            .defaultPage(0)
//                                            .enableSwipe(true)
//                                            .swipeHorizontal(false)
//                                            .enableDoubletap(true)
//                                            .onDraw((canvas, pageWidth, pageHeight, displayedPage) -> {
//
//                                            })
//                                            .onDrawAll((canvas, pageWidth, pageHeight, displayedPage) -> {
//
//                                            })
//                                            .onPageError((page, t) -> Toast.makeText(PdfViewActivity.this, "Error" + page, Toast.LENGTH_SHORT).show())
//                                            .onTap(e -> true)
//                                            .onRender((nbPages, pageWidth, pageHeight) -> pdfView.fitToWidth())
//                                            .enableAnnotationRendering(true)
//                                            .load();
//                                }
//
//                                @Override
//                                public void onError(FileLoadRequest request, Throwable t) {
//                                    Toast.makeText(PdfViewActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
//                                    loadingDialog.dismiss();
//                                    //progressBar.setVisibility(View.GONE);
//                                }
//                            });
//                }
//            } else {
//                TextUtils.isEmpty(null);
//            }
//        }
//    }
//}
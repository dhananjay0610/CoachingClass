package com.alphaCoaching.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alphaCoaching.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;
import java.util.Objects;


public class PdfViewActivity extends AppCompatActivity {
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        Toolbar toolbar = findViewById(R.id.toolbarOfViewActivity);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        pdfView = findViewById(R.id.PdfViewer);
        //    progressBar = findViewById(R.id.progressbar);

        if (getIntent() != null) {
            String ViewType = getIntent().getStringExtra("Viewtype");
            String url = getIntent().getStringExtra("url");
            if (ViewType != null) {

//                if (ViewType.equals("assets")) {
//                    pdfView.fromAsset("Shivjayanti_Aptitiude_Test.pdf")
//                            .password(null)
//                            .defaultPage(0)
//                            .enableSwipe(true)
//                            .swipeHorizontal(false)
//                            .enableDoubletap(true)
//                            .onDraw(new OnDrawListener() {
//                                @Override
//                                public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
//
//                                }
//                            })
//                            .onDrawAll(new OnDrawListener() {
//                                @Override
//                                public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
//
//                                }
//                            })
//                            .onPageError(new OnPageErrorListener() {
//                                @Override
//                                public void onPageError(int page, Throwable t) {
//                                    Toast.makeText(PdfViewActivity.this, "Error" + page, Toast.LENGTH_SHORT).show();
//                                }
//                            })
//                            .onTap(new OnTapListener() {
//                                @Override
//                                public boolean onTap(MotionEvent e) {
//                                    return true;
//                                }
//                            })
//                            .onRender(new OnRenderListener() {
//                                @Override
//                                public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
//                                    pdfView.fitToWidth();
//                                }
//                            })
//                            .enableAnnotationRendering(true)
//                            .load();
//                }
//                else if (ViewType.equals("storage")) {
//                    Uri pdfFile = Uri.parse(getIntent().getStringExtra("FileUri"));
//                    pdfView.fromUri(pdfFile)
//                            .password(null)
//                            .defaultPage(0)
//                            .enableSwipe(true)
//                            .swipeHorizontal(false)
//                            .enableDoubletap(true)
//                            .onDraw(new OnDrawListener() {
//                                @Override
//                                public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
//
//                                }
//                            })
//                            .onDrawAll(new OnDrawListener() {
//                                @Override
//                                public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {
//
//                                }
//                            })
//                            .onPageError(new OnPageErrorListener() {
//                                @Override
//                                public void onPageError(int page, Throwable t) {
//                                    Toast.makeText(PdfViewActivity.this, "Error" + page, Toast.LENGTH_SHORT).show();
//                                }
//                            })
//                            .onTap(new OnTapListener() {
//                                @Override
//                                public boolean onTap(MotionEvent e) {
//                                    return true;
//                                }
//                            })
//                            .onRender(new OnRenderListener() {
//                                @Override
//                                public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
//                                    pdfView.fitToWidth();
//                                }
//                            })
//                            .enableAnnotationRendering(true)
//                            .load();
//
//
//                }
//
                //for internet
                final LoadingDialog loadingDialog = new LoadingDialog(PdfViewActivity.this);
                if (ViewType.equals("internet")) {
                    // progressBar.setVisibility(View.VISIBLE);
                    loadingDialog.startLoadingDialog();
                    Log.d("PdfViewActivity", "Url  is : " + url);
                    FileLoader.with(this)
                            .load(url)
                            .fromDirectory("PDFFiles", FileLoader.DIR_EXTERNAL_PUBLIC)
                            .asFile(new FileRequestListener<File>() {
                                @Override
                                public void onLoad(FileLoadRequest request, FileResponse<File> response) {
//                                    progressBar.setVisibility(View.GONE);
                                    loadingDialog.dismiss();

                                    File pdfFile = response.getBody();

                                    pdfView.fromFile(pdfFile)
                                            .password(null)
                                            .defaultPage(0)
                                            .enableSwipe(true)
                                            .swipeHorizontal(false)
                                            .enableDoubletap(true)
                                            .onDraw((canvas, pageWidth, pageHeight, displayedPage) -> {

                                            })
                                            .onDrawAll((canvas, pageWidth, pageHeight, displayedPage) -> {

                                            })
                                            .onPageError((page, t) -> Toast.makeText(PdfViewActivity.this, "Error" + page, Toast.LENGTH_SHORT).show())
                                            .onTap(e -> true)
                                            .onRender((nbPages, pageWidth, pageHeight) -> pdfView.fitToWidth())
                                            .enableAnnotationRendering(true)
                                            .load();
                                }

                                @Override
                                public void onError(FileLoadRequest request, Throwable t) {
                                    Toast.makeText(PdfViewActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismiss();
                                    //progressBar.setVisibility(View.GONE);
                                }
                            });
                }
            } else {
                TextUtils.isEmpty(ViewType);
            }
        }
    }
}
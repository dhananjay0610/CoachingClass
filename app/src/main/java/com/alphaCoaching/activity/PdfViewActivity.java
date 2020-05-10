package com.alphaCoaching.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alphaCoaching.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.krishna.fileloader.FileLoader;
import com.krishna.fileloader.listener.FileRequestListener;
import com.krishna.fileloader.pojo.FileResponse;
import com.krishna.fileloader.request.FileLoadRequest;

import java.io.File;


public class PdfViewActivity extends AppCompatActivity {
    PDFView pdfView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        Toolbar toolbar = findViewById(R.id.toolbarofviewactivity);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pdfView = findViewById(R.id.pdfviewer);
    //    progressBar = findViewById(R.id.progressbar);

        if (getIntent() != null) {
            String viewtype = getIntent().getStringExtra("Viewtype");
            String url = getIntent().getStringExtra("url");
            if (viewtype != null || !TextUtils.isEmpty(viewtype)) {

//                if (viewtype.equals("assets")) {
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
//                else if (viewtype.equals("storage")) {
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
                if (viewtype.equals("internet")) {
                    // progressBar.setVisibility(View.VISIBLE);
                    loadingDialog.startLoadingDialog();
                    Log.d("PdfViewActivity", "dfshajhsdfkhfkjhfdskh" + url);
                    FileLoader.with(this)
                            .load(url)
                            //.load("https://firebasestorage.googleapis.com/v0/b/alphacoaching-403bb.appspot.com/o/uploads%2F1586428597010.pdf?alt=media&token=c56b8d45-8399-4817-abbd-cdf9773d72a5")
                            // .load("http://www.pdf995.com/samples/pdf.pdf")
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
                                            .onDraw(new OnDrawListener() {
                                                @Override
                                                public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                                                }
                                            })
                                            .onDrawAll(new OnDrawListener() {
                                                @Override
                                                public void onLayerDrawn(Canvas canvas, float pageWidth, float pageHeight, int displayedPage) {

                                                }
                                            })
                                            .onPageError(new OnPageErrorListener() {
                                                @Override
                                                public void onPageError(int page, Throwable t) {
                                                    Toast.makeText(PdfViewActivity.this, "Error" + page, Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .onTap(new OnTapListener() {
                                                @Override
                                                public boolean onTap(MotionEvent e) {
                                                    return true;
                                                }
                                            })
                                            .onRender(new OnRenderListener() {
                                                @Override
                                                public void onInitiallyRendered(int nbPages, float pageWidth, float pageHeight) {
                                                    pdfView.fitToWidth();
                                                }
                                            })
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
            }
        }
    }
}
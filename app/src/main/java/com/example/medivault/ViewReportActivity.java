package com.example.medivault;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class ViewReportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        ImageView imageView = findViewById(R.id.imageView);
        WebView webView = findViewById(R.id.webView);

        String fileUrl = getIntent().getStringExtra("fileUrl");

        if (fileUrl == null || fileUrl.isEmpty()) {
            Toast.makeText(this, "Invalid file URL ❌", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        /*
         * 🔑 IMPORTANT LOGIC
         * Cloudinary RAW URLs often have NO file extension.
         * So:
         * - Try image loading FIRST
         * - If image fails, fallback to PDF/WebView
         */

        imageView.setVisibility(ImageView.VISIBLE);
        webView.setVisibility(WebView.GONE);

        Glide.with(this)
                .load(fileUrl)
                .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {

                    @Override
                    public boolean onLoadFailed(
                            com.bumptech.glide.load.engine.GlideException e,
                            Object model,
                            com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                            boolean isFirstResource) {

                        // 🔄 Image failed → try PDF / document
                        imageView.setVisibility(ImageView.GONE);
                        webView.setVisibility(WebView.VISIBLE);

                        WebSettings settings = webView.getSettings();
                        settings.setJavaScriptEnabled(true);
                        settings.setDomStorageEnabled(true);
                        settings.setAllowFileAccess(true);
                        settings.setAllowContentAccess(true);
                        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

                        webView.setWebViewClient(new WebViewClient());

                        // Google Docs viewer works for PDFs & docs
                        String pdfViewer =
                                "https://docs.google.com/gview?embedded=true&url=" + fileUrl;
                        webView.loadUrl(pdfViewer);

                        return true; // we handled the failure
                    }

                    @Override
                    public boolean onResourceReady(
                            android.graphics.drawable.Drawable resource,
                            Object model,
                            com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                            com.bumptech.glide.load.DataSource dataSource,
                            boolean isFirstResource) {

                        // ✅ Image loaded successfully
                        return false;
                    }
                })
                .into(imageView);
    }
}

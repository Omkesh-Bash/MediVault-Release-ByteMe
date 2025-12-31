package com.example.medivault;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

public class ViewReportActivity extends AppCompatActivity {

    ImageView imageView;
    WebView webView;
    Button btnShareDoctor;

    FirebaseFirestore firestore;
    FirebaseAuth auth;

    String fileUrl;

    public static void open(Context context, String fileUrl) {
        Intent intent = new Intent(context, ViewReportActivity.class);
        intent.putExtra("fileUrl", fileUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_report);

        imageView = findViewById(R.id.imageView);
        webView = findViewById(R.id.webView);
        btnShareDoctor = findViewById(R.id.btnShareDoctor);

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        fileUrl = getIntent().getStringExtra("fileUrl");

        if (fileUrl == null || fileUrl.isEmpty()) {
            Toast.makeText(this, "Invalid report", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadReport(fileUrl);

        // 🔽 SHARE BUTTON FIXED HERE
        btnShareDoctor.setOnClickListener(v -> downloadShareViaFirebase());
    }

    // ✅ Load image or PDF inside app
    private void loadReport(String url) {

        imageView.setVisibility(ImageView.VISIBLE);
        webView.setVisibility(WebView.GONE);

        Glide.with(this)
                .load(url)
                .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {

                    @Override
                    public boolean onLoadFailed(
                            com.bumptech.glide.load.engine.GlideException e,
                            Object model,
                            com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                            boolean isFirstResource) {

                        imageView.setVisibility(ImageView.GONE);
                        webView.setVisibility(WebView.VISIBLE);

                        WebSettings settings = webView.getSettings();
                        settings.setJavaScriptEnabled(true);
                        settings.setDomStorageEnabled(true);

                        webView.setWebViewClient(new WebViewClient());
                        webView.loadUrl(
                                "https://docs.google.com/gview?embedded=true&url=" + url
                        );
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(
                            android.graphics.drawable.Drawable resource,
                            Object model,
                            com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                            com.bumptech.glide.load.DataSource dataSource,
                            boolean isFirstResource) {
                        return false;
                    }
                })
                .into(imageView);
    }

    // ✅ DOWNLOAD → SHARE (NO .BIN ISSUE)
    private void downloadAndShareReport() {

        new Thread(() -> {
            try {
                URL url = new URL(fileUrl);
                java.net.URLConnection connection = url.openConnection();
                connection.connect();

                String mimeType = connection.getContentType();
                String extension = getExtensionFromMime(mimeType);

                File file = new File(getCacheDir(), "Medical_Report." + extension);

                InputStream in = connection.getInputStream();
                FileOutputStream out = new FileOutputStream(file);

                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                }

                in.close();
                out.close();

                runOnUiThread(() -> shareFile(file, mimeType));

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Share failed", Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }


    // ✅ SHARE WITH PROPER MIME TYPE
    private void shareFile(File file, String mimeType) {

        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                file
        );

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mimeType);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(intent, "Share Report"));
    }

    private String getExtensionFromMime(String mime) {

        if (mime == null) return "pdf";

        if (mime.contains("pdf")) return "pdf";
        if (mime.contains("jpeg")) return "jpg";
        if (mime.contains("png")) return "png";
        if (mime.contains("msword")) return "doc";
        if (mime.contains("officedocument")) return "docx";

        return "pdf"; // safe fallback
    }

    private String getMimeType(String ext) {
        switch (ext) {
            case "pdf":
                return "application/pdf";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "doc":
                return "application/msword";
            case "docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default:
                return "*/*";
        }
    }

    // ✅ DOCTOR SHARE (UNCHANGED & SAFE)
    private void openDoctorSelectDialog() {

        DoctorSelectDialog dialog =
                new DoctorSelectDialog(
                        ViewReportActivity.this,
                        doctorUid -> {

                            Map<String, Object> reportMap = new HashMap<>();
                            reportMap.put("fileUrl", fileUrl);
                            reportMap.put("title", "Medical Report");
                            reportMap.put("type", "Shared Report");
                            reportMap.put("date", "");
                            reportMap.put("read", false);
                            reportMap.put("sharedAt", System.currentTimeMillis());

                            firestore.collection("users")
                                    .document(doctorUid)
                                    .collection("received_reports")
                                    .add(reportMap)
                                    .addOnSuccessListener(doc ->
                                            Toast.makeText(
                                                    ViewReportActivity.this,
                                                    "Report shared with doctor ✅",
                                                    Toast.LENGTH_SHORT
                                            ).show()
                                    )
                                    .addOnFailureListener(e ->
                                            Toast.makeText(
                                                    ViewReportActivity.this,
                                                    "Share failed: " + e.getMessage(),
                                                    Toast.LENGTH_LONG
                                            ).show()
                                    );
                        }
                );

        dialog.show();
    }

    private void downloadShareViaFirebase() {

        StorageReference ref =
                FirebaseStorage.getInstance().getReferenceFromUrl(fileUrl);

        ref.getMetadata().addOnSuccessListener(metadata -> {

            String mimeType = metadata.getContentType();
            String fileName = metadata.getName(); // REAL filename
            if (fileName == null) fileName = "Medical_Report";

            File file = new File(getCacheDir(), fileName);

            ref.getFile(file)
                    .addOnSuccessListener(task -> shareFileCorrectly(file, mimeType))
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Download failed", Toast.LENGTH_LONG).show()
                    );

        }).addOnFailureListener(e ->
                Toast.makeText(this, "Metadata error", Toast.LENGTH_LONG).show()
        );
    }

    private void shareFileCorrectly(File file, String mimeType) {

        Uri uri = FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                file
        );

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(mimeType != null ? mimeType : "*/*");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(Intent.createChooser(intent, "Share Report"));
    }

}

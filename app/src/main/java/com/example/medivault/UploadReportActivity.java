package com.example.medivault;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class UploadReportActivity extends AppCompatActivity {

    EditText etTitle, etType, etDate;
    TextView tvFileName;
    ImageView ivFileIcon;
    Button btnUpload;
    Uri fileUri;

    private static final int FILE_PICKER_REQUEST = 100;
    private File reportsDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        etTitle = findViewById(R.id.etTitle);
        etType = findViewById(R.id.etType);
        etDate = findViewById(R.id.etDate);
        tvFileName = findViewById(R.id.tvFileName);
        ivFileIcon = findViewById(R.id.ivFileIcon);
        btnUpload = findViewById(R.id.btnUpload);

        reportsDir = new File(getFilesDir(), "MyReports");
        if (!reportsDir.exists()) reportsDir.mkdir();

        ivFileIcon.setOnClickListener(v -> chooseFile());
        tvFileName.setOnClickListener(v -> chooseFile());

        btnUpload.setOnClickListener(v -> uploadReport());

        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int y = c.get(Calendar.YEAR), m = c.get(Calendar.MONTH), d = c.get(Calendar.DAY_OF_MONTH);
            new android.app.DatePickerDialog(this, (view, year, month, dayOfMonth) ->
                    etDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year), y, m, d).show();
        });
    }

    private void chooseFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), FILE_PICKER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICKER_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            tvFileName.setText(fileUri.getLastPathSegment());
        }
    }

    private void uploadReport() {
        String title = etTitle.getText().toString().trim();
        String type = etType.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if (title.isEmpty() || type.isEmpty() || date.isEmpty() || fileUri == null) {
            Toast.makeText(this, "Please fill all fields & select file", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String fileName = System.currentTimeMillis() + "_" + title;
            File outFile = new File(reportsDir, fileName);

            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            OutputStream outputStream = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            inputStream.close();
            outputStream.close();

            // Save metadata
            ReportUtils.saveReport(this, title, type, date, fileName);
            Toast.makeText(this, "Report Saved Locally ✅", Toast.LENGTH_SHORT).show();
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save ❌", Toast.LENGTH_SHORT).show();
        }
    }
}

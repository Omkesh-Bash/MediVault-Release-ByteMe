package com.example.medivault;

import android.app.Application;
import android.util.Log;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", "dndurhixs");
            MediaManager.init(this, config);
            Log.d("CLOUDINARY", "Cloudinary initialized ✅");
        } catch (Exception e) {
            Log.e("CLOUDINARY", "Cloudinary init failed ❌", e);
        }
    }

}

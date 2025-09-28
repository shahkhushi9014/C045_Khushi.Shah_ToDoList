package com.example.c045_khushishah_todolist;

import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // IMPORTANT: call once, before any DatabaseReference usage
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

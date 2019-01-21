package com.tencent.shadow.demo.usecases.provider;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.shadow.demo.gallery.BaseActivity;
import com.tencent.shadow.demo.gallery.R;

public class TestDBContentProviderActivity extends BaseActivity {

    private static final String TAG = "ContentProviderActivity";

    private TextView mTextView;

    private Handler mHandler = new Handler();
    private ContentObserver mObserver = new ContentObserver(mHandler) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            Log.d(TAG, uri + " onChange");
            Toast.makeText(TestDBContentProviderActivity.this, uri + " onChange", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_provider_db);

        mTextView = findViewById(R.id.text);

        getContentResolver().registerContentObserver(TestProviderInfo.TestEntry.CONTENT_URI,
                false, mObserver);
    }

    public void insert(View view){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TestProviderInfo.TestEntry.COLUMN_NAME, "test");
        contentValues.put(TestProviderInfo.TestEntry._ID, System.currentTimeMillis());
        getContentResolver().insert(TestProviderInfo.TestEntry.CONTENT_URI, contentValues);

        query(view);
    }

    public void query(View view){
        Cursor cursor = getContentResolver().query(TestProviderInfo.TestEntry.CONTENT_URI, null, null, null, null);
        StringBuilder s = new StringBuilder();
        while (cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndex(TestProviderInfo.TestEntry._ID));
            String name = cursor.getString(cursor.getColumnIndex(TestProviderInfo.TestEntry.COLUMN_NAME));
            s.append("id:").append(id).append(" name:").append(name).append(" \n");
        }
        mTextView.setText(s);
        cursor.close();
    }

    public void update(View view) {
        Cursor cursor = getContentResolver().query(TestProviderInfo.TestEntry.CONTENT_URI,
                null, null, null, null);
        int count = cursor != null ? cursor.getCount() : 0;
        if (count > 0) {
            cursor.moveToFirst();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TestProviderInfo.TestEntry.COLUMN_NAME, "name " + System.currentTimeMillis());

            long id = cursor.getLong(cursor.getColumnIndex(TestProviderInfo.TestEntry._ID));
            getContentResolver().update(TestProviderInfo.TestEntry.CONTENT_URI, contentValues,
                    TestProviderInfo.TestEntry._ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
        if (cursor != null) {
            cursor.close();
        }

        query(view);
    }

    public void delete(View view) {
        Cursor cursor = getContentResolver().query(TestProviderInfo.TestEntry.CONTENT_URI,
                null, null, null, null);
        int count = cursor != null ? cursor.getCount() : 0;
        if (count > 0) {
            cursor.moveToFirst();
            long id = cursor.getLong(cursor.getColumnIndex(TestProviderInfo.TestEntry._ID));
            getContentResolver().delete(TestProviderInfo.TestEntry.CONTENT_URI,
                    TestProviderInfo.TestEntry._ID + " = ?",
                    new String[]{String.valueOf(id)});
        }
        if (cursor != null) {
            cursor.close();
        }

        query(view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(mObserver);
    }
}

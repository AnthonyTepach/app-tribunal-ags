package com.anthonytepach.app.ags;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DocumentError extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_error);

        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }
}
package com.adocker.test.components.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.adocker.test.R;

import androidx.appcompat.app.AppCompatActivity;

public class ResolverActivityThree extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolver);
        TextView textView = (TextView) findViewById(R.id.text_resolver_message);
        textView.setText("ResolverActivityThree");
    }
}

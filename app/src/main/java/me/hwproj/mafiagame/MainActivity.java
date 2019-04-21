package me.hwproj.mafiagame;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TestStringHolder h = ViewModelProviders.of(this).get(TestStringHolder.class);
        Button bAddC = findViewById(R.id.button);
        Button bReset = findViewById(R.id.button2);
        TextView text = findViewById(R.id.textView);

        bAddC.setOnClickListener(v -> h.append('c'));
        bReset.setOnClickListener(v -> h.setText(""));
        h.getData().observe(this, text::setText);
    }
}

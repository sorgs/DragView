package com.drageview.sorgs.dragview;

import android.graphics.Picture;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.drageview.sorgs.dragview.widget.DragView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DragView dragView = findViewById(R.id.dragView);
        dragView.setData(900f, 500f, 300f, 100f, 200f);

        Button btn = findViewById(R.id.btn);
        final ImageView iv = findViewById(R.id.iv);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //截取选中区域
                Picture pic = dragView.getPic();
                Log.i(TAG, "onClick: " + pic);
                Glide.with(MainActivity.this).load(pic).into(iv);

            }
        });
    }
}

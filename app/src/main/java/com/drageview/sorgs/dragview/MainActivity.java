package com.drageview.sorgs.dragview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.drageview.sorgs.dragview.widget.DragView;

public class MainActivity extends AppCompatActivity {
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final DragView dragView = findViewById(R.id.dragView);
        dragView.setData(BitmapFactory.decodeResource(getResources(), R.drawable.text));

        Button btn = findViewById(R.id.btn);
        final ImageView iv = findViewById(R.id.iv);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //截取选中区域
                mBitmap = dragView.getBitmap(MainActivity.this);
                iv.setImageBitmap(mBitmap);

            }
        });
    }
}

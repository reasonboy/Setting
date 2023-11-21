package com.jzzh.setting.display;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;

import static com.jzzh.setting.display.BitmapManager.rotateBitmap;

public class PowerOffImageDefaultActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_poi_default);

        Bitmap defSleepImage = BitmapFactory.decodeFile("/vendor/media/poweroff.png");
        ImageView iv = findViewById(R.id.def_iv);
        iv.setImageBitmap(rotateBitmap(defSleepImage,90));

        findViewById(R.id.def_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}

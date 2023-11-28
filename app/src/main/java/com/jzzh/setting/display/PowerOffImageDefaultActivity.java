package com.jzzh.setting.display;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.jzzh.setting.BaseActivity;
import com.jzzh.setting.R;
import com.jzzh.setting.display.logo.BitmapManager;

import static com.jzzh.setting.display.logo.BitmapManager.rotateBitmap;

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
                for(String logoPath : BitmapManager.POWER_OFF_IMAGE_SAVE_FILES) {
                    BitmapManager.deleteBitmap(logoPath);
                }
                Toast.makeText(PowerOffImageDefaultActivity.this,R.string.set_image_successfully,Toast.LENGTH_LONG).show();
            }
        });
    }
}

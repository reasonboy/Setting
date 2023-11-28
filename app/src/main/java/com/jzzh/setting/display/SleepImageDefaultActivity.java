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

public class SleepImageDefaultActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_si_default);

        Bitmap defSleepImage = BitmapFactory.decodeFile("/vendor/media/standby.png");
        ImageView iv = findViewById(R.id.def_iv);
        iv.setImageBitmap(rotateBitmap(defSleepImage,90));

        findViewById(R.id.def_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(String logoPath : BitmapManager.SLEEP_IMAGE_SAVE_FILES) {
                    BitmapManager.deleteBitmap(logoPath);
                }
                Toast.makeText(SleepImageDefaultActivity.this,R.string.set_image_successfully,Toast.LENGTH_LONG).show();
            }
        });
    }

}

package com.housedevelop.housedevelop;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.net.URL;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class FullScreenViewActivity extends ActionBarActivity {
    String filename;
    TouchImageView img;
    Bitmap bitmap;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityfullscreenview);

        Bundle bundle = getIntent().getParcelableExtra("bundle");
        filename = bundle.getString("filename");

        img = new TouchImageView(this);
       // img.setImageResource(R.drawable.ab_texture_tile_mystyle);
        img.setMaxZoom(4f);
        setContentView(img);

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    final String url=AppConfig.site+"uploads/" + filename;
                    bitmap = BitmapFactory.decodeStream(new URL(url).openStream());
                    img.post(new Runnable() {
                        @Override
                        public void run() {
                            img.setImageBitmap(bitmap);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };
        t.start();
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.fullscreenimage_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.full_action_rotate) {
            Matrix matrix = new Matrix();
            matrix.postRotate(-90);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);

            img.setImageBitmap(bitmap);
        }
        return super.onOptionsItemSelected(item);
    }
}

package com.framgia.lupx.bitmapprocessing;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private Button btnOpenPhoto;
    private ImageView imgPhoto;

    private ImageView imgResult;
    private Button btnBW;
    private Button btnRed;

    private Bitmap originBitmap;

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnOpen:
                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickIntent.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

                    startActivityForResult(chooserIntent, PICK_IMAGE);
                    break;
                case R.id.btnBW:
                    Bitmap bw = performBW(originBitmap);
                    imgResult.setImageBitmap(bw);
                    break;
                case R.id.btnRed:
                    Bitmap red = performRed(originBitmap);
                    imgResult.setImageBitmap(red);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_IMAGE:
                    if (data == null) {
                        //Snackbar.make(,"Pick photo error",Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(inputStream, null, options);
                        options.inSampleSize = 2;
                        options.inJustDecodeBounds = false;
                        inputStream.close();
                        inputStream = getContentResolver().openInputStream(data.getData());
                        originBitmap = BitmapFactory.decodeStream(inputStream, null, options);
                        imgPhoto.setImageBitmap(originBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    private void initViews() {
        btnOpenPhoto = (Button) findViewById(R.id.btnOpen);
        btnOpenPhoto.setOnClickListener(clickListener);
        imgPhoto = (ImageView) findViewById(R.id.imgPhoto);
        imgResult = (ImageView) findViewById(R.id.imgResult);
        btnBW = (Button) findViewById(R.id.btnBW);
        btnBW.setOnClickListener(clickListener);
        btnRed = (Button) findViewById(R.id.btnRed);
        btnRed.setOnClickListener(clickListener);
    }

    private Bitmap performBW(Bitmap inp) {
        Bitmap bmOut = Bitmap.createBitmap(inp.getWidth(), inp.getHeight(),
                inp.getConfig());
        int A, R, G, B;
        int w = inp.getWidth();
        int h = inp.getHeight();
        int[] colors = new int[w * h];
        inp.getPixels(colors, 0, w, 0, 0, w, h);
        int i = 0;
        int j = 0;
        int pos;
        int val;
        for (i = 0; i < h; i++) {
            for (j = 0; j < w; j++) {
                pos = i * w + j;
                A = (colors[pos] >> 24) & 0xFF;
                R = (colors[pos] >> 16) & 0xFF;
                G = (colors[pos] >> 8) & 0xFF;
                B = colors[pos] & 0xFF;
                //Thuật toán xử lý grayscale
                R = G = B = (int) (0.299 * R + 0.587 * G + 0.114 * B);
                colors[pos] = Color.argb(A, R, G, B);
            }
        }
        bmOut.setPixels(colors, 0, w, 0, 0, w, h);
        return bmOut;
    }

    private Bitmap performRed(Bitmap inp) {
        Bitmap bmOut = Bitmap.createBitmap(inp.getWidth(), inp.getHeight(),
                inp.getConfig());
        int A, R, G, B;
        int w = inp.getWidth();
        int h = inp.getHeight();
        int[] colors = new int[w * h];
        inp.getPixels(colors, 0, w, 0, 0, w, h);
        int i = 0;
        int j = 0;
        int pos;
        int val;
        for (i = 0; i < h; i++) {
            for (j = 0; j < w; j++) {
                pos = i * w + j;
                A = (colors[pos] >> 24) & 0xFF;
                R = (colors[pos] >> 16) & 0xFF;
                G = (colors[pos] >> 8) & 0xFF;
                B = colors[pos] & 0xFF;
                colors[pos] = Color.argb(A, R, 0, B);
            }
        }
        bmOut.setPixels(colors, 0, w, 0, 0, w, h);
        return bmOut;
    }
}

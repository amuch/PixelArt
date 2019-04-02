package com.example.pixelart;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class DrawingActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    private final int WIDTH = 32;
    private final int HEIGHT = 32;

    private final int PIXEL_BACKGROUND = Color.argb(0, 127, 127, 127);
    private int currentColor;

    private Bitmap mainBitmap;
    private ImageView mainImageView, currentColorImageView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        bindButtons();
        mainImageView = findViewById(R.id.mainImageView);
        mainImageView.setOnTouchListener(this);



        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        mainBitmap = Bitmap.createBitmap(WIDTH, HEIGHT, config);

        clearBitmap();

        currentColorImageView = findViewById(R.id.currentColorImageView);
        Drawable drawable = getResources().getDrawable(R.drawable.selected_palette);
        //drawable.setColorFilter(currentColor, PorterDuff.Mode.ADD);
        currentColorImageView.setBackgroundDrawable(drawable);
        currentColorImageView.setMinimumWidth(160);
        currentColorImageView.setMinimumHeight(160);

        setPalette();
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()) {
            case R.id.addFrameButton:

                break;

            case R.id.addColorButton:

                break;

            case R.id.newPaletteButton:

                break;

            case R.id.fillBitmapButton:
                fillBitmap(mainBitmap, currentColor);
                mainImageView.setImageBitmap(mainBitmap);
                break;

            case R.id.clearBitmapButton:
                clearBitmap();
                break;

            default:
                break;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch(event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int x = touchToPixel(WIDTH, v.getWidth(), event.getX());
                int y = touchToPixel(HEIGHT, v.getHeight(), event.getY());
                colorPixel(x, y);
                break;
        }
        return true;
    }

    private void colorPixel(int x, int y) {

        if(y < mainBitmap.getHeight() && x < mainBitmap.getWidth()) {

            int pixel = mainBitmap.getPixel(x, y);

            if (pixel != currentColor) {
                mainBitmap.setPixel(x, y, currentColor);
                mainImageView.setImageBitmap(mainBitmap);
            }
        }

        //Toast toast = Toast.makeText(this, "X: " + x + " Y: " + y, Toast.LENGTH_LONG);
        //toast.show();
    }

    private void fillBitmap(Bitmap bitmap, int color) {
        for(int i = 0; i < bitmap.getWidth(); i++) {
            for(int j = 0; j < bitmap.getHeight(); j++) {
                bitmap.setPixel(i, j, color);
            }
        }
    }

    /**********************************************************************************************
     *
     * Helper Methods
     *
     *********************************************************************************************/

    private void bindButtons() {

        Button addFrameButton = findViewById(R.id.addFrameButton);
        addFrameButton.setOnClickListener(this);

        Button addColorButton = findViewById(R.id.addColorButton);
        addColorButton.setOnClickListener(this);

        Button newPaletteButton = findViewById(R.id.newPaletteButton);
        newPaletteButton.setOnClickListener(this);

        Button fillBitmapButton = findViewById(R.id.fillBitmapButton);
        fillBitmapButton.setOnClickListener(this);

        Button clearBitmapButton = findViewById(R.id.clearBitmapButton);
        clearBitmapButton.setOnClickListener(this);
    }

    private void clearBitmap() {
        for(int i = 0; i < WIDTH; i++) {
            for(int j = 0; j < HEIGHT; j++) {
                mainBitmap.setPixel(i, j, PIXEL_BACKGROUND);
            }
        }
        mainImageView.setImageBitmap(mainBitmap);
    }

    private void setPalette() {

        final int[] colorArray = getResources().getIntArray(R.array.default_palette);

        currentColorImageView.setBackgroundColor(colorArray[0]);

        LinearLayout linearLayout = findViewById(R.id.paletteLinearLayout);
        linearLayout.removeAllViews();

        for(int i = 0; i < colorArray.length; i++) {
            final int index = i;

            ImageButton imageButton = new ImageButton(this);
            imageButton.setLayoutParams(paletteButtonParameters());
            Drawable drawable = getResources().getDrawable(R.drawable.selected_palette);
            drawable.setColorFilter(colorArray[i], PorterDuff.Mode.ADD);
            imageButton.setBackgroundDrawable(drawable);
            //imageButton.setBackgroundColor(colorArray[i]);
            //imageButton.setMinimumHeight(120);
            //imageButton.setMinimumWidth(120);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentColor = colorArray[index];
                    Drawable drawable = getResources().getDrawable(R.drawable.selected_palette);
                    drawable.setColorFilter(currentColor, PorterDuff.Mode.ADD);
                    currentColorImageView.setBackgroundDrawable(drawable);
                    //currentColorImageView.setBackgroundColor(currentColor);
                }
            });
            linearLayout.addView(imageButton);
        }

        // Add clear color button
        ImageButton imageButton = new ImageButton(this);
        imageButton.setLayoutParams(paletteButtonParameters());

        //Drawable drawable = getResources().getDrawable(R.drawable.selected_palette);
        //drawable.setColorFilter(new PorterDuffColorFilter(PIXEL_BACKGROUND, PorterDuff.Mode.MULTIPLY));
        imageButton.setBackgroundResource(R.drawable.selected_palette);
        //imageButton.setBackgroundColor(PIXEL_BACKGROUND);

        //imageButton.setMinimumHeight(120);
        //imageButton.setMinimumWidth(120);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentColor = PIXEL_BACKGROUND;
                currentColorImageView.setBackgroundColor(currentColor);
            }
        });
        linearLayout.addView(imageButton);
    }

    // Under no circumstance, return an int less than 0 or greater than maxBitmapDimension - 1.
    private int touchToPixel(int maxBitmapDimension, int viewMaximum, float touchDimension) {

        int pixel = 0;

        int dimensionalUnit = viewMaximum / maxBitmapDimension;

        if(touchDimension > 0) {
            pixel = (int) touchDimension / dimensionalUnit;
        }

        if(pixel > maxBitmapDimension) {
            pixel = maxBitmapDimension - 1;
        }

        return pixel;
    }

    private LinearLayout.LayoutParams paletteButtonParameters() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.setMargins(8, 8, 8, 8);
        params.width = 130;
        params.height = 130;

        return params;
    }

}

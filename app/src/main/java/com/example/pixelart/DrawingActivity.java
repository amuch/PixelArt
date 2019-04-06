package com.example.pixelart;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class DrawingActivity extends Activity implements View.OnClickListener, View.OnTouchListener {

    private final int WIDTH = 32;
    private final int HEIGHT = 32;
    private final int COLOR_BUTTON_WIDTH = 120;
    private final int COLOR_BUTTON_HEIGHT = 120;
    private final int CURRENT_COLOR_IMAGE_WIDTH = 160;
    private final int CURRENT_COLOR_IMAGE_HEIGHT = 160;

    private final int PIXEL_BACKGROUND = Color.argb(0, 127, 127, 127);
    private ArrayList<Integer> colorPalette;
    private int currentColor;

    private Bitmap mainBitmap;
    private ImageView mainImageView, currentColorImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        bindButtons();
        bindMainImageView();

        colorPalette = setColorArrayList(getResources().getIntArray(R.array.default_palette));

        bindCurrentColorImageView();

        setPalette(colorPalette);
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
        // This check is currently unnecessary, but it might still come in handy eg. brush sizes.
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

    private void bindMainImageView() {
        mainImageView = findViewById(R.id.mainImageView);
        mainImageView.setOnTouchListener(this);

        createMainBitmap();
    }

    private void createMainBitmap() {
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        mainBitmap = Bitmap.createBitmap(WIDTH, HEIGHT, config);

        clearBitmap();
    }

    private void clearBitmap() {
        fillBitmap(mainBitmap, PIXEL_BACKGROUND);
        mainImageView.setImageBitmap(mainBitmap);
    }

    private void bindCurrentColorImageView() {
        currentColorImageView = findViewById(R.id.currentColorImageView);
        currentColorImageView.setLayoutParams(colorImageParameters(CURRENT_COLOR_IMAGE_WIDTH, CURRENT_COLOR_IMAGE_HEIGHT));
        Drawable drawable = getImageDrawable(currentColor);
        currentColorImageView.setBackgroundDrawable(drawable);
    }

    private ArrayList<Integer> setColorArrayList(int[] colors) {
        ArrayList<Integer> colorsArrayList = new ArrayList<>();

        for(int i = 0; i < colors.length; i++) {
            colorsArrayList.add(colors[i]);
        }
        // Add the clear color to the end of the colors list.
        colorsArrayList.add(PIXEL_BACKGROUND);

        currentColor = colorsArrayList.get(0);

        return colorsArrayList;
    }

    private void setPalette(final ArrayList<Integer> colorList) {
        if(colorList.size() > 0) {

            LinearLayout linearLayout = findViewById(R.id.paletteLinearLayout);
            linearLayout.removeAllViews();

            for(int i = 0; i < colorList.size(); i++) {
                final int index = i;

                ImageButton imageButton = new ImageButton(this);
                imageButton.setLayoutParams(colorImageParameters(COLOR_BUTTON_WIDTH, COLOR_BUTTON_HEIGHT));
                Drawable drawable = getImageDrawable(colorList.get(i));
                imageButton.setBackgroundDrawable(drawable);

                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentColor = colorList.get(index);
                        Drawable drawable = getImageDrawable(currentColor);
                        currentColorImageView.setBackgroundDrawable(drawable);
                    }
                });
                linearLayout.addView(imageButton);
            }
        }
    }

    // Under no circumstance, return an int less than 0 or greater than maxBitmapDimension - 1.
    private int touchToPixel(int maxBitmapDimension, int viewMaximum, float touchDimension) {
        float dimensionalUnit = (float) viewMaximum / maxBitmapDimension;
        int pixel = touchDimension > 0 ? (int) (touchDimension / dimensionalUnit) : 0;
        pixel = pixel > maxBitmapDimension ? maxBitmapDimension - 1 : pixel;

        return pixel;
    }

    private Drawable getImageDrawable(int color) {
        Drawable drawable = getResources().getDrawable(R.drawable.palette_drawable);

        // The color filter doesn't work with the clear color.
        if(color != PIXEL_BACKGROUND) {
            drawable.setColorFilter(color, PorterDuff.Mode.ADD);
        }

        return drawable;
    }

    private LinearLayout.LayoutParams colorImageParameters(int imageWidth, int imageHeight) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        params.setMargins(8, 8, 8, 8);
        params.width = imageWidth;
        params.height = imageHeight;

        return params;
    }

}

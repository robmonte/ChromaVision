package cs371m.chromavision;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;


public class LoadResultActivity extends AppCompatActivity {

    private static final String TAG = "Result Activity";

    private TextView mTextView;
    private TextView mColorTextView;
    private ProgressBar mProgressBar;
    private Bitmap bitmap;
    private String resultPicture;
    private String fileName;
    private Integer saved;
    private EditText input;
    private FileInputStream fis;
    private ResultActivity mResult;
    private Uri file;

    private enum COLORS { DARK_RED, RED, LIGHT_RED,
        DARK_GREEN, GREEN, LIGHT_GREEN,
        DARK_BLUE, BLUE, CYAN,
        BROWN, ORANGE, LIGHT_ORANGE,
        DARK_YELLOW, YELLOW, LIGHT_YELLOW,
        DARK_PURPLE, PURPLE, PINK,
        BLACK, GREY, WHITE,
        DARK_GREY, LIGHT_GREY,
        DARKER_GREEN, LIGHTER_GREEN,
        ERROR }

    private static final int[] COLOR_LIST = { Color.rgb(0x80, 0x00, 0x00), Color.rgb(0xFF, 0x00, 0x00), Color.rgb(0xFF, 0x80, 0x80),
            Color.rgb(0x00, 0x80, 0x00), Color.rgb(0x00, 0xC0, 0x00), Color.rgb(0x00, 0xFF, 0x00),
            Color.rgb(0x00, 0x00, 0x80), Color.rgb(0x00, 0x00, 0xFF), Color.rgb(0x00, 0xFF, 0xFF),
            Color.rgb(0x80, 0x40, 0x00), Color.rgb(0xFF, 0x80, 0x00), Color.rgb(0xFF, 0xC0, 0x80),
            Color.rgb(0x80, 0x80, 0x00), Color.rgb(0xFF, 0xFF, 0x00), Color.rgb(0xFF, 0xFF, 0x80),
            Color.rgb(0x40, 0x00, 0x40), Color.rgb(0x80, 0x00, 0x80), Color.rgb(0xFF, 0x00, 0xFF),
            Color.rgb(0x00, 0x00, 0x00), Color.rgb(0x80, 0x80, 0x80), Color.rgb(0xFF, 0xFF, 0xFF),
            Color.rgb(0x40, 0x40, 0x40), Color.rgb(0xC0, 0xC0, 0xC0),
            Color.rgb(0x00, 0x40, 0x00), Color.rgb(0x80, 0xFF, 0x80),
            Color.rgb(0x01, 0x01, 0x01) };

    private int colorCount[] = new int[COLOR_LIST.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_result);
        mResult = new ResultActivity();

        mTextView = (TextView)findViewById(R.id.colorDataView);

        readFile();

        final ImageView mImageView = (ImageView) findViewById(R.id.resultImage);


        if (mImageView != null) {
            mImageView.setDrawingCacheEnabled(true);
        }

        if (mImageView != null) {
            mImageView.setOnTouchListener( new View.OnTouchListener(){
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent){

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE){

                        int x = (int)motionEvent.getX();
                        int y = (int)motionEvent.getY();

                        bitmap = mImageView.getDrawingCache();
                        if(y < 0 || x < 0 || y > bitmap.getHeight() || x > bitmap.getWidth()) {
                            // Touched outside of the image boundaries
                        }
                        else {
                            int pixel = bitmap.getPixel(x, y);
                            int redValue = Color.red(pixel);
                            int blueValue = Color.blue(pixel);
                            int greenValue = Color.green(pixel);

                            TextView clickPixelBackground = (TextView) findViewById(R.id.clickPixel);
                            TextView clickPixelLocation = (TextView) findViewById(R.id.clickPixelText);
                            LinearLayout border = (LinearLayout) findViewById(R.id.clickPixelBorder);
                            if (border != null) {
                                border.setBackgroundColor(Color.BLACK);
                            }
                            if (clickPixelBackground != null) {
                                clickPixelBackground.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
                            }

                            float [] Hsb = new float[3];
                            Color.RGBToHSV(redValue,greenValue,blueValue, Hsb);
                            String loc = "(" + x + ", " + y + ") is " + getTouchedColor(Hsb).toString();
                            if (clickPixelLocation != null) {
                                clickPixelLocation.setText(loc);
                            }
                        }
                    }
                    return true;
                }

            });
        }


    }

    private COLORS getTouchedColor(float[] hsb) {
        float deg = hsb[0];

        if (hsb[2] < 0.1 ) {
            // Black
            colorCount[COLORS.valueOf("BLACK").ordinal()]++;
            return COLORS.valueOf("BLACK");
        }
        else if ((hsb[1] < 0.20 && hsb[2] >= 0.80) || (deg >= 0.3 && deg < 0.6 && hsb[1] < 0.3 && hsb[2] >= 0.7)) {
            // White
            colorCount[COLORS.valueOf("WHITE").ordinal()]++;
            return COLORS.valueOf("WHITE");
        }
        else if ((hsb[1] < 0.15 && hsb[2] >= 0.1 && hsb[2] < 0.75) || (hsb[1] < 0.4 && hsb[2] < 0.2) || ((deg < 64 || deg >= 180) && hsb[1] < 0.15)) {
            // Grey
            colorCount[COLORS.valueOf("GREY").ordinal()]++;
            return COLORS.valueOf("GREY");
        }
        else {
            //Log.d(TAG, deg);
            if (deg >= 335 || deg <  11) {
                if (deg < 350 && deg > 11 && hsb[1] < 0.65 && hsb[2] >= 0.5) {
                    // Pink
                    colorCount[COLORS.valueOf("PINK").ordinal()]++;
                    return COLORS.valueOf("PINK");
                }
                else /*if (hsb[1] >= 0.8 || (deg >= 0 && hsb[1] >= 0.5))*/ {
                    // Red
                    colorCount[COLORS.valueOf("RED").ordinal()]++;
                    return COLORS.valueOf("RED");
                }
//                else {
//                    // Dark Red
//                    colorCount[COLORS.valueOf("DARK_RED").ordinal()]++;
//                    return COLORS.valueOf("DARK_RED");
//                }
            }
            else if (deg >= 11 && deg < 45) {
                if ((hsb[1] >= 0.8 || hsb[1] > 0.5 && hsb[2] > 0.7 || hsb[2] > 0.85 /*&& hsb[2] >= 0.60*/)) {
                    // Orange
                    colorCount[COLORS.valueOf("ORANGE").ordinal()]++;
                    return COLORS.valueOf("ORANGE");
                }
//                else if ((hsb[2] >= 0.75)) {
//                    // Light Orange
//                    colorCount[COLORS.valueOf("LIGHT_ORANGE").ordinal()]++;
//                    return COLORS.valueOf("LIGHT_ORANGE");
//                }
                else {
                    if (hsb[1] < 0.4 && hsb[2] < 0.9) {
                        // Grey
                        colorCount[COLORS.valueOf("GREY").ordinal()]++;
                        return COLORS.valueOf("GREY");
                    }
                    // Brown
                    colorCount[COLORS.valueOf("BROWN").ordinal()]++;
                    return COLORS.valueOf("BROWN");
                }

            }
            else if (deg >=  45 && deg <  70) {
                // Yellow
                if (deg > 60) {
                    // Blue
                    if (hsb[1] < 0.25 && hsb[2] < 0.35) {
                        colorCount[COLORS.valueOf("BLUE").ordinal()]++;
                        return COLORS.valueOf("BLUE");
                    }
                    else if (hsb[1] < 0.3 && hsb[2] < 0.85) {
                        colorCount[COLORS.valueOf("GREY").ordinal()]++;
                        return COLORS.valueOf("GREY");
                    }
                    else {
                        // Yellow
                        colorCount[COLORS.valueOf("YELLOW").ordinal()]++;
                        return COLORS.valueOf("YELLOW");
                    }
                }
                else {
                    colorCount[COLORS.valueOf("YELLOW").ordinal()]++;
                    return COLORS.valueOf("YELLOW");
                }
            }
            else if (deg >=  70 && deg < 178) {
                if (deg < 170 || (hsb[1] >= 0.5 && hsb[2] > 0.4)) {
                    // Green
                    colorCount[COLORS.valueOf("GREEN").ordinal()]++;
                    return COLORS.valueOf("GREEN");
                }
                else {
                    // Blue
                    colorCount[COLORS.valueOf("BLUE").ordinal()]++;
                    return COLORS.valueOf("BLUE");
                }
            }
            else if (deg >= 178 && deg < 255) {
                if (hsb[2] >= 0.5) {
                    // Blue
                    colorCount[COLORS.valueOf("BLUE").ordinal()]++;
                    return COLORS.valueOf("BLUE");
                }
                else if (deg >= 245){
                    // Purple
                    colorCount[COLORS.valueOf("PURPLE").ordinal()]++;
                    return COLORS.valueOf("PURPLE");
                }
                else {
                    // Blue
                    colorCount[COLORS.valueOf("BLUE").ordinal()]++;
                    return COLORS.valueOf("BLUE");
                }
            }
            else if (deg >= 255 && deg < 310) {
                if (hsb[2] < 0.85) {
                    // Purple
                    colorCount[COLORS.valueOf("PURPLE").ordinal()]++;
                    return COLORS.valueOf("PURPLE");
                }
                else {
                    // Pink
                    colorCount[COLORS.valueOf("PINK").ordinal()]++;
                    return COLORS.valueOf("PINK");
                }
            }
            else if (deg >= 310 && deg < 335) {
                if ((hsb[1] < 0.5 && hsb[2] >= 0.75) || (hsb[1] >= 0.70 && hsb[2] >= 0.75) || (hsb[1] >= 0.5 && hsb[2] >= 0.65)) {
                    // Pink
                    colorCount[COLORS.valueOf("PINK").ordinal()]++;
                    return COLORS.valueOf("PINK");
                }
                else {
                    // Purple
                    colorCount[COLORS.valueOf("PURPLE").ordinal()]++;
                    return COLORS.valueOf("PURPLE");
                }
            }
            else {
                colorCount[COLORS.valueOf("ERROR").ordinal()]++;
                System.out.printf("Error color is %f, %f, %f\n", deg, hsb[1], hsb[2]);
                return COLORS.valueOf("ERROR");
            }
        }
    }

    private void readFile() {
        try {

            Uri uri = Uri.fromFile(new File(getFilesDir().toString() + "/" + FileListActivity.itemValue));
            file = uri;

            ImageView imageView = (ImageView) findViewById(R.id.resultImage);

            try {
                if (imageView != null) {
                    imageView.setImageURI(uri);
                }
            }
            catch (NullPointerException e){
                e.printStackTrace();

            }

            fis = openFileInput(FileListActivity.itemValue + ".chroma");
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(fis));

            String line = reader.readLine();
            StringBuilder output = new StringBuilder();

            while (line != null) {
                output.append(line);
                output.append("\n");
                line = reader.readLine();
            }

            String str = output.toString();
            mTextView.setText(str);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        saved = 0;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_load_page, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ScrollView resultString = (ScrollView) findViewById(R.id.resultString);
        if (item.getItemId() == R.id.load)
            deleteFile(resultString);
        return true;
    }

    private void deleteFile(View v) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Are you sure you want to delete this?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        Toast.makeText(getApplicationContext(),
                                "Deleted!",Toast.LENGTH_LONG).show();
                        //fileName = input.getText().toString();

                        File delete = new File(file.getPath());
                        File deleteData = new File(file.getPath() + ".chroma");
                        delete.delete();
                        deleteData.delete();
                        setResult(1);
                        finish();
                    }
                })
                .setNegativeButton("Nope", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        // User canceled the dialog
                        Toast.makeText(getApplicationContext(),
                                "Canceled",Toast.LENGTH_LONG).show();
                    }
                });

        builder.show();
    }

}

package cs371m.chromavision;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "Result Activity";

    private TextView mTextView;
    private ProgressBar mProgressBar;
    private GenerateColorDataAsync mRunPicture;
    private Bitmap bitmap;
    private String resultPicture;
    private String fileName;
    private Integer saved;
    private EditText input;
    private FileInputStream fis;
    private Uri pictureUri;
    private boolean doneAsync;


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
        doneAsync = false;
        Intent intent = getIntent();
        pictureUri = intent.getParcelableExtra("pictureUri");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        if (mProgressBar != null) {
            mProgressBar.setScaleY(5f);
            mProgressBar.setScaleX(0.75f);
        }

       final ImageView mImageView = (ImageView) findViewById(R.id.resultImage);

        if (mImageView != null) {
            mImageView.setDrawingCacheEnabled(true);
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
        if (mImageView != null) {
            mImageView.setImageURI(pictureUri);
        }

        mTextView = (TextView)findViewById(R.id.colorDataView);

        mRunPicture = new GenerateColorDataAsync();
        // generateColorData(picture);
        mRunPicture.execute(pictureUri);

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
            //System.out.println(deg);
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


    @Override
    public void onBackPressed()
    {
        File delete = new File(pictureUri.getPath());

        if (delete.exists())
            delete.delete();

        if (mRunPicture != null)
            mRunPicture.cancel(true);

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        saved = 0;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_result_page, menu);

        if (doneAsync)
            menu.getItem(0).setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        ScrollView resultString = (ScrollView) findViewById(R.id.resultString);
        if (item.getItemId() == R.id.save && saved == 0)
            nameFile(resultString);
        return true;
    }

    protected void nameFile(View v) {

//
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Please set a file name:")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        Toast.makeText(getApplicationContext(),
                                "Saved!",Toast.LENGTH_LONG).show();
                        input.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        fileName = input.getText().toString();
                        int length = fileName.length();

                        while (fileName.charAt(fileName.length()-1) == ' ')
                            fileName = fileName.substring(0, fileName.length()-1);

                        if(fileName.length() > 4) {
                            String end = fileName.substring(length - 4);

                            if (!end.equals(".jpg"))
                                fileName += ".jpg";
                        }
                        else
                            fileName += ".jpg";
                        saveFile();
                    }
                })
                .setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        // User canceled the dialog
                        Toast.makeText(getApplicationContext(),
                                "Canceled!",Toast.LENGTH_LONG).show();
                    }
                });

        // Create the AlertDialog object and return it
        AlertDialog fname = builder.create();
        input = new EditText(this);
        fname.setView(input);
        fname.show();

    }



    private void saveFile() {
        if (saved == 0) {
            try {

                InputStream load = null;

                try {
                    load = getContentResolver().openInputStream(pictureUri);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                Bitmap save = BitmapFactory.decodeStream(load);

                OutputStream fos = null;

                File dirFile = getFilesDir();

                File out = new File(dirFile, fileName);

                try {
                    fos = new FileOutputStream(out);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                save.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                try {
                    fos.flush();
                    fos.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }



                FileOutputStream fosText
                        = openFileOutput(fileName + ".chroma", MODE_PRIVATE);

                PrintStream writer = new PrintStream(fosText);
                writer.println(resultPicture);

                writer.close();
                fis = openFileInput(fileName);

                saved = 1;

                File delete = new File(pictureUri.getPath());

                delete.delete();

            }
            catch (FileNotFoundException e) {
                Log.d(TAG, "Exception trying to open file: " + e);
            }

        }
    }

    public void readFile() {
        FileListActivity mFile = new FileListActivity();

        try {
            Uri uri = Uri.fromFile(new File(mFile.itemValue));
            ImageView imageView = (ImageView) findViewById(R.id.resultImage);

            try {
                if (imageView != null) {
                    imageView.setImageURI(uri);
                }
            }
            catch (NullPointerException e){
                e.printStackTrace();

            }

            fis = openFileInput(mFile.itemValue + ".chroma");
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(fis));

            String line = reader.readLine();
            StringBuilder output = new StringBuilder();

            while (line != null) {

                output.append(line);
                output.append("\n");
                line = reader.readLine();
            }
            mTextView.setText(output);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    class GenerateColorDataAsync extends AsyncTask<Uri, Integer, StringBuilder> {


        @Override
        protected StringBuilder doInBackground(Uri... params) {

            Intent intent = getIntent();

            int width = intent.getIntExtra("width", 1);
            int height = intent.getIntExtra("height", 1);

            InputStream cameraInput = null;

            try {
                cameraInput = getContentResolver().openInputStream(params[0]);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Bitmap image = BitmapFactory.decodeStream(cameraInput);
            String[][] colors = new String[image.getHeight()][image.getWidth()];
            determineColors(image, colors);

            double imageSize = width * height;
            String[] outputArray = new String[COLOR_LIST.length];

            DecimalFormat df = new DecimalFormat("#.##");

            for (int i=0; i<outputArray.length; i++) {
                double percent = (colorCount[i] / imageSize) * 100;

                if (percent != 0 && !df.format(percent).equals("0") && !df.format(new BigDecimal(percent).setScale(2, RoundingMode.HALF_UP).doubleValue()).equals("0")) {
                    String s = COLORS.values()[i] + ":";
                    if (percent < 10.0) {
                        while (s.length() < 9)
                            s += " ";
                    }
                    else {
                        while (s.length() < 8)
                            s += " ";
                    }
                    outputArray[i] = s + df.format(new BigDecimal(percent).setScale(2, RoundingMode.HALF_UP).doubleValue()) + "% ";
                }
            }

            StringBuilder output = new StringBuilder();
            for (String s: outputArray) {
                if (s != null) {
                    output.append(s);
                    output.append("\n");
                }
            }


            resultPicture = output.toString();
            return output;


        }

        @Override
        protected void onPostExecute(StringBuilder output) {
            super.onPostExecute(output);
            mProgressBar.setVisibility(View.GONE);
            mTextView.setTypeface(Typeface.MONOSPACE);
            mTextView.setText(output);

            doneAsync = true;
            invalidateOptionsMenu();
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


            if (values.length == 2) {
                double row = values[0];
                double progress = row / values[1];
                int update = (int)(progress * 100);
                mProgressBar.setProgress(update);
            }
            else
                mProgressBar.setProgress(0);

        }

        public File getAlbumStorageDir(String albumName) {
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), albumName);
            if (!file.mkdirs()) {
                Log.e(TAG, "Directory not created");
            }
            return file;
        }

        private void determineColors(Bitmap image, String[][] colors) {

            int width = image.getWidth();
            int height = image.getHeight();
            int[] pixels = new int[width*height];

            image.getPixels(pixels, 0, width, 0, 0, width, height);

            for (int pixel=0, row=0, col=0; pixel<pixels.length; pixel++) {
                int c = pixels[pixel];

                float hsv[] = new float[3];
                Color.RGBToHSV(Color.red(c), Color.green(c), Color.blue(c), hsv);
                colors[row][col] = determineColor(hsv).toString();

                col++;
                if (col == width) {
                    publishProgress(row, height);

                    col = 0;
                    row++;
                }
            }
        }

        private double distance(int c, int r, int g, int b) {
            double redDiff = Math.pow(Color.red(c) - r, 2);
            double greenDiff = Math.pow(Color.green(c) - g, 2);
            double blueDiff = Math.pow(Color.blue(c) - b, 2);

            return Math.sqrt(redDiff + greenDiff + blueDiff);
        }

        /* DON'T FORGET!!
        *  ANDROID HUE VALUE IS ALREADY 0-360!!!
        *  DON'T MULTIPLY BY 360!!
        */
        private COLORS determineColor(float[] hsb) {
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
                //System.out.println(deg);
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


            //return null;

        }

        public Bitmap bitmapFromArray(int[][] pixels2d){
            int width = pixels2d.length;
            int height = pixels2d[0].length;
            int[] pixels = new int[width * height];
            int pixelsIndex = 0;
            for (int i = 0; i < width; i++)
            {
                for (int j = 0; j < height; j++)
                {
                    pixels[pixelsIndex] = pixels2d[i][j];
                    pixelsIndex ++;
                }
            }
            return Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888);
        }


    }


}

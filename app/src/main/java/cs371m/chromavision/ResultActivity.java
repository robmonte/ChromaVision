package cs371m.chromavision;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

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
    private MainMenuActivity mMian;
    public Uri pictureUri;

//    private enum COLORS { BLACK, VERY_DARK_RED, DARK_RED, MEDIUM_RED, BRIGHT_RED, PALE_RED, LIGHT_RED, VERY_LIGHT_RED, WHITE }
//
//    private static final int[] COLOR_LIST = { Color.rgb(0x00, 0x00, 0x00), Color.rgb(0x40, 0x00, 0x00), Color.rgb(0x80, 0x00, 0x00),
//            Color.rgb(0xC0, 0x00, 0x00), Color.rgb(0xFF, 0x00, 0x00), Color.rgb(0xFF, 0xC0, 0x00),
//            Color.rgb(0xFF, 0x80, 0x80), Color.rgb(0xFF, 0xC0, 0xC0), Color.rgb(0xFF, 0xFF, 0xFF) };


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
        System.out.println("Getting the cropped picture from " + pictureUri);
        mImageView.setDrawingCacheEnabled(true);
        mImageView.setOnTouchListener( new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE){

                    int x = (int)motionEvent.getX();
                    int y = (int)motionEvent.getY();

                    bitmap = mImageView.getDrawingCache();
                    if(y < 0 || x < 0 || y > bitmap.getHeight() || x > bitmap.getWidth()) {



                    }
                    else {
                        int pixel = bitmap.getPixel(x, y);
                        int redValue = Color.red(pixel);
                        int blueValue = Color.blue(pixel);
                        int greenValue = Color.green(pixel);
                        //System.out.println();

                        TextView clickPixelBackground = (TextView) findViewById(R.id.clickPixel);
                        TextView clickPixelLocation = (TextView) findViewById(R.id.clickPixelText);
                        LinearLayout border = (LinearLayout) findViewById(R.id.clickPixelBorder);
                        border.setBackgroundColor(Color.BLACK);
                        clickPixelBackground.setBackgroundColor(Color.rgb(redValue, greenValue, blueValue));
                        System.out.println(x + " , " + y);
                        System.out.println("red =" + redValue + "blue = " + blueValue + "green = " + greenValue);
                        //String loc = "(" + x + ", " + y + "): ";
                       // clickPixelLocation.setText(loc);
                        float [] Hsb = new float[3];
                        Color.RGBToHSV(redValue,greenValue,blueValue, Hsb);
                        String loc = "(" + x + ", " + y + ") is: " + getTouchedColor(Hsb).toString();
                        clickPixelLocation.setText(loc);

                    }
                }
                return true;
            }

        });
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

        if (hsb[2] < 0.1) {
            // Black
            colorCount[COLORS.valueOf("BLACK").ordinal()]++;
            return COLORS.valueOf("BLACK");
        } else if ((hsb[1] < 0.20 && hsb[2] >= 0.80) || (deg >= 0.3 && deg < 0.6 && hsb[1] < 0.3 && hsb[2] >= 0.7)) {
            // White
            colorCount[COLORS.valueOf("WHITE").ordinal()]++;
            return COLORS.valueOf("WHITE");
        } else if ((hsb[1] < 0.15 && hsb[2] >= 0.1 && hsb[2] < 0.66) || ((deg < 64 || deg >= 180) && hsb[1] < 0.15)) {
            // Grey
            colorCount[COLORS.valueOf("GREY").ordinal()]++;
            return COLORS.valueOf("GREY");
        } else {
            //System.out.println(deg);
            if (deg >= 335 || deg < 11) {
                if (deg < 350 && deg > 11 && hsb[1] < 0.65 && hsb[2] >= 0.5) {
                    // Pink
                    colorCount[COLORS.valueOf("PINK").ordinal()]++;
                    return COLORS.valueOf("PINK");
                } else if (hsb[1] >= 0.8 || (deg >= 0 && hsb[1] >= 0.5)) {
                    // Red
                    colorCount[COLORS.valueOf("RED").ordinal()]++;
                    return COLORS.valueOf("RED");
                } else {
                    // Dark Red
                    colorCount[COLORS.valueOf("DARK_RED").ordinal()]++;
                    return COLORS.valueOf("DARK_RED");
                }
            } else if (deg >= 11 && deg < 45) {
                if ((hsb[1] >= 0.8 && hsb[2] >= 0.60)) {
                    // Orange
                    colorCount[COLORS.valueOf("ORANGE").ordinal()]++;
                    return COLORS.valueOf("ORANGE");
                } else if ((hsb[2] >= 0.75)) {
                    // Light Orange
                    colorCount[COLORS.valueOf("LIGHT_ORANGE").ordinal()]++;
                    return COLORS.valueOf("LIGHT_ORANGE");
                } else {
                    // Brown
                    colorCount[COLORS.valueOf("BROWN").ordinal()]++;
                    return COLORS.valueOf("BROWN");
                }

            } else if (deg >= 45 && deg < 70) {
                // Yellow
                if (deg > 60) {
                    // Blue
                    if (hsb[1] < 0.25 && hsb[2] < 0.35) {
                        colorCount[COLORS.valueOf("BLUE").ordinal()]++;
                        return COLORS.valueOf("BLUE");
                    } else {
                        // Yellow
                        colorCount[COLORS.valueOf("YELLOW").ordinal()]++;
                        return COLORS.valueOf("YELLOW");
                    }
                } else {
                    colorCount[COLORS.valueOf("YELLOW").ordinal()]++;
                    return COLORS.valueOf("YELLOW");
                }
            } else if (deg >= 70 && deg < 178) {
                if (deg < 170 || (hsb[1] >= 0.5 && hsb[2] > 0.4)) {
                    // Green
                    colorCount[COLORS.valueOf("GREEN").ordinal()]++;
                    return COLORS.valueOf("GREEN");
                } else {
                    // Blue
                    colorCount[COLORS.valueOf("BLUE").ordinal()]++;
                    return COLORS.valueOf("BLUE");
                }
            } else if (deg >= 178 && deg < 255) {
                if (hsb[2] >= 0.5) {
                    // Blue
                    colorCount[COLORS.valueOf("BLUE").ordinal()]++;
                    return COLORS.valueOf("BLUE");
                } else if (deg >= 245) {
                    // Purple
                    colorCount[COLORS.valueOf("PURPLE").ordinal()]++;
                    return COLORS.valueOf("PURPLE");
                } else {
                    // Blue
                    colorCount[COLORS.valueOf("BLUE").ordinal()]++;
                    return COLORS.valueOf("BLUE");
                }
            } else if (deg >= 255 && deg < 310) {
                if (hsb[2] < 0.85) {
                    // Purple
                    colorCount[COLORS.valueOf("PURPLE").ordinal()]++;
                    return COLORS.valueOf("PURPLE");
                } else {
                    // Pink
                    colorCount[COLORS.valueOf("PINK").ordinal()]++;
                    return COLORS.valueOf("PINK");
                }
            } else if (deg >= 310 && deg < 335) {
                if ((hsb[1] < 0.5 && hsb[2] >= 0.75) || (hsb[1] >= 0.70 && hsb[2] >= 0.75) || (hsb[1] >= 0.5 && hsb[2] >= 0.65)) {
                    // Pink
                    colorCount[COLORS.valueOf("PINK").ordinal()]++;
                    return COLORS.valueOf("PINK");
                } else {
                    // Purple
                    colorCount[COLORS.valueOf("PURPLE").ordinal()]++;
                    return COLORS.valueOf("PURPLE");
                }
            } else {
                colorCount[COLORS.valueOf("ERROR").ordinal()]++;
                System.out.printf("Error color is %f, %f, %f\n", deg, hsb[1], hsb[2]);
                return COLORS.valueOf("ERROR");
            }
        }
    }


    @Override
    public void onBackPressed()
    {
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
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        Toast.makeText(getApplicationContext(),
                                "YOU DID!",Toast.LENGTH_LONG).show();
                        fileName = input.getText().toString();
                        String end = fileName.substring(fileName.length()-4);
                        System.out.println("******end is " + end);
                        if (!end.equals(".jpg"))
                            fileName += ".jpg";
                        saveFile();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        // User cancelled the dialog
                        Toast.makeText(getApplicationContext(),
                                "YOU CANCELED THE STORAGE!",Toast.LENGTH_LONG).show();
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
            System.out.println("Are you actually saving file?");
            System.out.println(fileName);
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
                        = openFileOutput(fileName + ".Chroma", MODE_PRIVATE);

                PrintStream writer = new PrintStream(fosText);
                writer.println(resultPicture);
                System.out.println(resultPicture);
                writer.close();
                fis = openFileInput(fileName);

                saved = 1;

            } catch (FileNotFoundException e) {
                Log.d(TAG, "Exception trying to open file: " + e);
            }

        }
    }

    public void readFile() {
        FileListActivity mFile = new FileListActivity();
        File dir = this.getFilesDir();
        System.out.println(getFilesDir().toString());

        try {
            Uri uri = Uri.fromFile(new File(mFile.itemValue));

//            InputStream in = null;
//
//            try {
//                in = getContentResolver().openInputStream(uri);
//            }
//            catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            Bitmap image = BitmapFactory.decodeStream(in);
            ImageView imageView = (ImageView) findViewById(R.id.resultImage);
            //imageView.setImageBitmap(image);

            try {
                imageView.setImageURI(uri);
            }
            catch (NullPointerException n){
                System.out.println("Reading File line by line using Bufferreader");

            }

//            Uri uri = Uri.fromFile(new File(fileName + ".Chroma"));
            fis = openFileInput(mFile.itemValue + ".Chroma");
            BufferedReader reader;
            reader = new BufferedReader(new InputStreamReader(fis));
            System.out.println("Reading File line by line using Bufferreader");
            String line = reader.readLine();
            StringBuilder output = new StringBuilder();

            while (line != null) {
                System.out.println(line);
                output.append(line);
                output.append("\n");
                line = reader.readLine();
            }
            mTextView.setText(output);
        }
        catch (IOException e)
        {
            ;
        }
    }


//        try {
//            String content = new Scanner(new File(name)).useDelimiter("\\Z").next();
//            System.out.println(content);
//
//        }
//        catch(FileNotFoundException e1) {
//            Log.d(TAG, "Exception trying to read file: " + e1);
//        }


//    private void generateColorData(Uri pictureToProcess) {
//        InputStream cameraInput = null;
//
//        try {
//            cameraInput = getContentResolver().openInputStream(pictureToProcess);
//        }
//        catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        Bitmap image = BitmapFactory.decodeStream(cameraInput);
//
//        String[][] colors = new String[image.getHeight()][image.getWidth()];
//
//        int[][] result = determineColors(image, colors);
//    }

//    private int[][] determineColors(Bitmap image, String[][] colors) {
//
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        int[] pixels = new int[width*height];
//
//        image.getPixels(pixels, 0, width, 0, 0, width, height);
//
//
//        int[][] result = new int[height][width];
//
//        for (int pixel=0, row=0, col=0; pixel<pixels.length; pixel++) {
//            int c = pixels[pixel];
//
//            colors[row][col] = colorDistanceEnum(c).toString();
//
//            col++;
//            if (col == width) {
//                col = 0;
//                row++;
//            }
//        }
//
//        return result;
//    }

//    private COLORS colorDistanceEnum(int c) {
//        double lowest = 442.0;
//        int index = 0;
//
//        for (int i=0; i<COLOR_LIST.length; i++) {
//            int r = Color.red(COLOR_LIST[i]);
//            int g = Color.green(COLOR_LIST[i]);
//            int b = Color.blue(COLOR_LIST[i]);
//
//            double low = distance(c, r, g, b);
//
//            if (low < lowest) {
//                lowest = low;
//                index = i;
//            }
//        }
//
//        colorCount[index]++;
//
//        return COLORS.values()[index];
//    }

//    private static double distance(int c, int r, int g, int b) {
//        double redDiff = Math.pow(Color.red(c) - r, 2);
//        double greenDiff = Math.pow(Color.green(c) - g, 2);
//        double blueDiff = Math.pow(Color.blue(c) - b, 2);
//
//        return Math.sqrt(redDiff + greenDiff + blueDiff);
//    }
//
//}

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

//            int[][] newPicture = new int[height][width];
//
//            for (int i=0; i<colors.length; i++) {
//                for (int j=0; j<colors[i].length; j++) {
//                    //String name = colors[i][j];
//
//                    //int index = COLORS.valueOf(name).ordinal();
//
//                    newPicture[i][j] = COLOR_LIST[COLORS.valueOf(colors[i][j]).ordinal()];
//                }
//            }

            //Bitmap testOut = bitmapFromArray(newPicture);

//            File storageDir = getAlbumStorageDir("ChromaVisionDebug");
//
//            Locale mylocale = new Locale("en");
//            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", mylocale).format(new Date());
            //File file = new File(storageDir, "testColors" + timeStamp + ".jpg"); // the File to save to
            //OutputStream fOut = null;

//            try {
//                fOut = new FileOutputStream(file);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }

            //testOut.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 100% compression rate

//            try {
//                if (fOut != null) {
//                    fOut.flush();
//                    fOut.close();
//                }
//            }
//            catch (IOException e) {
//                e.printStackTrace();
//            }

            System.out.println(Arrays.toString(colorCount));

            double imageSize = width * height;
            String[] outputArray = new String[COLOR_LIST.length];

            DecimalFormat df = new DecimalFormat("#.##");

            for (int i=0; i<outputArray.length; i++) {
                System.out.println(colorCount[i] + " / " + imageSize);
                double percent = (colorCount[i] / imageSize) * 100;

                if (percent != 0)
                    outputArray[i] = COLORS.values()[i] + ": " + df.format(percent) + "% ";
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
            mTextView.setText(output);
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);


            if (values.length == 2) {
                double row = values[0];
                double progress = row / values[1];
                int update = (int)(progress * 100);
                System.out.println(update);
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


            //int[][] result = new int[height][width];

            for (int pixel=0, row=0, col=0; pixel<pixels.length; pixel++) {
                int c = pixels[pixel];

                float hsv[] = new float[3];
                Color.RGBToHSV(Color.red(c), Color.green(c), Color.blue(c), hsv);

                //colors[row][col] = colorDistanceEnum(c).toString();
                colors[row][col] = hsbEnum(hsv).toString();

                //int index = COLORS.valueOf(colors[row][col]).ordinal();
                //result[row][col] = COLOR_LIST[index];

                col++;
                if (col == width) {
                    publishProgress(row, height);

                    col = 0;
                    row++;
                }
            }
        }

//        private COLORS colorDistanceEnum(int c) {
//            double lowest = 442.0;
//            int index = 0;
//
//            for (int i=0; i<COLOR_LIST.length; i++) {
//                int r = Color.red(COLOR_LIST[i]);
//                int g = Color.green(COLOR_LIST[i]);
//                int b = Color.blue(COLOR_LIST[i]);
//
//                double low = distance(c, r, g, b);
//
//                if (low < lowest) {
//                    lowest = low;
//                    index = i;
//                }
//            }
//
//            colorCount[index]++;
//
//            return COLORS.values()[index];
//        }

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

        private COLORS hsbEnum(float[] hsb) {
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

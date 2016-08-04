package cs371m.chromavision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "Result Activity";

    private TextView mTextView;
    private ProgressBar mProgressBar;
    private GenerateColorDataAsync mRunPicture;


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
        DARKER_GREEN, LIGHTER_GREEN }

    private static final int[] COLOR_LIST = { Color.rgb(0x80, 0x00, 0x00), Color.rgb(0xFF, 0x00, 0x00), Color.rgb(0xFF, 0x80, 0x80),
            Color.rgb(0x00, 0x80, 0x00), Color.rgb(0x00, 0xC0, 0x00), Color.rgb(0x00, 0xFF, 0x00),
            Color.rgb(0x00, 0x00, 0x80), Color.rgb(0x00, 0x00, 0xFF), Color.rgb(0x00, 0xFF, 0xFF),
            Color.rgb(0x80, 0x40, 0x00), Color.rgb(0xFF, 0x80, 0x00), Color.rgb(0xFF, 0xC0, 0x80),
            Color.rgb(0x80, 0x80, 0x00), Color.rgb(0xFF, 0xFF, 0x00), Color.rgb(0xFF, 0xFF, 0x80),
            Color.rgb(0x40, 0x00, 0x40), Color.rgb(0x80, 0x00, 0x80), Color.rgb(0xFF, 0x00, 0xFF),
            Color.rgb(0x00, 0x00, 0x00), Color.rgb(0x80, 0x80, 0x80), Color.rgb(0xFF, 0xFF, 0xFF),
            Color.rgb(0x40, 0x40, 0x40), Color.rgb(0xC0, 0xC0, 0xC0),
            Color.rgb(0x00, 0x40, 0x00), Color.rgb(0x80, 0xFF, 0x80)};

    private int colorCount[] = new int[COLOR_LIST.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Uri picture = intent.getParcelableExtra("pictureUri");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        mProgressBar = (ProgressBar)findViewById(R.id.progressBar);

        if (mProgressBar != null) {
            mProgressBar.setScaleY(5f);
            mProgressBar.setScaleX(0.75f);
        }

        ImageView mImageView = (ImageView) findViewById(R.id.resultImage);
        System.out.println("Getting the cropped picture from " + picture);

        if (mImageView != null) {
            mImageView.setImageURI(picture);
        }

        mTextView = (TextView)findViewById(R.id.colorDataView);

        mRunPicture = new GenerateColorDataAsync();
        // generateColorData(picture);
        mRunPicture.execute(picture);

    }

    @Override
    public void onBackPressed()
    {
        if (mRunPicture != null)
            mRunPicture.cancel(true);

        super.onBackPressed();
    }

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

            System.out.println("test1");
            publishProgress(25);
            Bitmap image = BitmapFactory.decodeStream(cameraInput);
            System.out.println("test2");
            publishProgress(50);
            String[][] colors = new String[image.getHeight()][image.getWidth()];
            System.out.println("test3");


            int[][] result = determineColors(image, colors);
            int[][] newPicture = new int[height][width];

            for (int i=0; i<colors.length; i++) {
                for (int j=0; j<colors[i].length; j++) {
                    String name = colors[i][j];

                    int index = COLORS.valueOf(name).ordinal();

                    newPicture[i][j] = COLOR_LIST[index];
                }
            }

            Bitmap testOut = bitmapFromArray(newPicture);

            File storageDir = getAlbumStorageDir("ChromaVisionDebug");

            Locale mylocale = new Locale("en");
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", mylocale).format(new Date());
            File file = new File(storageDir, "testColors" + timeStamp + ".jpg"); // the File to save to
            OutputStream fOut = null;

            try {
                fOut = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            testOut.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 100% compression rate
            try {
                if (fOut != null) {
                    fOut.flush();
                    fOut.close();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }




            publishProgress(75);
            System.out.println("test4");
            publishProgress(100);
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

        private int[][] determineColors(Bitmap image, String[][] colors) {

            int width = image.getWidth();
            int height = image.getHeight();

            int[] pixels = new int[width*height];

            image.getPixels(pixels, 0, width, 0, 0, width, height);


            int[][] result = new int[height][width];

            for (int pixel=0, row=0, col=0; pixel<pixels.length; pixel++) {
                int c = pixels[pixel];

                float hsv[] = new float[3];

                Color.RGBToHSV(Color.red(c), Color.green(c), Color.blue(c), hsv);

                //colors[row][col] = colorDistanceEnum(c).toString();
                colors[row][col] = hsbEnum(hsv).toString();

                int index = COLORS.valueOf(colors[row][col]).ordinal();
                result[row][col] = COLOR_LIST[index];

                col++;
                if (col == width) {
                    publishProgress(row, height);

                    col = 0;
                    row++;
                }
            }

            return result;
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

        private COLORS hsbEnum(float[] hsb) {

            if      (hsb[1] < 0.15 && hsb[2] >= 0.65) {
                colorCount[COLORS.valueOf("WHITE").ordinal()]++;
                return COLORS.valueOf("WHITE");
            }
            else if (hsb[1] < 0.15 && hsb[2] > 0.1 && hsb[2] < 0.65) {

                colorCount[COLORS.valueOf("GREY").ordinal()]++;
                return COLORS.valueOf("GREY");
            }
            else if (hsb[2] < 0.1 ) {

                colorCount[COLORS.valueOf("BLACK").ordinal()]++;
                return COLORS.valueOf("BLACK");
            }
            else {
                //float deg = hsb[0]*360; //multiply by 360 in regular java, not android
                float deg = hsb[0];
                //System.out.println(deg);
                if      (deg >=   351 && deg <  11 && hsb[1] >= 0.7) {
                    int index = COLORS.valueOf("RED").ordinal();
                    colorCount[index]++;
                    //System.out.println("index: " + index + "  deg between 0 and 30");
                    return COLORS.valueOf("RED");
                }
                else if ((deg >=  351 && deg <  11 && hsb[1] < 0.7) || (deg >= 310 && deg < 351 && hsb[1] > 0.15)) {
                    colorCount[COLORS.valueOf("PINK").ordinal()]++;
                    return COLORS.valueOf("PINK");
                }
                else if (deg >= 11 && deg < 45 && hsb[1] >= 0.15 && hsb[2] > 0.75) {
                    colorCount[COLORS.valueOf("ORANGE").ordinal()]++;
                    return COLORS.valueOf("ORANGE");
                }
                else if (deg >= 11 && deg < 45 && hsb[1] >= 0.15 && hsb[2] >= 0.1 && hsb[2] < 0.75) {
                    colorCount[COLORS.valueOf("BROWN").ordinal()]++;
                    return COLORS.valueOf("BROWN");
                }
                else if (deg >=  45 && deg <  64 && hsb[1] >= 0.15) {
                    colorCount[COLORS.valueOf("YELLOW").ordinal()]++;
                    return COLORS.valueOf("YELLOW");
                }
                else if (deg >=  64 && deg < 180 && hsb[1] >= 0.15) {
                    colorCount[COLORS.valueOf("GREEN").ordinal()]++;
                    return COLORS.valueOf("GREEN");
                }
                else if (deg >= 180 && deg < 255 && hsb[1] >= 0.15) {
                    colorCount[COLORS.valueOf("BLUE").ordinal()]++;
                    return COLORS.valueOf("BLUE");
                }
                else if (deg >= 255 && deg < 310 && hsb[1] >= 0.5) {
                    colorCount[COLORS.valueOf("PURPLE").ordinal()]++;
                    return COLORS.valueOf("PURPLE");
                }
                else if (deg >= 255 && deg < 310 && hsb[1] >= 0.15 && hsb[1] < 0.5) {
                    colorCount[COLORS.valueOf("PURPLE").ordinal()]++;
                    return COLORS.valueOf("PURPLE");
                }
                else {
                    colorCount[COLORS.valueOf("RED").ordinal()]++;
                    return COLORS.valueOf("RED");
                }
            }

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

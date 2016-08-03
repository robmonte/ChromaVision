package cs371m.chromavision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Arrays;

public class ResultActivity extends AppCompatActivity {

    ImageView mImageView;
    TextView mTextView;

    private enum COLORS { BLACK, VERY_DARK_RED, DARK_RED, MEDIUM_RED, BRIGHT_RED, PALE_RED, LIGHT_RED, VERY_LIGHT_RED, WHITE }

    private static final int[] COLOR_LIST = { Color.rgb(0x00, 0x00, 0x00), Color.rgb(0x40, 0x00, 0x00), Color.rgb(0x80, 0x00, 0x00),
            Color.rgb(0xC0, 0x00, 0x00), Color.rgb(0xFF, 0x00, 0x00), Color.rgb(0xFF, 0xC0, 0x00),
            Color.rgb(0xFF, 0x80, 0x80), Color.rgb(0xFF, 0xC0, 0xC0), Color.rgb(0xFF, 0xFF, 0xFF) };

    private int colorCount[] = new int[COLOR_LIST.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();

        Uri picture = intent.getParcelableExtra("pictureUri");

        int width = intent.getIntExtra("width", 1);
        int height = intent.getIntExtra("height", 1);

        System.out.println("Getting the cropped picture from " + picture);

        mImageView = (ImageView)findViewById(R.id.resultImage);

        mImageView.setImageURI(picture);

        mTextView = (TextView)findViewById(R.id.colorDataView);

//        makePicture getpic = new makePicture();
// generateColorData(picture);
//        getpic.execute(picture);
        System.out.println(Arrays.toString(colorCount));

        double imageSize = width * height;
        String[] outputArray = new String[COLOR_LIST.length];

        DecimalFormat df = new DecimalFormat("#.##");
        colorCount[0] += colorCount[1];

        for (int i=0; i<outputArray.length; i++) {
            System.out.println(colorCount[i] + " / " + imageSize);
            double percent = (colorCount[i] / imageSize) * 100;

            if (i != 1)
                outputArray[i] = COLORS.values()[i] + ": " + df.format(percent) + "% ";
        }

        StringBuilder output = new StringBuilder();
        for (String s: outputArray) {
            if (s != null) {
                output.append(s);
                output.append("\n");
            }
        }

        mTextView.setText(output);
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
//        int[][] result = convertTo2DWithoutUsingGetRGB(image, colors);
//    }

//    private int[][] convertTo2DWithoutUsingGetRGB(Bitmap image, String[][] colors) {
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

    class makePicture extends AsyncTask<Uri, Integer, int[][]> {
        @Override
        protected int[][] doInBackground(Uri... params) {


            InputStream cameraInput = null;

            try {
                cameraInput = getContentResolver().openInputStream(params[0]);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("test1");
            Bitmap image = BitmapFactory.decodeStream(cameraInput);
            System.out.println("test2");
            String[][] colors = new String[image.getHeight()][image.getWidth()];
            System.out.println("test3");
            int[][] result = convertTo2DWithoutUsingGetRGB(image, colors);
            System.out.println("test4");
            return result;


        }

        @Override
        protected void onPostExecute(int[][] ints) {
            super.onPostExecute(ints);

        }

        private int[][] convertTo2DWithoutUsingGetRGB(Bitmap image, String[][] colors) {

            int width = image.getWidth();
            int height = image.getHeight();

            int[] pixels = new int[width*height];

            image.getPixels(pixels, 0, width, 0, 0, width, height);


            int[][] result = new int[height][width];

            for (int pixel=0, row=0, col=0; pixel<pixels.length; pixel++) {
                int c = pixels[pixel];

                colors[row][col] = colorDistanceEnum(c).toString();

                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
            }

            return result;
        }
        private COLORS colorDistanceEnum(int c) {
            double lowest = 442.0;
            int index = 0;

            for (int i=0; i<COLOR_LIST.length; i++) {
                int r = Color.red(COLOR_LIST[i]);
                int g = Color.green(COLOR_LIST[i]);
                int b = Color.blue(COLOR_LIST[i]);

                double low = distance(c, r, g, b);

                if (low < lowest) {
                    lowest = low;
                    index = i;
                }
            }

            colorCount[index]++;

            return COLORS.values()[index];
        }

        private double distance(int c, int r, int g, int b) {
            double redDiff = Math.pow(Color.red(c) - r, 2);
            double greenDiff = Math.pow(Color.green(c) - g, 2);
            double blueDiff = Math.pow(Color.blue(c) - b, 2);

            return Math.sqrt(redDiff + greenDiff + blueDiff);
        }


    }


}

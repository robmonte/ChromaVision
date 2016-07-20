package cs371m.chromavision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

public class ResultActivity extends AppCompatActivity {

    ImageView mImageView;
    TextView mTextView;

    private enum COLORS { DARK_RED, RED, LIGHT_RED }

    private static final int[] COLOR_LIST = { Color.rgb(0x80, 0x00, 0x00), Color.rgb(0xFF, 0x00, 0x00), Color.rgb(0xFF, 0x80, 0x80) };

    private int colorCount[] = new int[COLOR_LIST.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();

        Uri picture = intent.getParcelableExtra("pictureUri");

        System.out.println("Getting the cropped picture from " + picture);

        mImageView = (ImageView)findViewById(R.id.resultImage);

        mImageView.setImageURI(picture);

        mTextView = (TextView)findViewById(R.id.colorDataView);

        generateColorData(picture);

        System.out.println(Arrays.toString(colorCount));
        String darkRed = "# of dark red: " + colorCount[0];
        String red = "# of red: " + colorCount[1];
        String lightRed = "# of light red: " + colorCount[2];

        mTextView.setText(darkRed + "\n\n" + red + "\n\n" + lightRed);
    }

    private void generateColorData(Uri pictureToProcess) {
        InputStream cameraInput = null;

        try {
            cameraInput = getContentResolver().openInputStream(pictureToProcess);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap image = BitmapFactory.decodeStream(cameraInput);

        String[][] colors = new String[image.getHeight()][image.getWidth()];

        int[][] result = convertTo2DWithoutUsingGetRGB(image, colors);
    }

    private int[][] convertTo2DWithoutUsingGetRGB(Bitmap image, String[][] colors) {

        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width*height];

        image.getPixels(pixels, 0, width, 0, 0, width, height);

        final boolean hasAlphaChannel = false; //image.getAlphaRaster() != null;

        int[][] result = new int[height][width];
        if (hasAlphaChannel) {
//            final int pixelLength = 4;
//            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
//                int argb = 0;
//                argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
//                argb += ((int) pixels[pixel + 1] & 0xff); // blue
//                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
//                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
//                result[row][col] = argb;
//                System.out.printf("%X:", argb);
//                Color c = new Color(argb);
//                int red = (((int) pixels[pixel + 3] & 0xff));
//                System.out.printf("%X:", red);
//                System.out.printf("%X:", c.getRed());
//                System.out.printf("%X:", c.getGreen());
//                System.out.printf("%X ", c.getBlue());
//                col++;
//                if (col == width) {
//                    col = 0;
//                    row++;
//                    System.out.println();
//                }
//            }
        }
        else {
//            final int pixelLength = 3;
//            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
//                int argb = 0;
//                //argb += -16777216; // 255 alpha
//                //argb += ((int) pixels[pixel] & 0xff); // blue
//                //argb += (((int) pixels[pixel + 1] & 0xff) << 8); // green
//                //argb += (((int) pixels[pixel + 2] & 0xff) << 16); // red
//
//                int blue = ((int) pixels[pixel] & 0xff); // blue
//                int green = (((int) pixels[pixel] & 0xff) << 8); // green
//                int red = (((int) pixels[pixel] & 0xff) << 16); // red
//
//                //Color c = new Color(argb);
//                int c = Color.rgb(red, green, blue);
//
//                colors[row][col] = colorDistanceEnum(c).toString();
//
//                //System.out.println(colors[row][col]);
//
//                if (colors[row][col].equals("LIGHT_GREY"))
//                    result[row][col] = argb;
//
//                //result[row][col] = argb;
//                col++;
//                if (col == width) {
//                    col = 0;
//                    row++;
//                }
//            }

            for (int pixel=0, row=0, col=0; pixel<pixels.length; pixel++) {
                int c = pixels[pixel];

                colors[row][col] = colorDistanceEnum(c).toString();

                //result[row][col] = argb;
                col++;
                if (col == width) {
                    col = 0;
                    row++;
                }
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

            //System.out.println(r + " " + g + " " + b);

            double low = distance(c, r, g, b);

            //System.out.println("low is " + low + " and lowest is " + lowest);
            if (low < lowest) {
                lowest = low;
                index = i;
            }
        }

        //System.out.println("return " + index);

        colorCount[index]++;

        return COLORS.values()[index];
    }

    private static double distance(int c, int r, int g, int b) {
        double redDiff = Math.pow(Color.red(c) - r, 2);
        double greenDiff = Math.pow(Color.green(c) - g, 2);
        double blueDiff = Math.pow(Color.blue(c) - b, 2);

        return Math.sqrt(redDiff + greenDiff + blueDiff);
    }

}

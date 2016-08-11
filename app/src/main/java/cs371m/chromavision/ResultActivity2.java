package cs371m.chromavision;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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


public class ResultActivity2 extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result2);
        mResult = new ResultActivity();

        mTextView = (TextView)findViewById(R.id.colorDataView);

        readFile();

        System.out.println("helllllo!");
        final ImageView mImageView = (ImageView) findViewById(R.id.resultImage);



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
                        String loc = "(" + x + ", " + y + "): ";
                        clickPixelLocation.setText(loc);
                    }
                }
                return true;
            }

        });


    }

    private void readFile() {
        FileListActivity mFile = new FileListActivity();
        File dir = this.getFilesDir();
        System.out.println("++++++++++++" + getFilesDir().toString());

        try {

            Uri uri = Uri.fromFile(new File(getFilesDir().toString() + "/" + mFile.itemValue));

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

            String str = output.toString();

            System.out.println("STR IS " + str + "    ***********");
            mTextView.setText(str);
        }
        catch (IOException e)
        {
            ;
        }
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
        if (item.getItemId() == R.id.save)
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
                FileOutputStream fos
                        = openFileOutput(fileName, MODE_PRIVATE);

                PrintStream writer = new PrintStream(fos);
//            Random r = new Random();
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

}

package cs371m.chromavision;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ResultActivity extends AppCompatActivity {

    ImageView mImageView;
    Bitmap resultBitmap;
    MainMenuActivity mMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
//        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();



        Intent intent = getIntent();

        Uri picture = intent.getParcelableExtra("pictureUri");

        System.out.println("Getting the cropped picture from " + picture);

        mImageView = (ImageView)findViewById(R.id.resultImage);
        mImageView.setImageURI(picture);




//        resultBitmap = intent.getParcelableExtra("BitmapImage");
//        mImageView = (ImageView) findViewById(R.id.resultImage);
//        if (resultBitmap != null) {
//            mImageView.setImageBitmap(resultBitmap);
//        }




        // Pulled code
//        mImageView = (ImageView) findViewById(R.id.resultImage);
//        System.out.println("in result activity.");
////        File file = new File(mMain.photoUri.getPath());
    }

}

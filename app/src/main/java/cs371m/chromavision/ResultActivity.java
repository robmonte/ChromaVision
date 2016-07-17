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
        mImageView = (ImageView) findViewById(R.id.resultImage);
        System.out.println("in result activity.");
//        File file = new File(mMain.photoUri.getPath());
    }

}

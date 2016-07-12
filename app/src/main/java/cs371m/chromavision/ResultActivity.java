package cs371m.chromavision;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

public class ResultActivity extends AppCompatActivity {

    ImageView mImageView;
    Bitmap resultBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

//        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();

        Intent intent = getIntent();
        resultBitmap = intent.getParcelableExtra("BitmapImage");
        mImageView = (ImageView) findViewById(R.id.resultImage);
        if (resultBitmap != null) {
            mImageView.setImageBitmap(resultBitmap);
        }
    }

}

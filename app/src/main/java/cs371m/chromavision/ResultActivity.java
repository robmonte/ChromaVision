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

public class ResultActivity extends AppCompatActivity {

    ImageView mImageView;
    Bitmap resultBitmap;
    public MainMenuActivity mMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

//        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();

        Intent intent = getIntent();
//        resultBitmap = intent.getParcelableExtra("BitmapImage");
        mImageView = (ImageView) findViewById(R.id.resultImage);
        System.out.println("in result activity.");
//        if (resultBitmap != null) {
        getBitmapfromUri(mMain.photoUri);
//            mImageView.setImageBitmap(resultBitmap);

    }

    public void getBitmapfromUri(Uri pickedImage)
    {
        // Let's read picked image path using content resolver
        String[] filePath = { MediaStore.Images.Media.DATA };
        File picture = new File(""+mMain.photoUri);
        System.out.println("test");
        Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
        cursor.moveToFirst();
        String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
        mImageView.setImageBitmap(bitmap);

        // Do something with the bitmap


        // At the end remember to close the cursor or you will end with the RuntimeException!
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("in result activity, for gallery picture.");
        if (requestCode == Activity.RESULT_OK && resultCode == mMain.GALLERY_REQUEST_CODE)
        {
            Uri pickedImage = data.getData();
            // Let's read picked image path using content resolver
            String[] filePath = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
            cursor.moveToFirst();
            String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
            mImageView.setImageBitmap(bitmap);

            cursor.close();
        }
    }
}

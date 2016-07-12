package cs371m.chromavision;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class MainMenuActivity extends AppCompatActivity{

    public static final String TAG = "MainMenuActivity";

    static final int CAMERA_REQUEST_CODE = 1;
    static final int GALLERY_REQUEST_CODE = 2;
    static final int LOAD_FILE_REQUEST_CODE = 3;

    private Uri fileUri;
    public String mCurrentPhotoPath;
    private Bitmap resultBitmap;

//    ImageView mImageView;

    /**
     * Create a collision-resistant file name.
     * Code come from 'Taking Photos Simply' android developer
     * @return a unique file name for a new photo using date-time stamp
     * @throws IOException
     */
    private File createImageFile() throws IOException {

        // get the local time presentation
        // construct a locale from language
        // "en" shorts for 'English'
        // see at 'Locale' android developer
        Locale mylocale = new Locale("en");

        // create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", mylocale).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // get a directory to save the photo
        // getExternalStoragePublicDirectory returns a proper directory for shared photos,
        // Which means the photo is accessible by all apps.
        //File storageDir = getExternalStoragePublicDirectory(DIRECTORY_PICTURES);
        //= getAlbumStorageDir("ChromaVision");

        File storageDir = getAlbumStorageDir("ChromaVision");

        File image = File.createTempFile(
                imageFileName,      // prefix
                ".jpg",             // suffix
                storageDir          // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
//        mImageView = (ImageView) findViewById(R.id.resultImage);
    }

    /**
     * Called when the user click the 'take a new picture' button
     * Create an intent to capture new pictures
     * MediaStore. ACTION_IMAGE_CAPTURE - Intent action type for
     *       requesting an image from an existing camera application.
     **/
    public void takeAPicture(View view) {

        // Create Intent to take a picture and return control to the
        // calling application.
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        /*
        // create a file to save the image
        // Uri: specify a path and file name where you'd like to save the picture
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        // set the image file name
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        */

        File photoFile = null;

        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
//            System.out.println("create Image file failed");
        }
        Log.d(TAG, "testing");
        System.out.println("testing!!!!");

        // Continue only if the file was successfully created
        if (photoFile != null) {
            Uri photoURI = Uri.fromFile(photoFile);
//            Uri photoURI = FileProvider.getUriForFile(this,
//                    "cs371m.fileprovider",
//                    photoFile);
            Log.d(TAG, "logging: " + photoURI.toString());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }
//        startActivity(intent);
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }

    /** Called when the user click the 'pick from gallery' button */
    public void pickFromGallery(View view) {
//        Intent intent = new Intent(this, GalleryActivity.class);
//        Intent intent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(intent, GALLERY_REQUEST_CODE);
//        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
//        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    /**
     * To receive the result from the subsequent activity
     * @param requestCode: which request this result is responding to
     * @param resultCode: RESULT_OK means the request was successful
     * @param data: a result in an Intent from the subsequent activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the request went well(OK)
//        super.onActivityResult(requestCode, resultCode, data);
        // Uri targetUri = data.getData();
        System.out.println("get data");
        galleryAddPic();
        if (data == null) {
            System.out.println("No data available.");
        }
        else if (resultCode == Activity.RESULT_OK) {
            try {
                // Check which request we're responding to
                if (requestCode == CAMERA_REQUEST_CODE) {

                /*
                // Image captured and saved to fileUri specified in the Intent
                 Toast.makeText(this, "Image saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG().show());
                */

                    // The Android Camera application encodes the photo in the
                    // return Intent delivered to onActivityResult() as a small
                    // Bitmap in the extras, under the key "data".
                    galleryAddPic();
                    Bundle extras = data.getExtras();
                    resultBitmap = (Bitmap) extras.get("data");
//                mImageView.setImageBitmap(imageBitmap);
                    Intent resultIntent = new Intent(this, ResultActivity.class);
                    resultIntent.putExtra("BitmapImage", resultBitmap);
                    startActivity(resultIntent);

                } else if (requestCode == GALLERY_REQUEST_CODE) {
                    // Do something with the returned image
//                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());

                    // Get the image from data
//                    Uri selectedImage = data.getData();
//                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                    // Get the cursor
//                    Cursor cursor = getContentResolver().query(selectedImage,
//                            filePathColumn, null, null, null);
//
//                    // Move to first row
//                    cursor.moveToFirst();
//
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    String imgDecodableString = cursor.getString(columnIndex);
//                    cursor.close();
//                    resultBitmap = BitmapFactory.decodeFile(imgDecodableString);
//                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
//                    System.out.println("get data");
//                    resultBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    Intent resultIntent = new Intent(this, ResultActivity.class);
//                    resultIntent.putExtra("BitmapImage", resultBitmap);
                    startActivity(resultIntent);
                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the image capture

                } else {
                    // Image capture failed, advise user

                }
            } catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Add the Photo to a Gallery
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

//        private void setPic() {
//        // Get the dimension of the View
//        int targetW = mImageView.getWidth();
//        int targetH = mImageView.getHeight();
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        // Determine how much to scale down the image
//        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//
//        // Decode the image file into a Bitmap sized to fill the View
//        bmOptions.inJustDecodeBounds = false;
//        bmOptions.inSampleSize = scaleFactor;
//        bmOptions.inPurgeable = true;
//
//        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
//        mImageView.setImageBitmap(bitmap);
//    }

}

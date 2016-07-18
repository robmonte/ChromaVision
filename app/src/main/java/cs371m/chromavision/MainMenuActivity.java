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
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public Uri photoUri;
    public String mCurrentPhotoPath;
    private Bitmap resultBitmap;
    public File storageDir;
    public File photoFile;

    /**
     * Create a collision-resistant file name.
     * Code come from 'Taking Photos Simply' android developer
     * @return a unique file name for a new photo using date-time stamp
     * @throws IOException
     */
    private File createImageFile() throws IOException {

        // get the local time presentation
        Locale mylocale = new Locale("en");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", mylocale).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        storageDir = getAlbumStorageDir("ChromaVision");

        File image = File.createTempFile(
                imageFileName,      // prefix
                ".jpg",             // suffix
                storageDir          // directory
        );

        System.out.println("Calling crop");


        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
    }

    /**
     * Called when the user click the 'take a new picture' button
     * Create an intent to capture new pictures
     * MediaStore. ACTION_IMAGE_CAPTURE - Intent action type for
     *       requesting an image from an existing camera application.
     **/
    public void takeAPicture(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = null;

        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
        }
        Log.d(TAG, "testing");
        System.out.println("testing!!!!");

        if (photoFile != null) {
            photoUri = Uri.fromFile(photoFile);
            CropImage.activity(photoUri).setGuidelines(CropImageView.Guidelines.ON).start(this);
            Log.d(TAG, "logging: " + photoUri.toString());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        }


    }

        // Get the directory for the user's public pictures directory.
    public File getAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        return file;
    }

    /** Called when the user click the 'pick from gallery' button */
    public void pickFromGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);
    }

    /**
     * To receive the result from the subsequent activity
     * @param requestCode: which request this result is responding to
     * @param resultCode: RESULT_OK means the request was successful
     * @param data: a result in an Intent from the subsequent activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("get data");

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                System.out.println("resultUri is " + resultUri.toString());

                Intent cropped = new Intent(this, ResultActivity.class);

                cropped.putExtra("pictureUri", resultUri);

                startActivity(cropped);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        //galleryAddPic();
        if (data == null) {
            System.out.println("No data available.");
        }
        if (resultCode == Activity.RESULT_OK) {
            try {
                System.out.println("resultCode is okay.");
                // Check which request we're responding to
                if (requestCode == CAMERA_REQUEST_CODE) {
//                    galleryAddPic();
//                    Bundle extras = data.getExtras();
//                    resultBitmap = (Bitmap) extras.get("data");
                    System.out.println("receive camera.");
                    Intent resultIntent = new Intent(this, ResultActivity.class);
//                    resultIntent.putExtra("BitmapImage", resultBitmap);
                    startActivity(resultIntent);

                } else if (requestCode == GALLERY_REQUEST_CODE) {
                    System.out.println("inside gallery request");

                    // Do something with the returned image
//                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());

                    // Get the image from data
                    Uri selectedImage = data.getData();

                    File scaleFile = new File(selectedImage.toString());
                    Bitmap scale = BitmapFactory.decodeFile(scaleFile.toString());

                    scale = Bitmap.createScaledBitmap(scale, 1280, 720, true);


                    storageDir = getAlbumStorageDir("ChromaVision");
                    OutputStream fOut = null;
                    File file = new File(storageDir, "scaled"+System.currentTimeMillis()); // the File to save to
                    fOut = new FileOutputStream(file);

                    scale.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                    fOut.flush();
                    fOut.close(); // do not forget to close the stream

                    Uri fileUri = android.net.Uri.parse(file.toURI().toString());




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


                    System.out.println("receive picture from gallery.");
                    Intent resultIntent = new Intent(this, ResultActivity.class);
                    System.out.println("selectedImage gallery is " + fileUri);
                    resultIntent.putExtra("pictureUri", selectedImage);
                    startActivityForResult(resultIntent, GALLERY_REQUEST_CODE);
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
//        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(storageDir);
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

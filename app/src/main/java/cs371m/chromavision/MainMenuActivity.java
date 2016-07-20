package cs371m.chromavision;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainMenuActivity extends AppCompatActivity{

    public static final String TAG = "MainMenuActivity";

    static final int CAMERA_REQUEST_CODE = 1;
    static final int GALLERY_REQUEST_CODE = 2;

    private File storageDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        storageDir = getAlbumStorageDir("ChromaVision");
    }

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
        String imageFileName = "temp" + timeStamp;

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * Called when the user click the 'take a new picture' button
     * Create an intent to capture new pictures
     * MediaStore. ACTION_IMAGE_CAPTURE - Intent action type for
     *       requesting an image from an existing camera application.
     **/
    public void takeAPicture(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;

        try {
            photoFile = createImageFile();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        if (photoFile != null) {
            Uri photoUri = Uri.fromFile(photoFile);

            Log.d(TAG, "takeAPicture: " + photoUri.toString());

            CropImage.activity(photoUri).setGuidelines(CropImageView.Guidelines.ON).start(this);

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
        System.out.println("inside onActivityResult");

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            System.out.println("Inside CROP_IMAGE_ACTIVITY_REQUEST");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri croppedImage = result.getUri();

                InputStream galleryInput = null;
                try {
                    galleryInput = getContentResolver().openInputStream(croppedImage);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap scale = BitmapFactory.decodeStream(galleryInput);

                scale = resizeImageToScreen(scale);

                OutputStream fOut = null;
                Locale mylocale = new Locale("en");
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", mylocale).format(new Date());
                File file = new File(storageDir, "scaledcropped" + timeStamp + ".jpg"); // the File to save to

                try {
                    fOut = new FileOutputStream(file);
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                scale.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 100% compression rate
                try {
                    fOut.flush();
                    fOut.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                Uri fileUri = android.net.Uri.parse(file.toURI().toString());


                System.out.println("pictureCrop is " + fileUri.toString());
                Intent cropped = new Intent(this, ResultActivity.class);
                cropped.putExtra("pictureUri", fileUri);

                startActivity(cropped);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                error.printStackTrace();
            }
        }

        //galleryAddPic();
        if (data == null) {
            System.out.println("No data available.");
        }
        if (resultCode == Activity.RESULT_OK) {
            try {
                // Check which request we're responding to
                if (requestCode == CAMERA_REQUEST_CODE) {
                    //galleryAddPic();
//                    System.out.println("Inside CAMERA_REQUEST_CODE");
//
//                    Intent resultIntent = new Intent(this, ResultActivity.class);
//                    startActivity(resultIntent);

                }
                else if (requestCode == GALLERY_REQUEST_CODE) {
                    System.out.println("Inside GALLERY_REQUEST_CODE");

                    // Get the image from data
                    Uri selectedImage = data.getData();

                    System.out.println("selectedImage URI: " + selectedImage);
                    System.out.println("selectedImage toString: " + selectedImage.toString());

                    InputStream galleryInput = getContentResolver().openInputStream(selectedImage);
                    Bitmap scale = BitmapFactory.decodeStream(galleryInput);

                    scale = resizeImageToScreen(scale);


                    storageDir = getAlbumStorageDir("ChromaVision");
                    Locale mylocale = new Locale("en");
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", mylocale).format(new Date());
                    File file = new File(storageDir, "scaledgallery" + timeStamp + ".jpg"); // the File to save to
                    OutputStream fOut = new FileOutputStream(file);

                    scale.compress(Bitmap.CompressFormat.JPEG, 100, fOut); // saving the Bitmap to a file compressed as a JPEG with 100% compression rate
                    fOut.flush();
                    fOut.close(); // do not forget to close the stream

                    Uri fileUri = android.net.Uri.parse(file.toURI().toString());


                    System.out.println("receive picture from gallery.");
                    Intent resultIntent = new Intent(this, ResultActivity.class);
                    System.out.println("selectedImage gallery is " + fileUri);
                    resultIntent.putExtra("pictureUri", fileUri);
                    startActivityForResult(resultIntent, GALLERY_REQUEST_CODE);
                }
                else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the image capture

                }
                else {
                    // Image capture failed, advise user

                }
            }
            catch (Exception e) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private Bitmap resizeImageToScreen(Bitmap scale) {
        DisplayMetrics dm = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(dm);

        double screenWidth = dm.widthPixels;
        double screenHeight = dm.heightPixels;

        int width = scale.getWidth();
        int height = scale.getHeight();

        if (width <= screenWidth && height <= screenHeight)
            return scale;

        double scaleRatioHeight = screenHeight / height;
        double scaleRatioWidth = screenWidth / width;
        double scaleRatio;

        if (scaleRatioHeight < scaleRatioWidth)
            scaleRatio = scaleRatioHeight;
        else
            scaleRatio = scaleRatioWidth;

        System.out.println("Screen resolution: " + screenWidth + "x" + screenHeight);

        return Bitmap.createScaledBitmap(scale, (int)(width*scaleRatio), (int)(height*scaleRatio), true);
    }

    // Add the Photo to a Gallery
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(storageDir);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}

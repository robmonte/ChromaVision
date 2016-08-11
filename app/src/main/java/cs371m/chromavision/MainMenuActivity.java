package cs371m.chromavision;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

public class MainMenuActivity extends AppCompatActivity {

    public static final String TAG = "MainMenuActivity";

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int REQUEST_CODE_PERMISSION = 3;
    private File storageDir;
    private Uri mCameraImageUri;
    private MenuItem mTutorialMenuItem;

    private boolean mPrecise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        int cameraPermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int storagePermissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        Log.d(TAG, Integer.toString(cameraPermissionCheck));

        if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED || storagePermissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Log.d(TAG, "Camera permission denied!");
            }
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.d(TAG, "Storage permission denied!");
            }
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            }
        }

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean hideTutorial = prefs.getBoolean("ViewedTutorial", false);
        System.out.println("hide tutorial is " + hideTutorial);
        if (hideTutorial) {
            Button tutorialButton = (Button)findViewById(R.id.button4);
            if (tutorialButton != null) {
                tutorialButton.setVisibility(View.GONE);
            }
        }

        storageDir = getAlbumStorageDir("ChromaVision");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_menu, menu);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean showWelcome = prefs.getBoolean("ViewedWelcome", false);
        MenuItem checkWelcome = menu.findItem(R.id.show_welcome_checkbox);
        checkWelcome.setChecked(!showWelcome);

        boolean showTutorial = prefs.getBoolean("ViewedTutorial", false);
        Log.d(TAG, "HideTutorial is " + showTutorial);
        MenuItem checkTutorial = menu.findItem(R.id.tutorial_button_checkbox);
        mTutorialMenuItem = checkTutorial;
        checkTutorial.setChecked(!showTutorial);

        mPrecise = prefs.getBoolean("PreciseMode", true);
        MenuItem checkPrecise = menu.findItem(R.id.precise_mode_checkbox);
        checkPrecise.setChecked(mPrecise);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        switch (item.getItemId()) {
            case R.id.show_welcome_checkbox:
                boolean showWelcome = prefs.getBoolean("ViewedWelcome", false);
                item.setChecked(showWelcome);
                editor.putBoolean("ViewedWelcome", !showWelcome);
                editor.apply();
                return true;
            case R.id.precise_mode_checkbox:
                mPrecise = prefs.getBoolean("PreciseMode", true);
                item.setChecked(!mPrecise);
                editor.putBoolean("PreciseMode", !mPrecise);
                editor.apply();
                return true;
            case R.id.tutorial_button_checkbox:
                boolean hideTutorial = prefs.getBoolean("ViewedTutorial", false);
                item.setChecked(hideTutorial);
                editor.putBoolean("ViewedTutorial", !hideTutorial);
                System.out.println("on select, hidetutorial is " + hideTutorial);
                editor.apply();
                Button tutorialButton = (Button)findViewById(R.id.button4);
                if (tutorialButton != null) {
                    if (hideTutorial)
                        tutorialButton.setVisibility(View.VISIBLE);
                    else
                        tutorialButton.setVisibility(View.GONE);
                }
                return true;
            case R.id.about_menu_button:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                }
                else {

                    Log.d(TAG, "Permissions were denied!!");

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Create a collision-resistant file name.
     * Code come from 'Taking Photos Simply' android developer
     *
     * @return a unique file name for a new photo using date-time stamp
     * @throws IOException
     */
    private File createImageFile() throws IOException {

        // get the local time presentation
        Locale mylocale = new Locale("en");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", mylocale).format(new Date());
        String imageFileName = "cameraTemp" + timeStamp;

        storageDir = getAlbumStorageDir("ChromaVision");
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    /**
     * Called when the user click the 'take a new picture' button
     * Create an intent to capture new pictures
     * MediaStore. ACTION_IMAGE_CAPTURE - Intent action type for
     * requesting an image from an existing camera application.
     **/
    public void takeAPicture(View view) {

//        int permissionCheck = ContextCompat.checkSelfPermission(this,
//                Manifest.permission.CAMERA);
//
//        if (permissionCheck == PackageManager.PERMISSION_DENIED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//                Log.d(TAG, "Camera permission denied!");
//            }
//            else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
//            }
//        }

        Log.d(TAG, "After the requests!");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;

        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (photoFile != null) {
            Uri photoUri = Uri.fromFile(photoFile);

            Log.d(TAG, "takeAPicture: " + photoUri.toString());

            mCameraImageUri = photoUri;

            //CropImage.activity(photoUri).setGuidelines(CropImageView.Guidelines.ON).start(this);

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

    /**
     * Called when the user click the 'pick from gallery' button
     */
    public void pickFromGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        System.out.println("choosing");

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST_CODE);

    }

//    public void pickFromGallery(View view) {
////        Intent intent = new Intent();
//        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, GALLERY_REQUEST_CODE);
//    }

    /**
     * To receive the result from the subsequent activity
     *
     * @param requestCode: which request this result is responding to
     * @param resultCode:  RESULT_OK means the request was successful
     * @param data:        a result in an Intent from the subsequent activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("inside onActivityResult");

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            System.out.println("Inside CROP_IMAGE_ACTIVITY_REQUEST");
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri croppedImage = result.getUri();

                InputStream cameraInput = null;
                try {
                    cameraInput = getContentResolver().openInputStream(croppedImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap scale = BitmapFactory.decodeStream(cameraInput);

                System.out.println(croppedImage);

                File delete = new File(croppedImage.getPath());
                if (delete.exists())
                    System.out.println("it exists!!!");
                delete.delete();

                scale = resizeImageToScreen(scale);

                OutputStream fOut = null;
                Locale mylocale = new Locale("en");
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", mylocale).format(new Date());
                storageDir = getAlbumStorageDir("ChromaVision");
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
                System.out.println(scale.getWidth() + " " + scale.getHeight());
                cropped.putExtra("width", scale.getWidth());
                cropped.putExtra("height", scale.getHeight());

                startActivity(cropped);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
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
                    System.out.println("Inside CAMERA_REQUEST_CODE");

                    System.out.println(mCameraImageUri);

                    CropImage.activity(mCameraImageUri).setGuidelines(CropImageView.Guidelines.ON).start(this);

                }
                else if (requestCode == GALLERY_REQUEST_CODE) {
                    System.out.println("Inside GALLERY_REQUEST_CODE");

                    // Get the image from data
                    Uri selectedImage = null;
                    if (data != null) {
                        selectedImage = data.getData();
                    }

                    System.out.println("selectedImage URI: " + selectedImage);

                    CropImage.activity(selectedImage).setGuidelines(CropImageView.Guidelines.ON).start(this);



                    InputStream galleryInput = null;
                    if (selectedImage != null) {
                        galleryInput = getContentResolver().openInputStream(selectedImage);
                    }

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
                    resultIntent.putExtra("width", scale.getWidth());
                    resultIntent.putExtra("height", scale.getHeight());
                    //startActivityForResult(resultIntent, GALLERY_REQUEST_CODE);
                }
                else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the image capture

                }
                else {
                    // Image capture failed, advise user

                }
            } catch (Exception e) {
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

        return Bitmap.createScaledBitmap(scale, (int) (width * scaleRatio), (int) (height * scaleRatio), true);
    }

    // Add the Photo to a Gallery
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        storageDir = getAlbumStorageDir("ChromaVision");
        Uri contentUri = Uri.fromFile(storageDir);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    public void tutorial(View view) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("ViewedTutorial", true);
        editor.apply();

        Intent Tutorial = new Intent(this, TutorialActivity.class);
        startActivity(Tutorial);

        Button tutorialButton = (Button)findViewById(R.id.button4);
        if (tutorialButton != null) {
            tutorialButton.setVisibility(View.GONE);
            mTutorialMenuItem.setChecked(false);
        }
    }

    public void openFolder(View view) {

        Intent intent = new Intent(this, FileListActivity.class);
        startActivity(intent);
    }


}

package cs371m.chromavision;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Welcome!");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage("Welcome to ChromaVision, an app intended to help those with color vision deficiency or those just wondering what colors compose something.\n\nThis app requires camera and storage permissions in order to work correctly. The camera permission is necessary to allow you to take pictures of objects you are around to evaluate their colors. The storage permission is necessary to load images you already have stored on your device and to store data that ChromaVision will generate.\n\nIf your device does not have a camera, you can still use the app by utilizing this method of loading images saved to your device as long as you accept that permission. Later revoking permissions from the app may result in erroneous behavior so do so at your own risk!");

        final SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.contains("FirstTime")){

            alertDialog.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("FirstTime", true);
                    editor.apply();
                    //more code....
                }
            });

            alertDialog.show();
        }
    }

    /* Called when the user clicks the 'click here to start' button. */
    public void startMainMenu(View view) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        //finish();
    }

}

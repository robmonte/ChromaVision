package cs371m.chromavision;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = "Splash Screen Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Welcome!");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setMessage("Welcome to ChromaVision, an app intended to help those with color vision deficiency or those just wondering what colors compose something.\n\nThis app requires camera and storage permissions in order to work correctly. The camera permission is necessary to allow you to take pictures of objects nearby to evaluate their colors. The storage permission is necessary to load images you already have stored on your device and to store data that ChromaVision will generate.\n\nIf your device does not have a camera, you can still use the app by utilizing this method of loading images saved to your device as long as you accept that permission. If you later decide to revoke permissions from the app, it may result in erroneous behavior so do so at your own risk!\n\nIf this is your first time using the app, why don't you check out the Tutorial on the next page by tapping on the \"Tutorial\" button!");

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(TAG, "Prefs contains ViewedWelcome if true: " + prefs.contains("ViewedWelcome"));
        Log.d(TAG, "Prefs bool ViewedWelcome is : " + prefs.getBoolean("ViewedWelcome", false));
        if(!prefs.contains("ViewedWelcome") || (prefs.contains("ViewedWelcome") && !prefs.getBoolean("ViewedWelcome", false))) {

            alertDialog.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("ViewedWelcome", true);
                    if (!prefs.contains("ViewedTutorial"))
                        editor.putBoolean("ViewedTutorial", false);
                    if (!prefs.contains("PreciseMode"))
                        editor.putBoolean("PreciseMode", true);

                    editor.apply();
                }
            });

                alertDialog.show();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    /* Called when the user clicks the 'click here to start' button. */
    public void startMainMenu(View view) {
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
        //finish();
    }

}

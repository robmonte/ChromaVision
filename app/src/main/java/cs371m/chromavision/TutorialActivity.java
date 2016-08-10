package cs371m.chromavision;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class TutorialActivity extends AppCompatActivity {

    private final String TAG = "Tutorial Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

//        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putBoolean("ViewedTutorial", false);
//        editor.apply();

//        Log.d(TAG, "ViewedTutorial is now " + prefs.getBoolean("ViewedTutorial", false));
    }
}

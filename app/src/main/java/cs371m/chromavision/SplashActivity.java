package cs371m.chromavision;

import android.content.Intent;
import android.provider.MediaStore;
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
    }

    /* Called when the user clicks the 'click here to start' button. */
    public void startMainMenu(View view) {

        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }

}

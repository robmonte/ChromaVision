package cs371m.chromavision;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by niuyajie on 8/10/16.
 */
public class FileListActivity extends AppCompatActivity{

    private static final String TAG = "FileList";

    private ListView listview;
    private ArrayList<String> files;
    private ArrayAdapter<String> adapter;
    private File file[];
    private TextView content;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_file_list);
//        Toolbarbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        System.out.println("hey are you okay?\n");

        files = new ArrayList<>();
        getFileList();
        listview = (ListView) findViewById(R.id.list);
//        content = (TextView) findViewById(R.id.FileTextView);
        ArrayAdapter adapter
                = new ArrayAdapter<>(this, R.layout.list_item, android.R.id.text1, files);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // ListView Clicked item index
                        int itemPosition = position;

                        // ListView Clicked item value
                        String itemValue = (String) listview.getItemAtPosition(position);
                        System.out.println("click: Postion: " + itemPosition + ", ListItem: " + itemValue);
                        Toast.makeText(getApplicationContext(),
                                "click: Postion: " + itemPosition +
                                        ", ListItem: " + itemValue, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void getFileList() {
        String path = getFilesDir().toString();
        Log.d("Files", "Path: " + path);
        File f = new File(path);
        file = f.listFiles();
        Log.d("Files", "Size: " + file.length);
        for (int i = 0; i < file.length; i++) {
            Log.d("Files", "FileName:" + file[i].getName());
            files.add(file[i].getName());
        }
    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_main, menu);
//        return true;
//    }
}
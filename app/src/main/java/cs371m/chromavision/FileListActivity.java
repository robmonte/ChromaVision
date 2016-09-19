package cs371m.chromavision;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

    public int opened;
    public static String itemValue;
    public String toPictureName;
    public String toDataName;
    public EditText input;
    public File fromPicture;
    public File fromData;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_file_list);

        itemValue = null;

        files = new ArrayList<>();
        getFileList();
        listview = (ListView) findViewById(R.id.list);
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.list_item, android.R.id.text1, files);
        listview.setAdapter(adapter);

        listview.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                        int itemPosition = position;
                        view.setSelected(true);
                        itemValue = (String) listview.getItemAtPosition(position);
                        Uri uri = Uri.fromFile(new File(getFilesDir().toString() + "/" + itemValue));
                        Log.d(TAG, "longclick: Position: " + itemPosition + ", ListItem: " + itemValue + ", uri: " + uri.toString());

                        renameFile(uri);
                        return true;
                    }
                });

        listview.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // ListView Clicked item index
                        int itemPosition = position;
                        view.setSelected(true);
                        // ListView Clicked item value
                        itemValue = (String) listview.getItemAtPosition(position);
                        Log.d(TAG, "click: Position: " + itemPosition + ", ListItem: " + itemValue);
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
            String str = file[i].getName();
            if (str.substring(str.length()-4).equals(".jpg"))
                files.add(file[i].getName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_file_list, menu);

        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1)
            finish();
    }

    public void renameFile(Uri uri) {
        fromPicture = new File(uri.getPath());
        fromData = new File(uri.getPath() + ".chroma");


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        input = new EditText(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setMessage("Please set a file name:")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        String fileName;
                        Toast.makeText(getApplicationContext(),
                                "Renamed!",Toast.LENGTH_LONG).show();
                        input.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        fileName = input.getText().toString();
                        int length = fileName.length();
                        while (fileName.charAt(fileName.length()-1) == ' ')
                            fileName = fileName.substring(0, fileName.length()-1);

                        if(fileName.length() > 4) {
                            String end = fileName.substring(length - 4);

                            if (!end.equals(".jpg"))
                                fileName += ".jpg";
                        }
                        else
                            fileName += ".jpg";

                        toPictureName = fileName;
                        toDataName = fileName + ".chroma";

                        File toPicture = new File(getFilesDir().toString() + "/" + toPictureName);
                        File toData = new File(getFilesDir().toString() + "/" + toDataName);

                        fromPicture.renameTo(toPicture);
                        fromData.renameTo(toData);

                        finish();
                        startActivity(getIntent());
                    }
                })
                .setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {

                        // User cancelled the dialog
                        Toast.makeText(getApplicationContext(),
                                "Canceled!",Toast.LENGTH_LONG).show();
                    }
                });

        // Create the AlertDialog object and return it
        AlertDialog fname = builder.create();
        input = new EditText(this);
        fname.setView(input);
        fname.show();

    }

    public void readFile(View view) {
        if (itemValue == null) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setIcon(R.mipmap.ic_launcher);
            alertDialog.setMessage("Please choose a file!")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            alertDialog.show();
        }
        else  {
            Intent intent = new Intent(this, LoadResultActivity.class);
            startActivityForResult(intent, 1);
        }
    }


}
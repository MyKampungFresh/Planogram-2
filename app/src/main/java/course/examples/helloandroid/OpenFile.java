package course.examples.helloandroid;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/*
 * From Rou1997
 * http://www.codeproject.com/Tips/1019055/Open-File-Dialog-Save-File-Dialog
 *
 */

public class OpenFile extends Activity
        implements OnClickListener, OnItemClickListener {

    ListView LvList;

    ArrayList<String> listItems = new ArrayList<String>();

    ArrayAdapter<String> adapter;

    Button BtnOK;
    Button BtnCancel;

    String currentPath = null;

    String selectedFilePath = null; /* Full path, i.e. /mnt/sdcard/folder/file.txt */
    String selectedFileName = null; /* File Name Only, i.e file.txt */

    private Context context;
    private Activity activity;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_plano);

        context = getApplicationContext();
        activity = this;

        //requestPermission();

        try {
            /* Initializing Widgets */
            LvList = (ListView) findViewById(R.id.LvList);
            BtnOK = (Button) findViewById(R.id.BtnOK);
            BtnCancel = (Button) findViewById(R.id.BtnCancel);

            /* Initializing Event Handlers */

            LvList.setOnItemClickListener(this);

            BtnOK.setOnClickListener(this);
            BtnCancel.setOnClickListener(this);

            //setCurrentPath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
            setCurrentPath("/");

        } catch (Exception ex) {
            Toast.makeText(this,
                    "Error in OpenFileActivity.onCreate: " + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    void setCurrentPath(String path) {
        ArrayList<String> folders = new ArrayList<String>();

        ArrayList<String> files = new ArrayList<String>();

        currentPath = path;

        File[] allEntries = new File(path).listFiles();

        for (int i = 0; i < allEntries.length; i++) {
            if (allEntries[i].isDirectory()) {
                folders.add(allEntries[i].getName());
            } else if (allEntries[i].isFile()) {
                files.add(allEntries[i].getName());
            }
        }

        Collections.sort(folders, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        Collections.sort(files, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });

        listItems.clear();

        for (int i = 0; i < folders.size(); i++) {
            listItems.add(folders.get(i) + "/");
        }

        for (int i = 0; i < files.size(); i++) {
            listItems.add(files.get(i));
        }

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        adapter.notifyDataSetChanged();

        LvList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        if (!currentPath.equals(Environment.getExternalStorageDirectory().getAbsolutePath() + "/")) {
            setCurrentPath(new File(currentPath).getParent() + "/");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.BtnOK:

                intent = new Intent(OpenFile.this, MainActivity.class);
                intent.putExtra("fileName", selectedFilePath);
                intent.putExtra("shortFileName", selectedFileName);
                setResult(RESULT_OK, intent);

                OpenFile.this.startActivity(intent);

                this.finish();

                break;
            case R.id.BtnCancel:

                intent = new Intent(OpenFile.this, MainActivity.class);
                intent.putExtra("fileName", "");
                intent.putExtra("shortFileName", "");
                setResult(RESULT_CANCELED, intent);

                OpenFile.this.startActivity(intent);

                this.finish();

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        String entryName = (String)parent.getItemAtPosition(position);
        if (entryName.endsWith("/")) {
            int permissionCheck = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                requestPermission();
            }
            else {
                setCurrentPath(currentPath + entryName);
            }

            //setCurrentPath(currentPath + entryName);
        } else {
            selectedFilePath = currentPath + entryName;

            selectedFileName = entryName;

            /*this.setTitle(this.getResources().getString(R.string.title_activity_open_file)
                    + "[" + entryName + "]");*/
            this.setTitle("Open planogram" + "[" + entryName + "]");
        }
    }

    private void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.READ_EXTERNAL_STORAGE)){

            Toast.makeText(context,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(context,"Permission Granted",Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(context,"Permission Denied",Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
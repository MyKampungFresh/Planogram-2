package course.examples.helloandroid;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Vector;

public class OpenRecent extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_recent);

        String destPath = getFilesDir().getPath();
        destPath = destPath.substring(0, destPath.lastIndexOf("/")) + "/databases";

        Log.d("Files", "Path: " + destPath);
        File f = new File(destPath);
        File file[] = f.listFiles();
        Vector<String> v = new Vector<String>();
        for (int i = 0; i < file.length; i++) {
            String bla = file[i].getName();
            if(!bla.contains("journal")) {
                v.add(bla);
            }
        }

        String a[] = {};
        v.toArray(a);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_open_recent_listview, a);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "Click ListItem " + parent.getItemAtPosition(position), Toast.LENGTH_LONG).show();
            }
        });
    }

}

package course.examples.helloandroid;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

    private static final String EXCLUDES_FILES = "journal";

    private ListView mDBFileListView;
    private String mDatabasePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_recent);

        showFileList();

        mDBFileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(OpenRecent.this, MainActivity.class);
                intent.putExtra("isANewPlano", false);
                intent.putExtra("dbFileName", parent.getItemAtPosition(position).toString());
                intent.putExtra("dbPath", mDatabasePath);
                setResult(RESULT_OK, intent);

                OpenRecent.this.startActivity(intent);
            }
        });
    }

    private void showFileList() {
        mDatabasePath = getFilesDir().getPath();
        mDatabasePath = mDatabasePath.substring(0, mDatabasePath.lastIndexOf("/")) + "/databases/";

        File dbFolder = new File(mDatabasePath);
        File file[] = dbFolder.listFiles();
        ArrayList<String> fileList = new ArrayList<>();
        for (File aFile : file) {
            String currentFile = aFile.getName();
            if (!currentFile.contains(EXCLUDES_FILES)) {
                fileList.add(currentFile);
            }
        }

        String[] arrayFileList = fileList.toArray(new String[fileList.size()]);

        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.activity_open_recent_listview, arrayFileList);

        mDBFileListView = (ListView) findViewById(R.id.listView);
        mDBFileListView.setAdapter(adapter);
    }

}

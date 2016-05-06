package course.examples.helloandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void openPlano(View v) {
        Intent intent = new Intent(StartActivity.this, OpenFile.class);
        StartActivity.this.startActivity(intent);
    }
}

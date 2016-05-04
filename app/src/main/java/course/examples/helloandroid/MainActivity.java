package course.examples.helloandroid;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends Activity {

    private int loc = 1;
    private int totalNbProd = 88;

    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = getResources();

        Button btnExp = (Button)findViewById(R.id.btnExpiration);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String date = extras.getString("date");
            int nbExp = extras.getInt("nbExp");
            int nbTotal = extras.getInt("nbTotal");

            String text = res.getString(R.string.setExpiration, nbExp, nbTotal, date);
            btnExp.setText(text);
        }

        FrameLayout frameBarcode = (FrameLayout) findViewById(R.id.frameBarcode);
        DrawBarcode dv = new DrawBarcode(this);
        frameBarcode.addView(dv);

        final View workView = findViewById(R.id.workView);
        workView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                nextProduct(workView);
            }

            @Override
            public void onSwipeRight() {
                prevProduct(workView);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void nextProduct(View v) {

        if(loc < totalNbProd)
            loc++;

        String text = res.getString(R.string.setLoc, loc, totalNbProd);
        ((TextView)findViewById(R.id.loc)).setText(text);
    }

    public void prevProduct(View v) {

        if(loc > 1)
            loc = loc - 1;

        String text = res.getString(R.string.setLoc, loc, totalNbProd);
        ((TextView)findViewById(R.id.loc)).setText(text);
    }

    public void setExpiration(View v) {
        Intent intent = new Intent(MainActivity.this, setExpirationActivity.class);
        MainActivity.this.startActivity(intent);
    }

}

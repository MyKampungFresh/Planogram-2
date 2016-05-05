package course.examples.helloandroid;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

        refreshView();

        // Swipe gesture
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

    public void setExpiration(View v) {
        Intent intent = new Intent(MainActivity.this, setExpirationActivity.class);
        MainActivity.this.startActivity(intent);
    }

    private void refreshView(){

        // Description
        TextView prodDesc = (TextView)findViewById(R.id.prodDescription);
        String txtProdDesc = res.getString(R.string.desc, "JAMIESON MULTI COMPL.VIT.ADLT");
        prodDesc.setText(txtProdDesc);

        // Format
        TextView prodFormat = (TextView)findViewById(R.id.prodFormat);
        String txtProdFormat = res.getString(R.string.format, "1 X 90 CAPL");
        prodFormat.setText(txtProdFormat);

        // Draw barcode
        FrameLayout frameBarcode = (FrameLayout) findViewById(R.id.frameBarcode);
        DrawBarcode dv = new DrawBarcode(this);
        frameBarcode.addView(dv);

        // Show barcode data
        TextView UPC = (TextView)findViewById(R.id.UPC);
        String txtUPC = res.getString(R.string.upc, "799366629702");
        UPC.setText(txtUPC);

        // Facing
        TextView nbFacing = (TextView)findViewById(R.id.nbFacing);
        String txtNbFacing = res.getString(R.string.nbFacing, 1);
        nbFacing.setText(txtNbFacing);

        // Expiration
        Button btnExp = (Button)findViewById(R.id.btnExpiration);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String date = extras.getString("date");
            int nbExp = extras.getInt("nbExp");
            int nbTotal = extras.getInt("nbTotal");

            String text = res.getString(R.string.setExpiration, nbExp, nbTotal, date);
            btnExp.setText(text);
        }

        // Loc
        String txtLoc = res.getString(R.string.setLoc, loc, totalNbProd);
        ((TextView)findViewById(R.id.loc)).setText(txtLoc);

        // Shelf height
        String txtShelfHeight = res.getString(R.string.shelfHeight, 7);
        ((TextView)findViewById(R.id.shelfHeight)).setText(txtShelfHeight);

    }

    private void nextProduct(View v) {

        if(loc < totalNbProd)
            loc++;

        String text = res.getString(R.string.setLoc, loc, totalNbProd);
        ((TextView)findViewById(R.id.loc)).setText(text);
    }

    private void prevProduct(View v) {

        if(loc > 1)
            loc = loc - 1;

        String text = res.getString(R.string.setLoc, loc, totalNbProd);
        ((TextView)findViewById(R.id.loc)).setText(text);
    }

}

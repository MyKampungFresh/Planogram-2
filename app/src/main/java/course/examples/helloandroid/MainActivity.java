package course.examples.helloandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {

    private int loc = 1;
    private int totalNbProd = 0;

    private Resources res;

    private Planogram plano;

    private Expiration mExpiration;

    File pdfFile = null;

    ProgressDialog progress;

    View workView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String fileName;
        String shortFileName;

        res = getResources();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileName = extras.getString("fileName");
            shortFileName = extras.getString("shortFileName");

            progress = ProgressDialog.show(this, getString(R.string.openPlanoMsg) + " " + shortFileName,
                    getString(R.string.progressMsg), true);

            if(shortFileName != null) {
                pdfFile = new File(fileName);

                new Thread(new Runnable() {
                    @Override
                    public void run()
                    {
                        // do the thing that takes a long time
                        plano = new Planogram(pdfFile);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run()
                            {
                                progress.dismiss();
                                View view = findViewById(R.id.workView);
                                view.setVisibility(View.VISIBLE);
                                refreshView();
                            }
                        });
                    }
                }).start();
            }
        }

        // Swipe gesture
        workView = findViewById(R.id.workView);
        workView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                nextProduct(workView);
            }

            @Override
            public void onSwipeRight() {
                prevProduct(workView);
            }

            @Override
            public boolean onSingleTap(MotionEvent event) {

                doneProduct(workView);
                return true;
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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set expiration");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String expCode;
                expCode = input.getText().toString();

                mExpiration = new Expiration(expCode);

                Button btnExp = (Button) findViewById(R.id.btnExpiration);
                if(mExpiration.isValid()) {
                    String text = res.getString(R.string.setExpiration,
                            mExpiration.getNbExpiring(),
                            mExpiration.getNbTotal(),
                            mExpiration.getDateStr());
                    btnExp.setText(text);

                    plano.setExpirationAtPos(loc, mExpiration);
                } else {
                    btnExp.setText(R.string.expValidityNotice);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void refreshView(){

        int nbExp = 0;
        int nbTotal = 0;
        String date = null;
        String expCode = null;

        String txtProdDesc;
        String txtProdFormat;
        String txtUPC;
        String txtNbFacing;
        String txtLoc;
        String txtShelfHeight;

        Expiration exp;
        Product currentProd;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            date = extras.getString("date");
            nbExp = extras.getInt("nbExp");
            nbTotal = extras.getInt("nbTotal");
            expCode = extras.getString("expCode");
        }

        if (plano != null) {

            currentProd = plano.getProduct(loc);

            totalNbProd = plano.getNbProducts();

            // Description
            TextView prodDesc = (TextView) findViewById(R.id.prodDescription);
            txtProdDesc = res.getString(R.string.desc, currentProd.getDesc());
            prodDesc.setText(txtProdDesc);

            // Format
            TextView prodFormat = (TextView) findViewById(R.id.prodFormat);
            txtProdFormat = res.getString(R.string.format, currentProd.getFormat());
            prodFormat.setText(txtProdFormat);

            // Draw barcode
            FrameLayout frameBarcode = (FrameLayout) findViewById(R.id.frameBarcode);
            Barcode dv = new Barcode(this, currentProd.getUpc());
            frameBarcode.addView(dv);

            // Show barcode data
            TextView UPC = (TextView) findViewById(R.id.UPC);
            txtUPC = res.getString(R.string.upc, currentProd.getUpc());
            UPC.setText(txtUPC);

            // Facing
            TextView nbFacing = (TextView) findViewById(R.id.nbFacing);
            txtNbFacing = res.getString(R.string.nbFacing, currentProd.getNbFacing());
            nbFacing.setText(txtNbFacing);

            // Expiration
            Button btnExp = (Button) findViewById(R.id.btnExpiration);
            if (date != null) {
                String text = res.getString(R.string.setExpiration, nbExp, nbTotal, date);
                btnExp.setText(text);

                // This is stupid
                exp = new Expiration(expCode);

                plano.setExpirationAtPos(loc, exp);
            }

            // Loc
            txtLoc = res.getString(R.string.setLoc, loc, totalNbProd);
            ((TextView) findViewById(R.id.loc)).setText(txtLoc);

            // Shelf height
            txtShelfHeight = res.getString(R.string.shelfHeight, currentProd.getShelfHeight());
            ((TextView) findViewById(R.id.shelfHeight)).setText(txtShelfHeight);
        }

    }

    private void nextProduct(View v) {

        if(loc < totalNbProd)
            loc++;

        refreshView();
    }

    private void prevProduct(View v) {

        if(loc > 1)
            loc = loc - 1;

        refreshView();
    }

    private void doneProduct(View v) {

        v.setBackgroundColor(Color.GREEN);
        nextProduct(v);
    }

}

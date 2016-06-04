package course.examples.helloandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends Activity {

    private int pos = 0;
    private int totalNbProd = 0;

    private Resources res;

    private Planogram plano;
    private String mDBFilename;

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
        workView = findViewById(R.id.workView);

        CacheFileHandler planoProgress = new CacheFileHandler(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileName = extras.getString("fileName");
            shortFileName = extras.getString("shortFileName");

            if(extras.getBoolean("isANewPlano")) {

                progress = ProgressDialog.show(this, getString(R.string.openPlanoMsg) + " " + shortFileName,
                        getString(R.string.progressMsg), true);

                pdfFile = new File(fileName);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        plano = new Planogram(pdfFile);

                        // First save
                        save();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.dismiss();
                                workView.setVisibility(View.VISIBLE);
                                refreshView();
                            }
                        });
                    }
                }).start();
            } else {
                Log.d("onCreateMain","Open recent file: " + extras.getString("dbFileName"));
                plano = new Planogram();
                plano.open(this,extras.getString("dbFileName"),extras.getString("dbPath"));
                mDBFilename = extras.getString("dbFileName");

                workView.setVisibility(View.VISIBLE);
                refreshView();
            }
        }


        // Swipe gesture
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

                FrameLayout frameProdDesc = (FrameLayout) findViewById(R.id.frameProdDesc);
                doneProduct(frameProdDesc);
                return true;
            }
        });

        addListenerProductIsNew();

        // Seekbar
        SeekBar seekPos = (SeekBar) findViewById(R.id.seekPos);
        seekPos.setMax(totalNbProd - 1);

        seekPos.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int positionSought = 0;
            String txtLoc;
            String txtShelf;

            Product currentProd;

            public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser){
                positionSought = position + 1;
                currentProd = plano.getProduct(position);

                txtLoc = res.getString(R.string.setLoc, positionSought, totalNbProd);
                ((TextView) findViewById(R.id.loc)).setText(txtLoc);

                txtShelf = currentProd.getShelfNb() + " - " +
                        res.getString(R.string.shelf, currentProd.getShelfHeight());
                ((TextView) findViewById(R.id.shelf)).setText(txtShelf);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                pos = positionSought - 1;
                refreshView();
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

        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_about) {
            showAboutDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addListenerProductIsNew() {

        CheckBox chkNewProd = (CheckBox) findViewById(R.id.newProd);

        chkNewProd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (((CheckBox) v).isChecked()) {
                    plano.productIsNew(pos);
                }
            }
        });
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

                Expiration exp = new Expiration(expCode);

                Button btnExp = (Button) findViewById(R.id.btnExpiration);
                if(exp.isValid()) {
                    String text = res.getString(R.string.setExpiration,
                            exp.getNbExpiring(),
                            exp.getNbTotal(),
                            exp.getDateStr());
                    btnExp.setText(text);

                    plano.setExpirationAtPos(pos, exp);
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

    public void find(View v) {

        Intent intent = new Intent(MainActivity.this, FindActivity.class);
        intent.putExtra("dbName",mDBFilename);
        MainActivity.this.startActivity(intent);
    }

    private void refreshView(){

        String txtProdDesc;
        String txtProdFormat;
        String txtUPC;
        String txtNbFacing;
        String txtLoc;
        String txtShelfHeight;

        Product currentProd;

        currentProd = plano.getProduct(pos);

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
        if (plano.getProduct(pos).isExpired()) {
            Expiration exp = plano.getProduct(pos).getExpiration();
            String text = res.getString(R.string.setExpiration,
                        exp.getNbExpiring(),
                        exp.getNbTotal(),
                        exp.getDateStr());
            btnExp.setText(text);
        } else {
            btnExp.setText(res.getString(R.string.expiration));
        }

        // Pos
        txtLoc = res.getString(R.string.setLoc, pos + 1, totalNbProd);
        ((TextView) findViewById(R.id.loc)).setText(txtLoc);

        // Shelf height
        txtShelfHeight = currentProd.getShelfNb() + " - " +
                res.getString(R.string.shelf, currentProd.getShelfHeight());
        ((TextView) findViewById(R.id.shelf)).setText(txtShelfHeight);

        // Is product has been placed?
        FrameLayout frameProdDesc = (FrameLayout) findViewById(R.id.frameProdDesc);
        ImageView imgCheck = (ImageView) findViewById(R.id.imgCheck);
        if (plano.isProductPlaced(pos)) {
            frameProdDesc.setBackgroundColor(0xFF008000);
            imgCheck.setVisibility(View.VISIBLE);
        } else {
            frameProdDesc.setBackgroundColor(Color.TRANSPARENT);
            imgCheck.setVisibility(View.INVISIBLE);
        }

        // Is product new?
        CheckBox chkNewProd = (CheckBox) findViewById(R.id.newProd);
        if (plano.isProductNew(pos)) {
            chkNewProd.setChecked(true);
        } else {
            chkNewProd.setChecked(false);
        }
    }

    private void nextProduct(View v) {

        if(pos + 1 < totalNbProd)
            pos++;

        refreshView();
    }

    private void prevProduct(View v) {

        if(pos >= 1)
            pos = pos - 1;

        refreshView();
    }

    private void doneProduct(View v) {

        plano.productIsPlaced(pos);
        v.setBackgroundColor(0xFF008000);

        ImageView imgCheck = (ImageView) findViewById(R.id.imgCheck);
        imgCheck.setVisibility(View.VISIBLE);
    }

    private void showAboutDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About Planogram");

        final TextView about = new TextView(this);
        about.setText(R.string.about);
        builder.setView(about);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void save() {

        mDBFilename = Planogram.COMP_ACRONYM + "_"
                + plano.getDepartmentNameNorm() + "_"
                + plano.getPlanoLength() + "_"
                + plano.getPlanoCreationShortDate();
        plano.save(this,mDBFilename);
    }

}

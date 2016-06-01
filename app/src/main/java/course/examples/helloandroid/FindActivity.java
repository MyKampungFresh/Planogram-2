package course.examples.helloandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//TODO: MainActivity uses Planogram class to access the database via ProductDBHandler. Find a way to do the same!

public class FindActivity extends Activity implements AdapterView.OnItemSelectedListener{

    ProductDBHandler mProductDB;
    TableLayout tlSearchResults;
    String searchOperator;
    String searchColumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        tlSearchResults = (TableLayout) findViewById(R.id.tblResults);

        String dbFilename = "";

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            dbFilename = extras.getString("dbName");
        }

        String dbPath = getFilesDir().getPath();
        dbPath = dbPath.substring(0, dbPath.lastIndexOf("/")) + "/databases/";

        mProductDB = new ProductDBHandler(this,dbFilename);
        mProductDB.open(dbPath);

        showSpinner();
    }

    private void showSpinner() {
        // Spinner element
        Spinner spnColumn = (Spinner) findViewById(R.id.spnColumn);

        // Spinner click listener
        assert spnColumn != null;
        spnColumn.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> columns = new ArrayList<>();
        columns.add("Position");
        columns.add("McKesson Id");
        columns.add("UPC");
        columns.add("Description");

        // Creating adapter for spinner
        ArrayAdapter<String> adptColumns = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, columns);

        // Drop down layout style - list view with radio button
        adptColumns.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnColumn.setAdapter(adptColumns);

        // Spinner element
        Spinner spnOperator = (Spinner) findViewById(R.id.spnOperator);

        // Spinner click listener
        assert spnOperator != null;
        spnOperator.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> operators = new ArrayList<>();
        operators.add("=");
        operators.add("<");
        operators.add(">");
        operators.add("contains");

        // Creating adapter for spinner
        ArrayAdapter<String> adptOperators = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, operators);

        // Drop down layout style - list view with radio button
        adptOperators.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnOperator.setAdapter(adptOperators);
    }

    private void refreshTable(List<Product> products) {

        int childCount = tlSearchResults.getChildCount();
        if (childCount > 0)
            tlSearchResults.removeViews(0, childCount);

        int bla = (int) getResources().getDimension(R.dimen.tblTitles_pos_col);
        Log.d("refreshTable","bla = " + bla);
        Log.d("refreshTable","dpToPixels = " + dpToPixels(25));

        if (products.get(0) != null) {
            int i = 0;

            for (Product product : products) {
                TableRow row = new TableRow(this);

                int pos = product.getPos();

                TextView txtvPos = new TextView(this);
                TextView txtvMcKId = new TextView(this);
                TextView txtvUpc = new TextView(this);
                TextView txtvDesc = new TextView(this);
                TextView txtvFormat = new TextView(this);
                TextView txtvNbFacing = new TextView(this);
                TextView txtvIsNewProd = new TextView(this);
                TextView txtvIsPlaced = new TextView(this);
                TextView txtvExpiration = new TextView(this);

                txtvPos.setText(String.valueOf(pos + 1));
                txtvPos.setGravity(Gravity.CENTER);
                txtvPos.setWidth((int) getResources().getDimension(R.dimen.tblTitles_pos_col));
                txtvMcKId.setText(product.getIdNb());
                txtvMcKId.setGravity(Gravity.CENTER);
                txtvMcKId.setWidth((int) getResources().getDimension(R.dimen.tblTitles_McKId_col));
                txtvUpc.setText(product.getUpc());
                txtvUpc.setGravity(Gravity.CENTER);
                txtvUpc.setWidth((int) getResources().getDimension(R.dimen.tblTitles_upc_col));
                txtvDesc.setText(product.getDesc());
                txtvDesc.setGravity(Gravity.CENTER);
                txtvDesc.setWidth((int) getResources().getDimension(R.dimen.tblTitles_desc_col));
                txtvFormat.setText(product.getFormat());
                txtvFormat.setGravity(Gravity.CENTER);
                txtvFormat.setWidth((int) getResources().getDimension(R.dimen.tblTitles_format_col));
                txtvNbFacing.setText(String.valueOf(product.getNbFacing()));
                txtvNbFacing.setGravity(Gravity.CENTER);
                txtvNbFacing.setWidth((int) getResources().getDimension(R.dimen.tblTitles_nbFacing_col));

                if(product.isNew())
                    txtvIsNewProd.setText("Yes");
                else
                    txtvIsNewProd.setText("No");
                txtvIsNewProd.setGravity(Gravity.CENTER);
                txtvIsNewProd.setWidth((int) getResources().getDimension(R.dimen.tblTitles_newProd_col));

                if(product.isPlaced())
                    txtvIsPlaced.setText("Yes");
                else
                    txtvIsPlaced.setText("No");
                txtvIsPlaced.setGravity(Gravity.CENTER);
                txtvIsPlaced.setWidth((int) getResources().getDimension(R.dimen.tblTitles_isProdPlaced_col));

                if(product.isExpired())
                    txtvExpiration.setText(product.getExpiration().getExpCode());
                txtvExpiration.setGravity(Gravity.CENTER);
                txtvExpiration.setWidth((int) getResources().getDimension(R.dimen.tblTitles_expiration_col));

                row.addView(txtvPos);
                row.addView(txtvMcKId);
                row.addView(txtvUpc);
                row.addView(txtvDesc);
                row.addView(txtvFormat);
                row.addView(txtvNbFacing);
                row.addView(txtvIsNewProd);
                row.addView(txtvIsPlaced);
                row.addView(txtvExpiration);

                tlSearchResults.addView(row, i);

                i++;
            }
        }
    }

    public void findProduct(View v) {
        EditText etxtValues = (EditText) findViewById(R.id.editText);

        String value = etxtValues.getText().toString();

        Log.d("findProduct","Search string : " + searchColumn
                + " " + searchOperator + " " + value);

        List<Product> products = mProductDB.findProduct(searchColumn, searchOperator,value);
        refreshTable(products);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Spinner spinner = (Spinner) parent;
        if(spinner.getId() == R.id.spnColumn) {
            searchColumn = parent.getItemAtPosition(position).toString();
        } else if(spinner.getId() == R.id.spnOperator) {
            searchOperator = parent.getItemAtPosition(position).toString();
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void sort(View v) {

        String direction = "ASC";
        String column;

        switch(v.getId()) {
            case R.id.colPos:
                column = ProductDBHandler.KEY_POS;
                break;
            case R.id.colMcKesson:
                column = ProductDBHandler.KEY_IDNB;
                break;
            case R.id.colUPC:
                column = ProductDBHandler.KEY_UPC;
                break;
            case R.id.colDesc:
                column = ProductDBHandler.KEY_DESC;
                break;
            case R.id.colFormat:
                column = ProductDBHandler.KEY_FORMAT;
                break;
            case R.id.colNbFacing:
                column = ProductDBHandler.KEY_NBFACING;
                break;
            default:
                throw new RuntimeException("Unknow button ID");
        }

        mProductDB.sort(column,direction);

        Log.d("sort", column + " " + direction);
    }

    public void clearTable(View v) {

        int childCount = tlSearchResults.getChildCount();
        if (childCount > 1)
            tlSearchResults.removeViews(0, childCount);
    }

    private int dpToPixels(int dp) {

        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}

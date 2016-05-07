package course.examples.helloandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class setExpirationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_expiration);

    }

    public void pressOkBtn(View v) {
        String expCode;

        Intent intent = new Intent(setExpirationActivity.this, MainActivity.class);

        EditText etxtExpCode = (EditText)findViewById(R.id.expCode);
        expCode = etxtExpCode.getText().toString();

        TextView expValidityNotice = (TextView)findViewById(R.id.expValidityNotice);

        Expiration exp = new Expiration(expCode);

        if(exp.isValid()) {
            intent.putExtra("date",exp.getDateStr());
            intent.putExtra("nbExp",exp.getNbExpiring());
            intent.putExtra("nbTotal",exp.getNbTotal());
            intent.putExtra("expCode",expCode);

            expValidityNotice.setEnabled(false);

            setExpirationActivity.this.startActivity(intent);

            this.finish();
        }
        else {
            expValidityNotice.setEnabled(true);
            expValidityNotice.setText(R.string.expValidityNotice);
        }
    }

    public void pressCancelBtn(View v) {
        Intent intent = new Intent(setExpirationActivity.this, MainActivity.class);
        setExpirationActivity.this.startActivity(intent);

        this.finish();
    }
}

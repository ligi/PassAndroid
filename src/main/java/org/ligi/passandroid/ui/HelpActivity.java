package org.ligi.passandroid.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import org.ligi.passandroid.R;

public class HelpActivity extends AppCompatActivity {

    @InjectView(R.id.help_tv)
    TextView helpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.inject(this);

        helpTextView.setText(Html.fromHtml(getString(R.string.help_content)));

        Linkify.addLinks(helpTextView, Linkify.ALL);

        helpTextView.setMovementMethod(LinkMovementMethod.getInstance());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}

package org.ligi.ticketviewer.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.util.Linkify;
import android.view.MenuItem;
import android.widget.TextView;

import org.ligi.ticketviewer.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HelpActivity extends ActionBarActivity {

    @InjectView(R.id.help_tv)
    TextView helpTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.inject(this);

        helpTextView.setText(Html.fromHtml(getString(R.string.help_content)));

        Linkify.addLinks(helpTextView, Linkify.ALL);

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

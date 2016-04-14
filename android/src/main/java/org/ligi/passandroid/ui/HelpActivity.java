package org.ligi.passandroid.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import org.ligi.passandroid.R;
import org.xml.sax.XMLReader;

public class HelpActivity extends AppCompatActivity {

    @Bind(R.id.help_text)
    TextView helpText;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.bind(this);

        final Spanned html = Html.fromHtml(getString(R.string.help_content), null, new ListTagHandler());

        helpText.setText(html);
        helpText.setMovementMethod(new LinkMovementMethod());

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    class ListTagHandler implements Html.TagHandler {

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            if (tag.equalsIgnoreCase("li")) {
                output.append(opening ? "\u2022 " : "\n");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}

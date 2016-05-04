package org.ligi.passandroid.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import java.util.UUID;
import javax.inject.Inject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.ligi.axt.simplifications.SimpleTextWatcher;
import org.ligi.passandroid.App;
import org.ligi.passandroid.R;
import org.ligi.passandroid.events.PassRefreshEvent;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.BarCode;
import org.ligi.passandroid.model.pass.Pass;
import org.ligi.passandroid.model.pass.PassBarCodeFormat;
import org.ligi.passandroid.model.pass.PassImpl;
import org.ligi.passandroid.ui.edit.BarcodePickDialog;
import org.ligi.passandroid.ui.edit.CategoryPickDialog;
import org.ligi.passandroid.ui.edit.FieldsEditFragment;
import org.ligi.passandroid.ui.edit.ImageEditHelper;
import org.ligi.passandroid.ui.pass_view_holder.EditViewHolder;

public class PassEditActivity extends AppCompatActivity {

    private PassImpl currentPass;
    private ImageEditHelper imageEditHelper;

    @Inject
    PassStore passStore;

    @Inject
    EventBus bus;

    @BindView(R.id.title)
    EditText titleEdit;

    @BindView(R.id.add_barcode_button)
    Button addBarcodeButton;

    private PassViewHelper passViewHelper;

    @OnClick(R.id.icon)
    void pickIcon() {
        imageEditHelper.startPick(ImageEditHelper.Companion.getREQ_CODE_PICK_ICON());
    }

    @OnClick(R.id.categoryView)
    void pickCategory() {
        CategoryPickDialog.show(bus, currentPass, this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        App.component().inject(this);
        setContentView(R.layout.edit);
        ButterKnife.bind(this);

        titleEdit.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(final Editable s) {
                super.afterTextChanged(s);
                currentPass.setDescription(s.toString());
            }
        });
        final Pass currentPass = passStore.getCurrentPass();
        if (currentPass != null) {
            this.currentPass = (PassImpl) currentPass;
        } else {
            finish();
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        imageEditHelper = new ImageEditHelper(this, passStore);

        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.container_for_primary_fields, FieldsEditFragment.create(false));
        fragmentTransaction.add(R.id.container_for_secondary_fields, FieldsEditFragment.create(true));

        fragmentTransaction.commit();

        passViewHelper = new PassViewHelper(this);

        addBarcodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                BarcodePickDialog.show(PassEditActivity.this,
                                       bus,
                                       PassEditActivity.this.currentPass,
                                       new BarCode(PassBarCodeFormat.QR_CODE, UUID.randomUUID().toString().toUpperCase()));
            }
        });
    }


    @Subscribe
    public void onPassRefresh(PassRefreshEvent event) {
        refresh(currentPass);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageEditHelper.onActivityResult(requestCode, resultCode, data);
    }

    private void refresh(Pass pass) {
        final EditViewHolder passViewHolder = new EditViewHolder(getWindow().getDecorView().findViewById(R.id.pass_card));

        passViewHolder.apply(pass, passStore, this);

        prepareImageUI(R.id.logo_img, R.id.add_logo, ImageEditHelper.Companion.getREQ_CODE_PICK_LOGO());
        prepareImageUI(R.id.strip_img, R.id.add_strip, ImageEditHelper.Companion.getREQ_CODE_PICK_STRIP());
        prepareImageUI(R.id.footer_img, R.id.add_footer, ImageEditHelper.Companion.getREQ_CODE_PICK_FOOTER());

        addBarcodeButton.setVisibility(pass.getBarCode() == null ? View.VISIBLE : View.GONE);
        final BarcodeUIController barcodeUIController = new BarcodeUIController(getWindow().getDecorView(), pass.getBarCode(), this, passViewHelper);
        barcodeUIController.getBarcode_img().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                BarcodePickDialog.show(PassEditActivity.this, bus, currentPass, currentPass.getBarCode());
            }
        });
    }

    @Pass.PassBitmap
    private void prepareImageUI(@IdRes final int logo_img, @IdRes final int add_logo, final int requestCode) {
        final String imageString = ImageEditHelper.Companion.getImageStringByRequestCode(requestCode);
        assert (imageString != null);
        final Bitmap bitmap = currentPass.getBitmap(passStore, imageString);

        final View addButton = findViewById(add_logo);
        assert (addButton != null);
        addButton.setVisibility(bitmap == null ? View.VISIBLE : View.GONE);

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                imageEditHelper.startPick(requestCode);
            }
        };

        final ImageView logoImage = (ImageView) findViewById(logo_img);
        assert (logoImage != null);
        passViewHelper.setBitmapSafe(logoImage, bitmap);
        logoImage.setOnClickListener(listener);
        addButton.setOnClickListener(listener);
    }


    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
        refresh(currentPass);
    }

    @Override
    protected void onPause() {
        bus.unregister(this);
        passStore.save(currentPass);
        passStore.notifyChange();
        super.onPause();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }


}

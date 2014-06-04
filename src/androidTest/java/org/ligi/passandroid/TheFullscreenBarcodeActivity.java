package org.ligi.passandroid;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.squareup.spoon.Spoon;

import org.ligi.passandroid.injections.FixedPassBook;
import org.ligi.passandroid.injections.FixedPassListPassStore;
import org.ligi.passandroid.model.Passbook;
import org.ligi.passandroid.ui.FullscreenBarcodeActivity;
import org.ligi.tracedroid.TraceDroid;

import java.util.ArrayList;

import butterknife.ButterKnife;

import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.isDisplayed;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static org.fest.assertions.api.Assertions.assertThat;

public class TheFullscreenBarcodeActivity extends ActivityInstrumentationTestCase2<FullscreenBarcodeActivity> {

    public TheFullscreenBarcodeActivity() {
        super(FullscreenBarcodeActivity.class);
    }

    public static String readQRImage(Bitmap bMap) {
        String contents = null;

        int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Reader reader = new MultiFormatReader();// use this otherwise ChecksumException
        try {
            Result result = reader.decode(bitmap);
            contents = result.getText();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return contents;
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();

        final ArrayList<Passbook> list = new ArrayList<Passbook>() {{
            add(new FixedPassBook());
        }};

        App.replacePassStore(new FixedPassListPassStore(list));
        App.getPassStore().setCurrentPass(list.get(0));

        TraceDroid.deleteStacktraceFiles();

    }

    @MediumTest
    public void test_that_image_is_there() {
        final FixedPassBook pass = new FixedPassBook();
        pass.barcodeFormat = BarcodeFormat.PDF_417;
        App.getPassStore().setCurrentPass(pass);
        getActivity();
        onView(withId(R.id.fullscreen_image)).check(matches(isDisplayed()));
        Spoon.screenshot(getActivity(), "pdf417_barcode");

        final ImageView viewById = ButterKnife.findById(getActivity(), R.id.fullscreen_image);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) viewById.getDrawable();
        assertThat(readQRImage(bitmapDrawable.getBitmap())).isEqualTo("foo");
    }


    @MediumTest
    public void test_that_aztec_barcode_works() {
        final FixedPassBook pass = new FixedPassBook();
        pass.barcodeFormat = BarcodeFormat.AZTEC;
        App.getPassStore().setCurrentPass(pass);
        getActivity();
        onView(withId(R.id.fullscreen_image)).check(matches(isDisplayed()));
        Spoon.screenshot(getActivity(), "aztec_barcode");

        final ImageView viewById = ButterKnife.findById(getActivity(), R.id.fullscreen_image);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) viewById.getDrawable();
        assertThat(readQRImage(bitmapDrawable.getBitmap())).isEqualTo("foo");
    }


    @MediumTest
    public void test_that_qr_barcode_works() {
        final FixedPassBook pass = new FixedPassBook();
        pass.barcodeFormat = BarcodeFormat.QR_CODE;
        App.getPassStore().setCurrentPass(pass);
        getActivity();
        onView(withId(R.id.fullscreen_image)).check(matches(isDisplayed()));
        Spoon.screenshot(getActivity(), "qr_barcode");

        final ImageView viewById = ButterKnife.findById(getActivity(), R.id.fullscreen_image);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) viewById.getDrawable();
        assertThat(readQRImage(bitmapDrawable.getBitmap())).isEqualTo("foo");
    }

}

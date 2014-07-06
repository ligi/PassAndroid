package org.ligi.passandroid;

import android.app.Activity;
import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.passandroid.model.InputStreamWithSource;
import org.ligi.passandroid.ui.UnzipPassController;

import java.io.InputStream;

public class TheUnzipPassController extends BaseIntegration<Activity> {

    public TheUnzipPassController() {
        super(Activity.class);
    }

    @SmallTest
    public void test_should_decompress_all_passes() {

        try {
            final InputStream inputStream = getInstrumentation().getContext().getResources().getAssets().open("passes/broken/fail.pkpass");
            final InputStreamWithSource inputStreamWithSource = new InputStreamWithSource("none", inputStream);
            UnzipPassController.processInputStream(inputStreamWithSource, getInstrumentation().getTargetContext(), new UnzipPassController.SuccessCallback() {
                @Override
                public void call(String pathToPassbook) {
                    TheUnzipPassController.fail("should not succeed for broken pass");
                }
            }, new UnzipPassController.FailCallback() {
                @Override
                public void fail(String reason) {

                }
            });
        } catch (Exception e) {
            fail("should be able to load file");
        }
    }

}

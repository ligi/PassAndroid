package org.ligi.passandroid;

import android.app.Activity;
import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.passandroid.ui.UnzipPassController;

import java.io.InputStream;

public class TheUnzipPassController extends BaseIntegration<Activity> {

    public TheUnzipPassController() {
        super(Activity.class);
    }

    @SmallTest
    public void test_should_decompress_all_passes() {

        try {
            final InputStream ins = getInstrumentation().getContext().getResources().getAssets().open("passes/broken/fail.pkpass");
            UnzipPassController.processInputStream(ins, getInstrumentation().getTargetContext(), new UnzipPassController.SuccessCallback() {
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

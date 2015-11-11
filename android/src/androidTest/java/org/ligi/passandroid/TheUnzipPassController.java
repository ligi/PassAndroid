package org.ligi.passandroid;

import android.app.Activity;
import android.test.suitebuilder.annotation.SmallTest;

import org.ligi.passandroid.model.InputStreamWithSource;
import org.ligi.passandroid.ui.UnzipPassController;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.InputStream;

import static org.ligi.passandroid.ui.UnzipPassController.InputStreamUnzipControllerSpec;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class TheUnzipPassController extends BaseIntegration<Activity> {

    @Mock
    UnzipPassController.FailCallback failCallback;

    @Mock
    UnzipPassController.SuccessCallback successCallback;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
    }

    public TheUnzipPassController() {
        super(Activity.class);
    }

    @SmallTest
    public void testShouldFailForBrokenPass() {

        try {

            final InputStream inputStream = getInstrumentation().getContext().getResources().getAssets().open("passes/broken/fail.pkpass");
            final InputStreamWithSource inputStreamWithSource = new InputStreamWithSource("none", inputStream);
            final InputStreamUnzipControllerSpec spec = new InputStreamUnzipControllerSpec(inputStreamWithSource, getInstrumentation().getTargetContext(), successCallback, failCallback);
            UnzipPassController.processInputStream(spec);

            verify(successCallback, never()).call(any(String.class));
            verify(failCallback).fail(any(String.class));

        } catch (Exception e) {
            fail("should be able to load file");
        }
    }

}

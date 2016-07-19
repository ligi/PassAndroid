package org.ligi.passandroid;

import android.support.test.InstrumentationRegistry;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Test;
import org.ligi.passandroid.model.InputStreamWithSource;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.ui.UnzipPassController;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Fail.fail;
import static org.ligi.passandroid.ui.UnzipPassController.InputStreamUnzipControllerSpec;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class TheUnzipPassController  {

    @Mock
    UnzipPassController.FailCallback failCallback;

    @Mock
    UnzipPassController.SuccessCallback successCallback;

    @Mock
    PassStore passStore;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShouldFailForBrokenPass() {

        try {

            final InputStream inputStream = InstrumentationRegistry.getInstrumentation().getContext().getResources().getAssets().open("passes/broken/fail.pkpass");
            final InputStreamWithSource inputStreamWithSource = new InputStreamWithSource("none", inputStream);
            final InputStreamUnzipControllerSpec spec = new InputStreamUnzipControllerSpec(inputStreamWithSource,
                                                                                           InstrumentationRegistry.getInstrumentation().getTargetContext(),
                                                                                           passStore,
                                                                                           successCallback,
                                                                                           failCallback);
            UnzipPassController.INSTANCE.processInputStream(spec);

            verify(successCallback, never()).call(any(String.class));
            verify(failCallback).fail(any(String.class));

        } catch (Exception e) {
            fail("should be able to load file");
        }
    }

}

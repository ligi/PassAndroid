package org.ligi.passandroid;

import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import java.io.File;
import java.io.InputStream;
import org.junit.Before;
import org.ligi.passandroid.model.InputStreamWithSource;
import org.ligi.passandroid.model.PassStore;
import org.ligi.passandroid.model.pass.Pass;
import org.ligi.passandroid.reader.AppleStylePassReader;
import org.ligi.passandroid.ui.UnzipPassController;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.assertj.core.api.Fail.fail;
import static org.ligi.passandroid.ui.UnzipPassController.InputStreamUnzipControllerSpec;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class TheAppleStyleBarcodeReaderBase {

    @Mock
    UnzipPassController.FailCallback failCallback;

    @Mock
    PassStore passStore;

    @Before
    public void setUp()  {
        MockitoAnnotations.initMocks(this);
    }

    private File getTestTargetPath(final Context context) {
        return new File(context.getCacheDir() , "test_passes");
    }

    void loadPassFromAsset(final String asset, final OnPassLoadCallback callback) {
        try {

            final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
            final InputStream inputStream = instrumentation.getContext().getResources().getAssets().open(asset);
            final InputStreamWithSource inputStreamWithSource = new InputStreamWithSource("none", inputStream);

            final InputStreamUnzipControllerSpec spec = new InputStreamUnzipControllerSpec(inputStreamWithSource, instrumentation.getTargetContext(),passStore,
                    new UnzipPassController.SuccessCallback() {
                        @Override
                        public void call(String uuid) {
                            callback.onPassLoad(AppleStylePassReader.INSTANCE.read(new File(getTestTargetPath(instrumentation.getTargetContext()), uuid), "en",
                                                                                   instrumentation.getTargetContext()));
                        }
                    }
                    , failCallback
            );

            spec.setOverwrite(true);
            spec.setTargetPath(getTestTargetPath(spec.getContext()));
            UnzipPassController.INSTANCE.processInputStream(spec);

            verify(failCallback, never()).fail(any(String.class));

        } catch (Exception e) {
            fail("should be able to load file ", e);
        }
    }

    interface OnPassLoadCallback {
        void onPassLoad(Pass pass);
    }
}

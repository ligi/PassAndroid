package org.ligi;

import android.graphics.Bitmap;

import org.junit.Test;
import org.ligi.axt.helpers.BitmapHelper;
import org.ligi.ticketviewer.helper.BarcodeHelper;
import org.junit.runner.RunWith;

import static junit.framework.Assert.fail;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.aztec.AztecWriter;
import com.google.zxing.common.BitMatrix;

import java.lang.Exception;
import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.api.Assertions.assertThat;

import org.robolectric.RobolectricTestRunner;

public class TheBarcodeHelper {

    /**
     * test all 3 barcodes used in passbooks: QR, PDF417 && AZTEC
     *
     * NTFS this makes sense to have it here as tests fail between versions
     * and so we can be kind of sure that we do not break stuff when updating zxing version
     */

    @Test public void barcode_qr_format_should_have_correct_output_size() {
        try {
            BitMatrix tested = BarcodeHelper.getBitMatrix("foo-data", BarcodeFormat.QR_CODE, 42);

            assertThat(tested.getWidth()).isEqualTo(42);
        } catch (Exception e) {
            fail("could not create QR barcode " + e);
        }
    }

    @Test public void barcode_pdf417_format_should_have_correct_output_size() {
        try {
            BitMatrix tested = BarcodeHelper.getBitMatrix("foo-data", BarcodeFormat.PDF_417, 42);

            assertThat(tested.getWidth()).isGreaterThan(42);
        } catch (Exception e) {
            fail("could not create pdf417 barcode " + e);
        }
    }

    @Test public void barcode_aztec_format_writer_should_have_correct_output_size() {
        try {
            BitMatrix tested = BarcodeHelper.getBitMatrix("foo-data", BarcodeFormat.AZTEC, 42);

            assertThat(tested.getWidth()).isEqualTo(42);
        } catch (Exception e) {
            fail("could not create aztec barcode " + e);
        }
    }


      
    /**
     * for Images this fails
     *
     * doesnt work with Robolectric yet - but on device - wait until fix before progessing here
     *
     * java.lang.IllegalStateException
     at android.graphics.Bitmap.setPixel(Bitmap.java:1227)
     at org.ligi.ticketviewer.helper.BarcodeHelper.generateBarCodeBitmap(BarcodeHelper.java:29)
     at org.ligi.TheBarcodeHelper.aztec_writer_should_have_correct_output_size(TheBarcodeHelper.java:33)
     at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:45)
     at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
     at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:42)
     at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
     at org.robolectric.RobolectricTestRunner$2.evaluate(RobolectricTestRunner.java:236)
     at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:263)
     at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:68)
     at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:47)
     at org.junit.runners.ParentRunner$3.run(ParentRunner.java:231)
     at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:60)
     at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:229)
     at org.junit.runners.ParentRunner.access$000(ParentRunner.java:50)
     at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:222)
     at org.robolectric.RobolectricTestRunner$1.evaluate(RobolectricTestRunner.java:177)
     at org.junit.runners.ParentRunner.run(ParentRunner.java:300)
     at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:80)
     at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:47)
     at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69)
     at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:49)
     at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
     at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
     at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32)
     at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93)
     at com.sun.proxy.$Proxy2.processTestClass(Unknown Source)
     at org.gradle.api.internal.tasks.testing.worker.TestWorker.processTestClass(TestWorker.java:103)
     at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
     at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
     at org.gradle.messaging.remote.internal.hub.MessageHub$Handler.run(MessageHub.java:355)
     at org.gradle.internal.concurrent.DefaultExecutorFactory$StoppableExecutorImpl$1.run(DefaultExecutorFactory.java:66)
     at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
     at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
     at java.lang.Thread.run(Thread.java:744)
     *
     @Test public void barcode_aztec_format_writer_should_have_correct_output_size() {
     Bitmap tested = BarcodeHelper.generateBarCodeBitmap("foo-data",BarcodeFormat.AZTEC,42);

     assertThat(tested.getWidth()).isEqualTo(42);
     }
     */

}
package org.ligi.passandroid.unittest

import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.Test
import org.ligi.passandroid.reader.getBarcodeJson

class TheAppleStylePassReaderFunctions {


    @Test
    fun testEmptyWorks() {
        assertThat(JSONObject("{ }").getBarcodeJson()).isNull()
    }

    @Test
            // https://github.com/ligi/PassAndroid/issues/138
    fun testArrayWorks() {
        //language=JSON
        val via_array = "{ \"barcodes\":[{\"format\":\"PKBarcodeFormatQR\",\"message\":\"YO!\"}] }"
        val tested = JSONObject(via_array).getBarcodeJson()

        assertThat(tested).isNotNull()
        assertThat(tested!!.has("message")).isTrue()
    }

    @Test
    fun testSingleWorks() {
        //language=JSON
        val via_array = "{ \"barcode\":{\"format\":\"PKBarcodeFormatQR\",\"message\":\"YO!\"} }"
        val tested = JSONObject(via_array).getBarcodeJson()

        assertThat(tested).isNotNull()
        assertThat(tested!!.has("message")).isTrue()
    }

    @Test
    fun testSingleIsPreferred() {
        //language=JSON
        val via_array = "{ \"barcodes\":[{\"format\":\"PKBarcodeFormatQR\",\"message\":\"NO!\"}] ,\"barcode\":{\"format\":\"PKBarcodeFormatQR\",\"message\":\"YO!\"} }"
        val tested = JSONObject(via_array).getBarcodeJson()

        assertThat(tested).isNotNull()
        assertThat(tested!!.getString("message")).isEqualTo("YO!")
    }
}

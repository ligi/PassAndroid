package org.ligi.passandroid.unittest;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.junit.Test;
import org.ligi.axt.AXT;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class ThePassReader {

    @Test
    public void foo() throws URISyntaxException, IOException {
        final FileInputStream fileInputStream = new FileInputStream(new File(getClass().getClassLoader()
                .getResource("opass_v2/minimal.json")
                .toURI()));


        final Moshi build = new Moshi.Builder().build();
        final JsonAdapter<PassFromLibrePassFormatJSON> adapter = build.adapter(PassFromLibrePassFormatJSON.class);


        final String json = AXT.at(fileInputStream).readToString();
        final PassFromLibrePassFormatJSON passFromLibrePassFormatJSON = adapter.fromJson(json);

        assertThat(passFromLibrePassFormatJSON.what.description).isEqualTo("Concert Ticket");
    }
}

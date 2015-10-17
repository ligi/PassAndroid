package org.ligi.passandroid.unittest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Test;
import org.ligi.passandroid.reader.TAMMultiPassReader;
import org.xml.sax.SAXException;
import static org.assertj.core.api.Assertions.assertThat;

public class TheTAMMultiPassParser {

    @Test
    public void testThatLinksAreParsed() throws IOException, URISyntaxException, ParserConfigurationException, SAXException {
        final TAMMultiPassReader tested = new TAMMultiPassReader(new FileInputStream(new File(getClass().getClassLoader()
                                                                                                        .getResource("tam_example_multipass_reply.html")
                                                                                                        .toURI())));
        assertThat(tested.getLinks())
                .containsOnly("?SITK=annon=ymous&actionMobileHtml=&fpdid=4&uci=yes", "?SITK=goo=bar&actionMobileHtml=&fpdid=8&uci=yolo");

    }

    @Test
    public void testThatDetectionWorksForPositives() throws IOException, URISyntaxException, ParserConfigurationException, SAXException {
        final TAMMultiPassReader tested = new TAMMultiPassReader(new FileInputStream(new File(getClass().getClassLoader()
                                                                                                        .getResource("tam_example_multipass_reply.html")
                                                                                                        .toURI())));
        assertThat(tested.isTAMMultiPass()).isTrue();
    }


    @Test
    public void testThatDetectionDetectsNegatives() throws IOException, URISyntaxException, ParserConfigurationException, SAXException {
        final TAMMultiPassReader tested = new TAMMultiPassReader("");
        assertThat(tested.isTAMMultiPass()).isFalse();
    }
}

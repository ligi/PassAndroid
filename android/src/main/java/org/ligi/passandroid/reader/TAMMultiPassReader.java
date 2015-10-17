package org.ligi.passandroid.reader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.ligi.axt.AXT;
import org.xml.sax.SAXException;

public class TAMMultiPassReader {

    public static final String START_TOKEN = "<td class=\"saveBlock\"><a href=\"";

    final String content;

    public TAMMultiPassReader(final String content) {
        this.content = content;
    }

    public TAMMultiPassReader(final InputStream ins) throws IOException {
        this.content = AXT.at(ins).readToString();
    }


    public boolean isTAMMultiPass() {
        return content.contains("<title>TAM Online check-in</title>");
    }

    /*
    if you try to parse the xml:
    org.xml.sax.SAXParseException; lineNumber: 5; columnNumber: 282; The string "--" is not permitted within comments.
    been there - done that .. broken xml :-(
    */
    public List<String> getLinks() throws ParserConfigurationException, IOException, SAXException {

        final List<String> result = new ArrayList<>();

        int currentPosition = 0;
        while ((currentPosition = content.indexOf(START_TOKEN, currentPosition + 1)) != -1) {
            final String substring = content.substring(currentPosition + START_TOKEN.length(), content.indexOf("class=\"saveLink\"", currentPosition + 1));
            result.add(substring.substring(0, substring.lastIndexOf("\"")));
        }


        return result;
    }


}

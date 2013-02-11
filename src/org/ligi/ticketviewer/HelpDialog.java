package org.ligi.ticketviewer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

/**
 * User: ligi
 * Date: 2/11/13
 * Time: 10:25 PM
 */
public class HelpDialog {

    public static void show(Context c) {

        TextView tv = new TextView(c);
        tv.setPadding(10, 10, 10, 10);
        tv.setText(Html.fromHtml("<H1>Answers</H1>Click on the barcode to get it fullscreen and show to the one who needs to see/scan it. You can also click on the Map to make it fullscreen If you have no passbook yet, but want to test the app anyway you could <a href='http://www.passsource.com/'>get some from here</<a>" +
                "" +
                "If you have no idea what this is all about you can have a look <a href='http://en.wikipedia.org/wiki/Passbook_(application)'>at Wikipedia.</a> <br/>" +
                "<H1>Questions</H1>Give <b>feedback</b> in the <a href='https://plus.google.com/communities/113359783015991036195'>Google+ Community</a> or you can drop the <a href='mailto:ligi@ligi.de'>author a mail</a>. If you like the app" +
                "rate it on <a href='https://play.google.com/store/apps/details?id=org.ligi.ticketviewer'>Google Play</a> I also would love to hear stories about where you used this app."
                + "<H1>Legal</H1>" +
                "This app is not affiliated with Apple - Passbook might be Trademark by Apple, but introduced like a standard so this should be OK <br/>"
                + "THIS SOFTWARE IS PROVIDED \"AS IS\" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED"
                + "WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT"
                + "NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR"
                + "PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,"
                + "WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE."
        ));

        Linkify.addLinks(tv, Linkify.ALL);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        new AlertDialog.Builder(c).setTitle("Help").setView(tv)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }
}

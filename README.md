[![Android app on Google Play](http://ligi.de/img/play_badge.png)](https://play.google.com/store/apps/details?id=org.ligi.passandroid)
[![Android app on FDroid](http://ligi.de/img/fdroid_badge.png)](https://f-droid.org/repository/browse/?fdid=org.ligi.passandroid)

PassAndroid
===========

Android App to view Passbook files

<img src="http://ligi.de/img/passandroid_screenshots.jpg"/>

Displays Passbook ( *.pkpass ) files & shows the Barcode ( QR, PDF417 and AZTEC format ). It useable offline.
When preparing for the Chaos Communication Congress 2012 ( #29c3 ) I stumbled upon a passbook file for the first time. As I really like the idea of paperless tickets as it saves time and trees which both are very valuable to me. The problem was that I found no app with which I could use the downloaded passbook file. I found 2 apps which promised to do it, but both failed and judging by the comments: not only for me. Badly written intent-filters where one of the problems, but as both apps where closed source there was no option to submit a fix. After reading a bit I realized that this app can be written within one hour. the passbook format is just a zip container with some Json encoded data and some images. the essential thing is the Barcode message which is included in the json.
It is not pretty at the moment, but functional ..

THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT 
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Status
------

[![Build Status](https://snap-ci.com/ligi/PassAndroid/branch/master/build_image)](https://snap-ci.com/ligi/PassAndroid/branch/master)

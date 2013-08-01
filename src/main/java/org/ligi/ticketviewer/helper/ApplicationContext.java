package org.ligi.ticketviewer.helper;

import android.app.Application;
import com.google.analytics.tracking.android.EasyTracker;
import org.ligi.tracedroid.TraceDroid;
import org.ligi.tracedroid.logging.Log;

/**
 * User: ligi
 * Date: 2/8/13
 * Time: 11:01 PM
 */
public class ApplicationContext extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        EasyTracker.getInstance().setContext(this);

        TraceDroid.init(this);
        Log.setTAG("TicketViewer");
        Log.i("TicketViewer starting");
    }


}

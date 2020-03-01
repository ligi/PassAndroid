package org.ligi.passandroid

import android.content.Context

fun createTracker(context: Context) = AnalyticsTracker(context) as Tracker

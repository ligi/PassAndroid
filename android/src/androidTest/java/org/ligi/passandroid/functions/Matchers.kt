package org.ligi.passandroid.functions

import android.view.View
import org.hamcrest.Matcher

fun isCollapsed(): Matcher<in View>? = CollapsedCheck() as Matcher<in View>

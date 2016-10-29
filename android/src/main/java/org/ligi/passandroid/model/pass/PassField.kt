package org.ligi.passandroid.model.pass

import android.content.res.Resources
import android.support.annotation.StringRes

class PassField(var key: String?, var label: String?, var value: String?, var hide: Boolean) {

    fun toHtmlSnippet(): String {
        val result = StringBuilder()

        label?.let { result.append("<b>$label</b> ") }
        value?.let { result.append(value) }

        if (label != null || value != null) {
            result.append("<br/>")
        }
        return result.toString()
    }

    companion object {
        fun create(@StringRes label: Int, @StringRes value: Int, res: Resources, hide: Boolean = false) = PassField(null, res.getString(label), res.getString(value), hide)
    }
}
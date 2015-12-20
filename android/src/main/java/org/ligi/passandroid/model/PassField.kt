package org.ligi.passandroid.model

class PassField(val key: String?, val label: String?, val value: String?,val hide:Boolean) {

    fun toHtmlSnippet(): String {
        val result=StringBuilder()
        if (label != null) {
            result.append("<b>").append(label).append("</b> ")
        }
        if (value != null) {
            result.append(value)
        }

        if (label != null || value != null) {
            result.append("<br/>")
        }
        return result.toString()
    }
}
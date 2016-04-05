package org.ligi.passandroid.model.pass

class PassField(var key: String?, var label: String?, var value: String?,var hide:Boolean) {

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
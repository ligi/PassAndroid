package org.ligi.passandroid.json_adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class ZonedTimeAdapter {

    @ToJson
    internal fun toJson(zonedDateTime: ZonedDateTime) = zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    @FromJson
    internal fun fromJson(zonedDateTime: String) = ZonedDateTime.parse(zonedDateTime)

}

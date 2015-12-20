package org.ligi.passandroid.json_adapter;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;

public class ZonedTimeAdapter {
    @ToJson
    String toJson(ZonedDateTime zonedDateTime) {
        return zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME );
    }

    @FromJson
    ZonedDateTime fromJson(String zonedDateTime) {
        return ZonedDateTime.parse(zonedDateTime);
    }
}

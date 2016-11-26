package org.ligi.passandroid.json_adapter

import android.graphics.Color
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.ligi.passandroid.model.pass.PassImpl

class ColorAdapter {
    @ToJson
    internal fun toJson(@PassImpl.HexColor rgb: Int) = String.format("#%06x", rgb)

    @FromJson
    @PassImpl.HexColor
    internal fun fromJson(rgb: String) = Color.parseColor(rgb)

}

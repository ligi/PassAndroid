package org.ligi.passandroid.json_adapter;

import android.graphics.Color;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import org.ligi.passandroid.model.PassImpl;

public class ColorAdapter {
    @ToJson
    String toJson(@PassImpl.HexColor int rgb) {
        return String.format("#%06x", rgb);
    }

    @FromJson
    @PassImpl.HexColor
    int fromJson(String rgb) {
        return Color.parseColor(rgb);
    }
}

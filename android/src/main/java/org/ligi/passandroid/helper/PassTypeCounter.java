package org.ligi.passandroid.helper;

import org.ligi.passandroid.model.CountedType;
import org.ligi.passandroid.model.FiledPass;
import org.ligi.passandroid.model.Pass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class PassTypeCounter {

    public static Set<CountedType> count(final List<FiledPass> passList) {
        final Map<String, Integer> tempMap = new HashMap<>();

        for (Pass info : passList) {
            if (tempMap.containsKey(info.getTypeNotNull())) {
                final Integer i = tempMap.get(info.getTypeNotNull());
                tempMap.put(info.getTypeNotNull(), i + 1);
            } else {
                tempMap.put(info.getTypeNotNull(), 1);
            }
        }

        final Set<CountedType> result = new TreeSet<>();

        for (String type : tempMap.keySet()) {
            result.add(new CountedType(type, tempMap.get(type)));
        }

        return result;
    }
}

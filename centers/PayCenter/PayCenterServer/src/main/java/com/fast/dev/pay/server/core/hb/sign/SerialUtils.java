package com.fast.dev.pay.server.core.hb.sign;

import com.google.gson.*;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class SerialUtils {
    public SerialUtils() {
    }

    private static String sortJson(String json) {
        Gson g = (new GsonBuilder()).setPrettyPrinting().create();
        JsonParser p = new JsonParser();
        JsonElement e = p.parse(json);
        sort(e);
        return g.toJson(e);
    }

    public static void sort(JsonElement e) {
        if (!e.isJsonNull() && !e.isJsonPrimitive()) {
            if (e.isJsonArray()) {
                JsonArray a = e.getAsJsonArray();
                Iterator<JsonElement> it = a.iterator();
                it.forEachRemaining((i) -> {
                    sort(i);
                });
            } else {
                if (e.isJsonObject()) {
                    Map<String, JsonElement> tm = new TreeMap(getComparator());
                    Iterator var3 = e.getAsJsonObject().entrySet().iterator();

                    while(var3.hasNext()) {
                        Entry<String, JsonElement> en = (Entry)var3.next();
                        tm.put((String)en.getKey(), (JsonElement)en.getValue());
                    }

                    Iterator var5 = tm.entrySet().iterator();

                    while(var5.hasNext()) {
                        Entry<String, JsonElement> en = (Entry)var5.next();
                        String key = (String)en.getKey();
                        JsonElement val = (JsonElement)en.getValue();
                        e.getAsJsonObject().remove(key);
                        e.getAsJsonObject().add(key, val);
                        sort(val);
                    }
                }

            }
        }
    }

    private static Comparator<String> getComparator() {
        return (s1, s2) -> {
            return s1.compareTo(s2);
        };
    }

    public static String fromJson(String json) {
        String ret = "";
        json = sortJson(json);
        StringBuilder sb = new StringBuilder();
        Map<String, Object> map1 = (Map)(new Gson()).fromJson(json, Map.class);
        Iterator var5 = map1.entrySet().iterator();

        while(true) {
            Entry entry;
            String key;
            do {
                if (!var5.hasNext()) {
                    sb.deleteCharAt(sb.length() - 1);
                    ret = sb.toString();
                    ret = ret.replace("\r", "");
                    ret = ret.replace("\n", "");
                    ret = ret.replace(",", "");
                    ret = ret.replace(" ", "");
                    return ret;
                }

                entry = (Entry)var5.next();
                key = (String)entry.getKey();
            } while(key == null && "".equals(key.trim()));

            Object o = entry.getValue();
            if (o != null && !"".equals(o.toString().trim())) {
                sb.append(key + "=" + o.toString() + "&");
            }
        }
    }
}

package io.dangernoodle.cpw.util;

import com.google.gson.Gson;
import com.google.gson.JsonParser;


/**
 * Delegate for json operations.
 */
public final class JsonDelegate
{
    private static final Gson gson = new Gson();

    private JsonDelegate()
    {
        // empty
    }

    public static <T> T deserialize(String json, Class<T> clazz)
    {
        return gson.fromJson(json, clazz);
    }

    public static String parameterStoreValue(String json)
    {
        return JsonParser.parseString(json)
                         .getAsJsonObject()
                         .getAsJsonObject("Parameter")
                         .get("Value")
                         .getAsString();
    }

    public static String serialize(Object object)
    {
        return gson.toJson(object);
    }
}

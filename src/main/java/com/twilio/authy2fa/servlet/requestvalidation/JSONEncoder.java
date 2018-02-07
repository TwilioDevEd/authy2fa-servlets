package com.twilio.authy2fa.servlet.requestvalidation;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class JSON encoding. This class contains a static method for
 * converting a {@link JSONObject} into a {@String}. For more information
 * about this encoding strategy consult
 * <a href="https://docs.authy.com/onetouch.html#verifying-authenticity-of-callbacks-from-authy">
 *     Check ApprovalRequest status
 * </a>.
 *
 * @author Agustin Camino
 */
public class JSONEncoder {

    /**
     * Translates a JSONObject into a {@link String}.
     *
     * <p>
     *     Sort the list of received parameters in case-sensitive order and
     *     convert them to URL format.
     * </p>
     *
     * @param jsonObject The object to be encoded.
     *
     * @return The encoded JSON object as String.
     */
    public static String encode(JSONObject jsonObject) {
        Map<String, Object> serializedObject = serialize(jsonObject, new ArrayList<>());

        SortedSet<String> keys = new TreeSet<>(serializedObject.keySet());

        List<String> result = keys.stream()
                .map(key ->String.format("%s=%s", encodeContent(key), encodeContent(serializedObject.get(key).toString())))
                .collect(Collectors.toList());

        return String.join("&", result);
    }

    private static String encodeContent(String content) {

        return content
                .replace("null", "").replace("[", "%5B")
                .replace("]", "%5D").replace("@", "%40")
                .replace("=", "%3D").replace("/", "%2F")
                .replace("+", "%2B").replace(" ", "+");
    }

    private static Map<String, Object> serialize(JSONObject content, List<String> ancestors) {

        Map<String, Object> result = new HashMap<>();

        for(String key : content.keySet()) {

            JSONObject property = tryParseJSONObject(content, key);
            if (property != null) {

                // When the property contains an empty object, then skip it
                if (property.names() == null) {
                    continue;
                }

                ancestors.add(key);
                result.putAll(serialize(property, ancestors));
                ancestors.remove(key);
            } else {

                String path = formatPath(ancestors, key);
                result.put(path, content.get(key));
            }
        }

        return result;
    }

    private static String formatPath(List<String> ancestors, String key) {

        if (ancestors.size() == 0) {
            return key;
        }

        List<String> path = new ArrayList<>(ancestors);
        path.add(key);

        String head = path.remove(0);
        String tail = String.join("", path.stream()
                .map(element -> String.format("[%s]", element))
                .collect(Collectors.toList()));

        return String.format("%s%s", head, tail);
    }

    private static JSONObject tryParseJSONObject(JSONObject object, String key) {

        JSONObject result;

        try {
            result = object.getJSONObject(key);
        } catch (JSONException ex) {
            result = null;
        }

        return result;
    }
}
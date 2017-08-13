package com.example.prodigy.litevervelife.EXTERNAL;


import android.text.TextUtils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public enum EnableState {
    ENABLED,
    DISABLED,
    ENABLING,
    DISABLING;

    public boolean getBoolean(){
        switch (this) {
            case ENABLED:
                return true;
            case DISABLED:
                return false;
        }

        return true;
    }

    public static EnableState setBoolean(boolean status){
        return status ? EnableState.ENABLED : EnableState.DISABLED;
    }

    public static final JsonDeserializer<EnableState> DESERIALIZER;

    /* renamed from: com.hubble.loop.device.EnableState.1 */
    static class C12111 implements JsonDeserializer<EnableState> {
        C12111() {
        }

        public EnableState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            String enumStr = json.getAsString();
            if (TextUtils.isEmpty(enumStr)) {
                return EnableState.DISABLED;
            }
            try {
                return EnableState.valueOf(enumStr);
            } catch (IllegalArgumentException e) {
                try {
                    if (Boolean.valueOf(enumStr).booleanValue()) {
                        return EnableState.ENABLED;
                    }
                    return EnableState.DISABLED;
                } catch (Exception e2) {
                    throw new JsonParseException("Unknown value: " + enumStr);
                }
            }
        }
    }

    static {
        DESERIALIZER = new C12111();
    }
}

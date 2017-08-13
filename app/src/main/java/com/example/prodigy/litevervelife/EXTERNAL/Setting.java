package com.example.prodigy.litevervelife.EXTERNAL;

import java.util.HashMap;

/**
 * Created by Prodigy on 22.07.2017.
 */

/**
 * Класс Настроек
 */
public class Setting implements SettingBundle {
    public HashMap<String, EnableState> booleanSettings = new HashMap();
    public HashMap<String, Integer> intSettings = new HashMap();

   // public static EnableState ear_detect;
  //  public static EnableState passThroughAudio;
  //  public static int sound_eq_preset;
  //  public static int video_eq_preset;

    @Override
    public EnableState getEnableSetting(String key) {
        return this.booleanSettings.containsKey(key) ? (EnableState) this.booleanSettings.get(key) : EnableState.DISABLED;
    }

    @Override
    public int getIntSetting(String key) {
        return this.intSettings.containsKey(key) ? ((Integer) this.intSettings.get(key)).intValue() : 0;
    }

    @Override
    public void setEnableSetting(String key, EnableState value) {
        this.booleanSettings.put(key, value);
    }

    @Override
    public void setIntSetting(String str, int i) {
        this.intSettings.put(str, Integer.valueOf(i));
    }


}

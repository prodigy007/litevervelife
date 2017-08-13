package com.example.prodigy.litevervelife.EXTERNAL;

import java.io.Serializable;

/**
 * Created by Prodigy on 01.04.2017.
 */

public interface SettingBundle extends Serializable {
    EnableState getEnableSetting(String str);

    int getIntSetting(String str);

    void setEnableSetting(String str, EnableState enableState);

    void setIntSetting(String str, int i);


    /**
     * Класс врапер для настроек ?
     */
    public static class SettingsBitFieldSet extends CharacteristicBitFieldSet {
        public static String keySettingEqMode = "drake_setting_eq_preset";
        public static String keySettingVideoEqMode = "drake_setting_video_eq_preset";
        public static String keySettingEarDetect = "drake_setting_ear_detect";
        public static String keySettingPassThroughAudio = "drake_setting_pass_through_audio";

        public SettingsBitFieldSet() {
            super(4);


            put("drake_setting_reset", new EnableStateBitField(0));
            put(keySettingPassThroughAudio, new EnableStateBitField(6));
            put("drake_setting_moto_voice", new EnableStateBitField(7));
            put(keySettingEarDetect, new EnableStateBitField(8));
            put("drake_setting_eq_mode", new EnableStateBitField(9));
            put(keySettingVideoEqMode, new IntBitField(12, 4));
            put(keySettingEqMode, new IntBitField(16, 4));
            put("drake_setting_voice_prompt", new EnableStateBitField(1));
            put("drake_setting_caller_id_alert", new EnableStateBitField(2));
            put("drake_setting_voice_answer", new EnableStateBitField(4));
        }
    }

}

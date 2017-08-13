package com.example.prodigy.litevervelife.EXTERNAL;

/**
 * Created by Prodigy on 01.04.2017.
 */


import java.util.HashMap;

public class CharacteristicBitFieldSet {
    private static final String TAG;
    protected HashMap<String, BitField> mItemDescriptors;
    private final int mSize;

    protected CharacteristicBitFieldSet(int size) {
        this.mSize = size;
        this.mItemDescriptors = new HashMap();
    }
    static {
        TAG = "LoopUI." + CharacteristicBitFieldSet.class.getSimpleName();
    }

    /* renamed from: com.hubble.loop.plugin.CharacteristicBitFieldSet.1 */
    static /* synthetic */ class C12181 {
        static final /* synthetic */ int[] $SwitchMap$com$hubble$loop$device$EnableState;

        static {
            $SwitchMap$com$hubble$loop$device$EnableState = new int[EnableState.values().length];
            try {
                $SwitchMap$com$hubble$loop$device$EnableState[EnableState.ENABLED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$hubble$loop$device$EnableState[EnableState.ENABLING.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
        }
    }

    public static abstract class BitField<T> {
        final int mIndex;
        final int mLen;

       // static byte[] testt;
        protected abstract T fromInt(int i);

        protected abstract int toInt(T t);

        BitField(int index, int len) {
            this.mIndex = index;
            this.mLen = len;
        }

        public T get(byte[] data) {
            int value = 0;
            for (int i = 0; i < this.mLen; i++) {
                value = (value << 1) + getBit(data, this.mIndex + i);
            }
            return fromInt(value);
        }

        public byte[] set(byte[] data, T value) {
            byte[] testt = null;
            int intValue = toInt(value);
            for (int i = this.mLen - 1; i >= 0; i--) {
                testt = setBit(data, this.mIndex + i, intValue & 1);
                intValue >>= 1;
            }
            return testt;
        }

        private int getBit(byte[] data, int pos) {
            int index = pos / 8;
            int offset = 7 - (pos % 8);
            if (index < 0 || index >= data.length) {
                return 0;
            }
            if ((data[index] & (1 << offset)) > 0) {
                return 1;
            }
            return 0;
        }

        private byte[] setBit(byte[] data, int pos, int value) {
            int index = pos / 8;
            int offset = 7 - (pos % 8);
            if (index >= 0 && index < data.length) {
                if (value > 0) {
                    data[index] = (byte) (data[index] | (1 << offset));
                } else {
                    data[index] = (byte) (data[index] & ((1 << offset) ^ -1));
                }
            }
            return data;
        }
    }

    public static class EnableStateBitField extends BitField<EnableState> {
        public EnableStateBitField(int index) {
            super(index, 1);
        }

        protected EnableState fromInt(int value) {
            return value == 1 ? EnableState.ENABLED : EnableState.DISABLED;
        }

        protected int toInt(EnableState value) {
            switch (C12181.$SwitchMap$com$hubble$loop$device$EnableState[value.ordinal()]) {
                case 1 /*1*/:
                case 2 /*2*/:
                    return 1;
                default:
                    return 0;
            }
        }
    }



    public static class IntBitField extends BitField<Integer> {
        public IntBitField(int index, int len) {
            super(index, len);
        }

        protected Integer fromInt(int value) {
            return Integer.valueOf(value);
        }

        protected int toInt(Integer value) {
            return value.intValue();
        }
    }




    public void put(String key, BitField descriptor) {
        this.mItemDescriptors.put(key, descriptor);
    }


    /**
     * Получаем массив Byte (для отправки в Gatt) из указанного класс настроек
     * @param bundle
     * @return
     */
    public byte[] getArray(SettingBundle bundle) {
        byte[] data = new byte[this.mSize];
       // bundleToArrayGOOO(bundle, data);
     //   BitField fdfd = bundleToArrayBitField(bundle, data);
     //    int dffd= 66;
     //   byte[] datafgg = (byte[]) fdfd;
        return  bundleToArrayGOOO(bundle, data);
       // return data;
    }

    /**
     * Работает со статическим классом поэтому вызов dfd.set и присвоение его каждый раз одной переменной дает верное значение
     * @param bundle
     * @param data
     * @return
     */
    public byte[] bundleToArrayGOOO(SettingBundle bundle, byte[] data) {

        for (String key : this.mItemDescriptors.keySet()) {
            BitField descriptor = (BitField) this.mItemDescriptors.get(key);
            if (descriptor instanceof EnableStateBitField) {
                EnableState value = bundle.getEnableSetting(key);
                ((EnableStateBitField) descriptor).set(data, value); // можно и тут назначить переменную типа Byte[]
            } else if (descriptor instanceof IntBitField) {
                int value2 = bundle.getIntSetting(key);
                ((IntBitField) descriptor).set(data, Integer.valueOf(value2));
            }
        }
        return data;
    }

    /**
     * Конвертируем byte от Gatt в настройки Setting
     * @param data
     * @return
     */
    public Setting byteToBundle(byte[] data) {
        Setting setting = new Setting();
        for (String key : this.mItemDescriptors.keySet()) {
            BitField descriptor = (BitField) this.mItemDescriptors.get(key);
            if (descriptor instanceof EnableStateBitField) {
                setting.setEnableSetting(key, (EnableState) ((EnableStateBitField) descriptor).get(data));
            } else if (descriptor instanceof IntBitField) {
                setting.setIntSetting(key, ((Integer) ((IntBitField) descriptor).get(data)).intValue());
            }
        }
        return setting;
    }
}

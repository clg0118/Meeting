package com.jsgm.meeting;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;


public class PreferenceUtils {
    public static String getPrefString(Context context, String key, final String defaultValue) {
        Log.e("getPrefString", "getPrefString: " + context);
        if (context == null) {
            return "";
        }
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getString(key, defaultValue);
    }

    public static void setPrefString(Context context, final String key, final String value) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            settings.edit().putString(key, value).apply();
        }
    }

    public static boolean getPrefBoolean(Context context, final String key, final boolean defaultValue) {
        if (context != null) {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getBoolean(key, defaultValue);
        } else {
            return false;
        }
    }

    public static boolean hasKey(Context context, final String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).contains(key);
    }

    public static void setPrefBoolean(Context context, final String key, final boolean value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putBoolean(key, value).apply();
    }

    public static void setPrefInt(Context context, final String key, final int value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putInt(key, value).apply();
    }

    public static int getPrefInt(Context context, final String key, final int defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getInt(key, defaultValue);
    }

    public static void setPrefFloat(Context context, final String key, final float value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putFloat(key, value).apply();
    }

    public static float getPrefFloat(Context context, final String key, final float defaultValue) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        return settings.getFloat(key, defaultValue);
    }

    public static void setPrefLong(Context context, final String key, final long value) {
        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        settings.edit().putLong(key, value).apply();
    }

    public static long getPrefLong(Context context, final String key, final long defaultValue) {
        try {
            final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            return settings.getLong(key, defaultValue);
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    public static void clearPreference(Context context, final SharedPreferences p) {
        final Editor editor = p.edit();
        editor.clear();
        editor.apply();
    }

    public static void clearPreference(Context context) {
        final Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.clear();
        editor.apply();
    }

    /**
     * ??????SharedPreference?????????????????????
     * ???Base64.encode????????????????????????Base64???????????????String???
     *
     * @param context ?????????
     * @param key     ???????????????key
     * @param object  object??????  ??????????????????Serializable?????????????????????????????????
     *                out.writeObject ???????????? Parcelable ??????????????????
     */
    public static void setObject(Context context, String key, Object object) {
        if (context != null) {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            //?????????????????????
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //???????????????????????????
            ObjectOutputStream out = null;
            try {
                //??????????????????????????????64???????????????sp???
                out = new ObjectOutputStream(baos);
                out.writeObject(object);
                String objectValue = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
                Editor editor = sp.edit();
                editor.putString(key, objectValue);
                editor.apply();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (baos != null) {
                        baos.close();
                    }

                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * ??????SharedPreference???????????????
     * ??????Base64??????String?????????Object??????
     *
     * @param context ?????????
     * @param key     ???????????????key
     * @param <T>     ??????
     * @return ?????????????????????
     */
    public static <T> T getObject(Context context, String key) {
        if (context != null) {
            final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            if (sp.contains(key)) {
                String objectValue = sp.getString(key, null);
                byte[] buffer = Base64.decode(objectValue, Base64.DEFAULT);
                //???????????????????????????????????????????????????????????????????????????????????????
                ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = null;
                try {
                    ois = new ObjectInputStream(bais);
                    T t = (T) ois.readObject();
                    return t;
                } catch (StreamCorruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (bais != null) {
                            bais.close();
                        }

                        if (ois != null) {
                            ois.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}

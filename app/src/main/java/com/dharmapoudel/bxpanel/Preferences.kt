package com.dharmapoudel.bxpanel

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.dharmapoudel.bxpanel.actions.Mode
import com.dharmapoudel.bxpanel.actions.Option

class Preferences(context: Context) {
    private var prefs: SharedPreferences

    private var prefDisableMaxVolumeWarning = false
    private var prefDisableMaxBrightnessWarning = false
    private var prefEnableGreyscale = false
    private var prefAutoBackup = false
    private var prefNoPopupOnBtWifi = false
    private var prefNoPopupGmLocation = false
    private var prefAnimationDuration = 0.50f

    private val enabled = "enabled"
    private val serviceEnabled = "service_enabled"
    private val trackChecked = "track_checked"
    private val DOUBLE_UP_CHECKED = "double_up_checked"
    private val DOUBLE_UP_OPTIONS = "double_up_options"
    private val DOUBLE_UP_MODE = "double_up_mode"

    private val DOUBLE_DOWN_CHECKED = "double_down_checked"
    private val DOUBLE_DOWN_OPTIONS = "double_down_options"
    private val DOUBLE_DOWN_MODE = "double_down_mode"
    private val TRACK_OPTIONS = "track_options"

    private val BROADCAST_0_ACTION = "broadcast_0_action"
    private val BROADCAST_0_RECEIVER = "broadcast_0_receiver"
    private val BROADCAST_0_KEY = "broadcast_0_key"
    private val BROADCAST_0_VALUE = "broadcast_0_value"

    private val BROADCAST_1_ACTION = "broadcast_1_action"
    private val BROADCAST_1_RECEIVER = "broadcast_1_receiver"
    private val BROADCAST_1_KEY = "broadcast_1_key"
    private val BROADCAST_1_VALUE = "broadcast_1_value"

    init {
        prefs = getPref(context)
        loadSavedPreferences()
    }

    var isEnabled: Boolean
        get() = prefs.getBoolean(enabled, prefs.getBoolean(serviceEnabled, false))
        set(value) = prefs.edit { putBoolean(enabled, value) }

    var isTrackChecked: Boolean
        get() = prefs.getBoolean(trackChecked, true)
        set(value) = prefs.edit { putBoolean(trackChecked, value) }

    var isDoubleUpChecked: Boolean
        get() = prefs.getBoolean(DOUBLE_UP_CHECKED, true)
        set(value) = prefs.edit { putBoolean(DOUBLE_UP_CHECKED, value) }

    var isDoubleDownChecked: Boolean
        get() = prefs.getBoolean(DOUBLE_DOWN_CHECKED, true)
        set(value) = prefs.edit { putBoolean(DOUBLE_DOWN_CHECKED, value) }

    var trackOptions: Int
        get() = prefs.getInt(TRACK_OPTIONS, Option.VIBRATE.value)
        set(value) = prefs.edit { putInt(TRACK_OPTIONS, value) }

    var doubleUpOptions: Int
        get() = prefs.getInt(DOUBLE_UP_OPTIONS, Option.VIBRATE.value)
        set(value) = prefs.edit { putInt(DOUBLE_UP_OPTIONS, value) }

    var doubleUpMode: Int
        get() = prefs.getInt(DOUBLE_UP_MODE, Mode.FLASHLIGHT.value)
        set(value) = prefs.edit { putInt(DOUBLE_UP_MODE, value) }

    var doubleDownOptions: Int
        get() = prefs.getInt(DOUBLE_DOWN_OPTIONS, Option.VIBRATE.value)
        set(value) = prefs.edit { putInt(DOUBLE_DOWN_OPTIONS, value) }

    var doubleDownMode: Int
        get() = prefs.getInt(DOUBLE_DOWN_MODE, Mode.FLASHLIGHT.value)
        set(value) = prefs.edit { putInt(DOUBLE_DOWN_MODE, value) }

    var broadcast0Action: String
        get() = prefs.getString(BROADCAST_0_ACTION, "") ?: ""
        set(value) = prefs.edit { putString(BROADCAST_0_ACTION, value) }

    var broadcast0Receiver: String
        get() = prefs.getString(BROADCAST_0_RECEIVER, "") ?: ""
        set(value) = prefs.edit { putString(BROADCAST_0_RECEIVER, value) }

    var broadcast0Key: String
        get() = prefs.getString(BROADCAST_0_KEY, "") ?: ""
        set(value) = prefs.edit { putString(BROADCAST_0_KEY, value) }

    var broadcast0Value: String
        get() = prefs.getString(BROADCAST_0_VALUE, "") ?: ""
        set(value) = prefs.edit { putString(BROADCAST_0_VALUE, value) }

    var broadcast1Action: String
        get() = prefs.getString(BROADCAST_1_ACTION, "") ?: ""
        set(value) = prefs.edit { putString(BROADCAST_1_ACTION, value) }

    var broadcast1Receiver: String
        get() = prefs.getString(BROADCAST_1_RECEIVER, "") ?: ""
        set(value) = prefs.edit { putString(BROADCAST_1_RECEIVER, value) }

    var broadcast1Key: String
        get() = prefs.getString(BROADCAST_1_KEY, "") ?: ""
        set(value) = prefs.edit { putString(BROADCAST_1_KEY, value) }

    var broadcast1Value: String
        get() = prefs.getString(BROADCAST_1_VALUE, "") ?: ""
        set(value) = prefs.edit { putString(BROADCAST_1_VALUE, value) }

    private fun loadSavedPreferences() {
        prefDisableMaxVolumeWarning = prefs.getBoolean("pref_disable_max_volume_warning", false)
        prefDisableMaxBrightnessWarning = prefs.getBoolean("pref_disable_max_brightness_warning", false)
        prefEnableGreyscale = prefs.getBoolean("pref_enable_greyscale", false)
        prefAnimationDuration = prefs.getFloat("pref_animation_duration", prefAnimationDuration)
        prefAutoBackup = prefs.getBoolean("pref_auto_backup", false)
        prefNoPopupOnBtWifi = prefs.getBoolean("pref_no_popup_on_bt_wifi", false)
        prefNoPopupGmLocation = prefs.getBoolean("pref_no_popup_gm_location", false)
    }

    fun savePreference(key: String?, value: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(
            "com.crazyapp.bxpanel.MainActivity",
            Context.MODE_PRIVATE
        )
    }
}
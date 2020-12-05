package xyz.purema.binusmyforum.data.prefs

import android.content.Context

class SharedPrefs(
    ctx: Context
) {
    companion object {
        const val PREFS_NAME = "xyz.purema.binusmyforum.PREFS_MAIN"
        const val PREFS_REFRESH_TOKEN = "refresh_token"
        const val PREFS_ACCESS_TOKEN = "access_token"
        const val PREFS_LAST_EMAIL = "last_email"
        const val PREFS_BINUSIAN_ID = "binusian_id"
        const val PREFS_COURSE_DATA_SYNCHRONIZED = "course_data_synchronized"
    }

    private val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var lastEmail: String?
        get() = prefs.getString(PREFS_LAST_EMAIL, null)
        set(value) = prefs.edit().putString(PREFS_LAST_EMAIL, value).apply()

    var accessToken: String?
        get() = prefs.getString(PREFS_ACCESS_TOKEN, null)
        set(value) = prefs.edit().putString(PREFS_ACCESS_TOKEN, value).apply()

    var refreshToken: String?
        get() = prefs.getString(PREFS_REFRESH_TOKEN, null)
        set(value) = prefs.edit().putString(PREFS_REFRESH_TOKEN, value).apply()

    var binusianId: String?
        get() = prefs.getString(PREFS_BINUSIAN_ID, null)
        set(value) = prefs.edit().putString(PREFS_BINUSIAN_ID, value).apply()

    var courseDataSynchronized: Boolean
        get() = prefs.getBoolean(PREFS_COURSE_DATA_SYNCHRONIZED, false)
        set(value) = prefs.edit().putBoolean(PREFS_COURSE_DATA_SYNCHRONIZED, value).apply()

    fun clear() = prefs.edit().clear().commit()
}
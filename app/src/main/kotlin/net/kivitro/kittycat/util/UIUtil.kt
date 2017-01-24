package net.kivitro.kittycat.util

import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatDelegate
import net.kivitro.kittycat.R
import timber.log.Timber

/**
 * Created by Max on 14.09.2016.
 */
object UIUtil {

	fun setUpTheme(c: Context, value: String? = null) {
		val theme = value ?: PreferenceManager.getDefaultSharedPreferences(c).getString(c.getString(R.string.pref_key_laf_theme), c.getString(R.string.pref_themes_system_value))
		Timber.d("setUpTheme from '%s' to '%s'", AppCompatDelegate.getDefaultNightMode(), theme)
		when (theme) {
			c.getString(R.string.pref_themes_light_value) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
			c.getString(R.string.pref_themes_dark_value) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
			c.getString(R.string.pref_themes_auto_value) -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO)
			else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
		}
	}
}
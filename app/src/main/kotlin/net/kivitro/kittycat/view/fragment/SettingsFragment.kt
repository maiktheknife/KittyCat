package net.kivitro.kittycat.view.fragment

import android.app.Activity
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.preference.*
import net.kivitro.android.preferences.ColorPickerPreference
import net.kivitro.android.preferences.NumberPickerPreference
import net.kivitro.kittycat.R
import net.kivitro.kittycat.presenter.SettingsPresenter
import net.kivitro.kittycat.util.UIUtil.setUpTheme
import net.kivitro.kittycat.view.SettingsView
import timber.log.Timber

/**
 * Created by Max on 15.02.2017.
 */
class SettingsFragment : PreferenceFragmentCompat(), SettingsView, SharedPreferences.OnSharedPreferenceChangeListener {
	override val activity: Activity
		get() = getActivity()

	private lateinit var presenter: SettingsPresenter<SettingsView>

	override fun onCreatePreferences(bundle: Bundle?, s: String?) {
		Timber.d("onCreatePreferences")
		addPreferencesFromResource(R.xml.settings)
		presenter = SettingsPresenter(this)
	}

	override fun onStart() {
		Timber.d("onStart")
		super.onStart()
		for (i in 0..preferenceScreen.preferenceCount - 1) {
			initSummary(preferenceScreen.getPreference(i))
		}

		/* Set Values */
		val thisVersion: String =
		try {
			val pi = activity.packageManager.getPackageInfo(activity.packageName, 0)
			pi.versionName + " (" + pi.versionCode + ")"
		} catch (e: PackageManager.NameNotFoundException) {
			"Could not get version name from manifest!"
		}
		findPreference(getString(R.string.pref_key_about_version)).summary = thisVersion

		findPreference(getString(R.string.pref_key_about_license)).setOnPreferenceClickListener {
			presenter.onAboutClicked()
			true
		}

		findPreference(getString(R.string.pref_key_laf_theme)).setOnPreferenceChangeListener { preference, value ->
			Timber.d("onChange $value")
			setUpTheme(activity, value as String)
			activity.recreate()
			true
		}
	}

	override fun onDisplayPreferenceDialog(preference: Preference?) {
		Timber.d("onDisplayPreferenceDialog ${preference?.javaClass?.name}")
		if (preference is NumberPickerPreference || preference is ColorPickerPreference) {
			val dialogFragment = NumberPickerPreference.newDialogInstance(preference.key)
			dialogFragment.setTargetFragment(this, 0)
			dialogFragment.show(fragmentManager, "android.support.v7.preference.PreferenceFragment.DIALOG")
		} else {
			super.onDisplayPreferenceDialog(preference)
		}
	}

	private fun initSummary(p: Preference?) {
		if (p is PreferenceGroup) { // PreferenceCategory oder PreferenceScreen
			for (i in 0..p.preferenceCount - 1) {
				initSummary(p.getPreference(i))
			}
		} else {
			updatePreferenceSummary(p)
		}
	}

	override fun onResume() {
		super.onResume()
		preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
	}

	override fun onPause() {
		super.onPause()
		preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
	}

	override fun onSharedPreferenceChanged(p: SharedPreferences?, key: String?) {
		Timber.d("onPreferenceChange %s", key)
		updatePreferenceSummary(findPreference(key))
	}

	private fun updatePreferenceSummary(p: Preference?) {
		if (p == null || p is PreferenceScreen || p is PreferenceCategory || p.key == null) {
			return
		}
		if (p is ListPreference) {
			val currentNightMode = resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
			p.summary = when (currentNightMode) {
				Configuration.UI_MODE_NIGHT_NO -> "Using DayTheme"
				Configuration.UI_MODE_NIGHT_YES -> "Using NightTheme"
				else -> "Not sure, assume day"
			}
		}
	}

}
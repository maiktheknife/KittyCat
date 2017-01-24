package net.kivitro.kittycat.presenter

import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import net.kivitro.kittycat.R
import net.kivitro.kittycat.view.SettingsView
import timber.log.Timber

/**
 * Created by Max on 04.08.2016.
 */
class SettingsPresenter<V : SettingsView>(val view: V) : Presenter<V> {

	fun onAboutClicked() {
		Timber.d("onAboutClicked")
		LibsBuilder()
				.withActivityTitle(view.activity.getString(R.string.title_activity_about))
				.withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
				.start(view.activity)
	}

}
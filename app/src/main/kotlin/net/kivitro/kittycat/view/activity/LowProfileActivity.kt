package net.kivitro.kittycat.view.activity

import android.support.v7.app.AppCompatActivity
import android.view.View
import timber.log.Timber

/**
 * Created by Max on 24.01.2017.
 */
abstract class LowProfileActivity : AppCompatActivity() {

	protected fun hideSystemUI() {
		Timber.d("hideSystemUI")
		window.decorView.systemUiVisibility =
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						.or(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
						.or(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
						.or(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) // hide nav bar
						.or(View.SYSTEM_UI_FLAG_LOW_PROFILE) // dim status bar
						.or(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
	}

	protected fun showSystemUI() {
		Timber.d("showSystemUI")
		window.decorView.systemUiVisibility =
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						.or(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
						.or(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
	}

}
package net.kivitro.kittycat.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.ac_full_image.*
import net.kivitro.kittycat.R
import net.kivitro.kittycat.loadUrl
import net.kivitro.kittycat.model.Cat
import uk.co.senab.photoview.PhotoViewAttacher

/**
 * Created by Max on 17.03.2016.
 */
class FullScreenImageActivity : LowProfileActivity() {
	private lateinit var attacher: PhotoViewAttacher

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.ac_full_image)

		hideSystemUI()

		val mutedColor = intent.getIntExtra(EXTRA_COLOR_MUTED, R.color.colorPrimary)
		window.apply {
			decorView.setBackgroundColor(mutedColor)
			statusBarColor = mutedColor
			navigationBarColor = mutedColor
		}

		attacher = PhotoViewAttacher(ac_full_image)

		val cat = intent.getParcelableExtra<Cat>(EXTRA_CAT)
		ac_full_image.loadUrl(cat.url!!, callback = { attacher.update() }, errorCallback = { attacher.update() })
	}

	override fun onStart() {
		super.onStart()
		attacher.update()
	}

	override fun onBackPressed() {
		super.onBackPressed()
		attacher.cleanup()
	}

	override fun onStop() {
		super.onStop()
		attacher.cleanup()
	}

	companion object {
		const val EXTRA_CAT = "extra_cat"
		const val EXTRA_COLOR_MUTED = "extra_muted"
		const val EXTRA_COLOR_VIBRATE = "extra_vibrate"
		const val EXTRA_COLOR_VIBRATE_DARK = "extra_vibrate_dark"

		fun getStarterIntent(context: Context, cat: Cat, mutedColor: Int, vibrateColor: Int, vibrateColorDark: Int): Intent {
			return Intent(context, FullScreenImageActivity::class.java).apply {
				putExtra(EXTRA_CAT, cat)
				putExtra(EXTRA_COLOR_MUTED, mutedColor)
				putExtra(EXTRA_COLOR_VIBRATE, vibrateColor)
				putExtra(EXTRA_COLOR_VIBRATE_DARK, vibrateColorDark)
			}
		}

	}
}
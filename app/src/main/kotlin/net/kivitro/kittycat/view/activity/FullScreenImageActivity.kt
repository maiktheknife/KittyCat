package net.kivitro.kittycat.view.activity

import android.os.Bundle
import android.widget.ImageView
import butterknife.bindView
import net.kivitro.kittycat.R
import net.kivitro.kittycat.loadUrl
import net.kivitro.kittycat.model.Cat
import uk.co.senab.photoview.PhotoViewAttacher

/**
 * Created by Max on 17.03.2016.
 */
class FullScreenImageActivity : LowProfileActivity() {
	private val image: ImageView by bindView(R.id.ac_full_image)
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

		attacher = PhotoViewAttacher(image)

		val cat = intent.getParcelableExtra<Cat>(EXTRA_CAT)
		image.loadUrl(cat.url!!, callback = { attacher.update() }, errorCallback = { attacher.update() })
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
	}
}
package net.kivitro.kittycat.view.activity

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.transition.Transition
import android.util.Log
import android.view.View
import android.widget.ImageView
import butterknife.bindView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.util.DefaultTransitionListener
import uk.co.senab.photoview.PhotoViewAttacher

/**
 * Created by Max on 17.03.2016.
 */
class FullScreenImageActivity : AppCompatActivity() {
    private val HideDelay: Long = 1000
    private val image: ImageView by bindView(R.id.ac_full_image)
    private lateinit var attacher: PhotoViewAttacher
    private var isSystemUiVisible: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_full_image)

        val mutedColor = intent.getIntExtra(EXTRA_COLOR_MUTED, R.color.colorPrimary)
        window.decorView.setBackgroundColor(mutedColor)
        window.statusBarColor = mutedColor
        window.navigationBarColor = mutedColor

        attacher = PhotoViewAttacher(image)
        attacher.onPhotoTapListener = (object : PhotoViewAttacher.OnPhotoTapListener {
            override fun onOutsidePhotoTap() {
                toggleSystemUI()
            }

            override fun onPhotoTap(v: View, x: Float, y: Float) {
                toggleSystemUI()
            }
        })

        val cat = intent.getParcelableExtra<Image>(EXTRA_CAT)
        Picasso
                .with(this)
                .load(cat.url)
                .into(image, object : Callback {
                    override fun onSuccess() {
                        attacher.update()
                    }

                    override fun onError() {
                        attacher.update()
                    }
                })

        window.enterTransition.addListener(object : DefaultTransitionListener() {
            override fun onTransitionEnd(t: Transition) {
                Log.d(TAG, "onTransitionEnd")
                Handler().postDelayed({
                    toggleSystemUI()
                }, HideDelay)
            }
        })
    }

    private fun toggleSystemUI() {
        Log.d(TAG, "toggleSystemUI $isSystemUiVisible")
        if (isSystemUiVisible) {
            hideSystemUI()
        } else {
            showSystemUI()
        }
        isSystemUiVisible = !isSystemUiVisible
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart")
        attacher.update()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d(TAG, "onBackPressed")
        attacher.cleanup()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop")
        attacher.cleanup()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        .or(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
                        .or(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                        .or(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) // hide nav bar
                        .or(View.SYSTEM_UI_FLAG_FULLSCREEN) // hide status bar
                        .or(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    private fun showSystemUI() {
        window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        .or(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
                        .or(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    companion object {
        private val TAG = FullScreenImageActivity::class.java.name
        const val EXTRA_CAT = "extra_cat"
        const val EXTRA_COLOR_MUTED = "extra_bg"
        const val EXTRA_COLOR_VIBRATE = "extra_vibrate"
        const val EXTRA_COLOR_VIBRATE_DARK = "extra_vibrate_dark"
    }
}
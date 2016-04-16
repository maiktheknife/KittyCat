package net.kivitro.kittycat.view.activity

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import butterknife.bindView
import com.squareup.picasso.Picasso
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Image

/**
 * Created by Max on 17.03.2016.
 */
class FullScreenImageActivity : AppCompatActivity() {
    private val image: ImageView by bindView(R.id.ac_full_image)
    private val fab: FloatingActionButton by bindView(R.id.ac_full_favourite)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_full_image)

        val mutedColor = intent.getIntExtra(EXTRA_COLOR_MUTED, R.color.colorPrimary)
        window.decorView.setBackgroundColor(mutedColor)
        window.statusBarColor = mutedColor
        window.navigationBarColor = mutedColor

        fab.visibility = View.GONE
//        fab.backgroundTintList = ColorStateList.valueOf(intent.getIntExtra(EXTRA_COLOR_VIBRATE, R.color.colorAccent));
//        fab.setRippleColor(intent.getIntExtra(EXTRA_COLOR_VIBRATE_DARK, R.color.colorAccentDark))

        val cat = intent.getParcelableExtra<Image>(EXTRA_CAT)
        Picasso
                .with(this)
                .load(cat.url)
                .into(image)

//        window.enterTransition.addListener(object : DefaultTransitionListener() {
//            override fun onTransitionEnd(t: Transition) {
//                Log.d(TAG, "onTransitionEnd enter")
//                fab.animate()
//                        .scaleX(1f)
//                        .scaleY(1f)
//                window.enterTransition.removeListener(this)
//            }
//        })
    }

    companion object {
        private val TAG = FullScreenImageActivity::class.java.name
        const val EXTRA_CAT = "extra_cat"
        const val EXTRA_COLOR_MUTED = "extra_bg"
        const val EXTRA_COLOR_VIBRATE = "extra_vibrate"
        const val EXTRA_COLOR_VIBRATE_DARK = "extra_vibrate_dark"
    }
}
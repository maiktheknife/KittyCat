package net.kivitro.kittycat.view

import android.animation.Animator
import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.transition.Transition
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import butterknife.bindView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.presenter.DetailPresenter
import net.kivitro.kittycat.util.DefaultAnimator
import net.kivitro.kittycat.util.DefaultTransitionListener

/**
 * Created by Max on 10.03.2016.
 */
class DetailActivity : AppCompatActivity(), DetailView {
    private lateinit var presenter: DetailPresenter
    private lateinit var cat: Image

    internal val containerView: View by bindView(R.id.ac_detail_container)
    internal val appbarLayout: AppBarLayout by bindView(R.id.appbar)
    internal val collapseToolbarLayout: CollapsingToolbarLayout by bindView(R.id.collapse_toolbar)
    internal val fab: FloatingActionButton by bindView(R.id.ac_detail_favourite)
    internal val ratingBar: RatingBar by bindView(R.id.ac_detail_ratingbar)
    internal val image: ImageView by bindView(R.id.ac_detail_image)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_detail)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        presenter = DetailPresenter()
        presenter.attachView(this)

        fab.setOnClickListener { view -> presenter.onFABClicked(cat.id!!) }
        image.setOnClickListener { view -> revealImage(view) }

        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            presenter.onVoted(cat.id!!, (rating * 2).toInt())
        }

        cat = intent.getParcelableExtra<Image>(EXTRA_CAT)
        val txtID = findViewById(R.id.ac_detail_id) as TextView
        val txtURL = findViewById(R.id.ac_detail_url) as TextView
        val txtSourceURL = findViewById(R.id.ac_detail_source_url) as TextView
        val txtImage = findViewById(R.id.ac_detail_image) as ImageView
        val txtRate = findViewById(R.id.ac_detail_rate) as TextView

        txtID.text = cat.id
        txtURL.text = cat.url
        txtSourceURL.text = cat.source_url

        window.enterTransition.addListener(object : DefaultTransitionListener() {
            override fun onTransitionEnd(t: Transition) {
                fab.animate()
//                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                window.enterTransition.removeListener(this)
            }
        })

        Picasso
            .with(this)
            .load(cat.url)
            .into(txtImage, object : Callback {
                override fun onSuccess() {
                    val bitmap = ((txtImage.drawable) as BitmapDrawable).bitmap
                    Palette.from(bitmap).maximumColorCount(10).generate { palette ->
                        val vibrantColor = palette.getVibrantColor(resources.getColor(R.color.colorPrimaryDark))
                        txtID.setTextColor(vibrantColor)
                        txtRate.setTextColor(vibrantColor)
                        collapseToolbarLayout.setContentScrimColor(vibrantColor)
                        collapseToolbarLayout.setStatusBarScrimColor(vibrantColor)
                        appbarLayout.setBackgroundColor(vibrantColor)
                    }
                }
                override fun onError() { Log.d(TAG, "onError") }
            })
    }

    private fun revealImage(view: View) {
        val pixelDensity = resources.displayMetrics.density;
        var cx = (view.right.toFloat() - ((28 * pixelDensity) + (16 * pixelDensity))).toInt()
        val cy = view.bottom
        val hypotenuse = Math.hypot(view.width.toDouble(), view.height.toDouble()).toFloat();
        ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, hypotenuse).setDuration(700).start()
    }

    override fun onBackPressed() {
        fab.animate()
//            .alpha(0f)
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
            .setListener(object : DefaultAnimator() {
                override fun onAnimationEnd(a: Animator) = super@DetailActivity.onBackPressed()
            })
    }

    /* @{link DetailView} */

    override fun getActivity(): Activity {
        return this
    }

    override fun getMainView(): View {
        return containerView
    }

    override fun getFABView(): FloatingActionButton {
        return fab
    }

    companion object {
        private val TAG = DetailActivity::class.java.name
        const val EXTRA_CAT = "extra_cat"
    }
}
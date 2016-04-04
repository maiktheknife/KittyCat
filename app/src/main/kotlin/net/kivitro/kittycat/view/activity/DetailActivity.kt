package net.kivitro.kittycat.view.activity

import android.animation.Animator
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.transition.Transition
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import butterknife.bindView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.presenter.DetailPresenter
import net.kivitro.kittycat.util.DefaultAnimatorListener
import net.kivitro.kittycat.util.DefaultTransitionListener
import net.kivitro.kittycat.view.DetailView

/**
 * Created by Max on 10.03.2016.
 */
class DetailActivity : AppCompatActivity(), DetailView {

    private lateinit var presenter: DetailPresenter<DetailView>
    private lateinit var cat: Image
    private var mutedColor: Int = 0
    private var vibrantColor: Int = 0
    private var vibrantDarkColor: Int = 0
    private var issFinishing = false
    private val containerView: View by bindView(R.id.ac_detail_container)
    private val appbarLayout: AppBarLayout by bindView(R.id.appbar)
    private val collapseToolbarLayout: CollapsingToolbarLayout by bindView(R.id.collapse_toolbar)
    private val fab: FloatingActionButton by bindView(R.id.ac_detail_favourite)
    private val ratingBar: RatingBar by bindView(R.id.ac_detail_ratingbar)
    private val image: ImageView by bindView(R.id.ac_detail_image)
    private val txtID: TextView by bindView(R.id.ac_detail_id)
    private val txtRate: TextView by bindView(R.id.ac_detail_rate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_detail)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        presenter = DetailPresenter(this)

        cat = intent.getParcelableExtra<Image>(EXTRA_CAT)
        txtID.text = cat.id

        fab.setOnClickListener { v -> presenter.onFavourited(cat) }
        image.setOnClickListener { v -> presenter.onStartImageAC(cat, mutedColor, vibrantColor, vibrantDarkColor, v) }

        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            presenter.onVoted(cat, (rating * 2).toInt())
        }

        Picasso
                .with(this)
                .load(cat.url)
                .into(image, object : Callback {
                    override fun onSuccess() {
                        val bitmap = ((image.drawable) as BitmapDrawable).bitmap
                        Palette.from(bitmap).generate { applyColor(it) }
                    }

                    override fun onError() {
                        Log.d(TAG, "on Palette Error")
                    }
                })

        /*
        Activity A's exit transition determines how views in A are animated when A starts B.
        Activity B's enter transition determines how views in B are animated when A starts B.
        Activity B's return transition determines how views in B are animated when B returns to A.
        Activity A's reenter transition determines how views in A are animated when B returns to A.
        schön wär es ja ^^
         */
        window.enterTransition.addListener(object : DefaultTransitionListener() {
            override fun onTransitionEnd(t: Transition) {
                Log.d(TAG, "onTransitionEnd enter")
                fab.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                window.enterTransition.removeListener(this)
            }
        })

        window.reenterTransition.addListener(object : DefaultTransitionListener() {
            override fun onTransitionStart(t: Transition) {
                Log.d(TAG, "onTransitionStart reenter")
                fab.scaleX = 0f
                fab.scaleY = 0f
            }

            override fun onTransitionEnd(t: Transition) {
                Log.d(TAG, "onTransitionEnd reenter")
                fab.animate()
                        .scaleX(1f)
                        .scaleY(1f)
            }
        })
    }

    private fun applyColor(palette: Palette) {
        mutedColor = palette.getMutedColor(resources.getColor(R.color.colorPrimary))
        vibrantColor = palette.getVibrantColor(resources.getColor(R.color.colorAccent))
        vibrantDarkColor = palette.getDarkVibrantColor(resources.getColor(R.color.colorAccentDark))

        txtID.setTextColor(mutedColor)
        txtRate.setTextColor(mutedColor)
        collapseToolbarLayout.setContentScrimColor(mutedColor)
        collapseToolbarLayout.setStatusBarScrimColor(mutedColor)
        appbarLayout.setBackgroundColor(mutedColor)

        fab.backgroundTintList = ColorStateList.valueOf(vibrantColor);
        fab.setRippleColor(vibrantDarkColor)
    }

    override fun onBackPressed() {
        if (!issFinishing) {
            issFinishing = true
            fab.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .setListener(object : DefaultAnimatorListener() {
                        override fun onAnimationEnd(a: Animator) {
                            a.removeListener(this)
                            super@DetailActivity.onBackPressed()
                        }
                    })
        }
    }

    /* @{link DetailView} */

    override val activity: Activity
        get() = this

    override fun onVoting(rating: Int) {
        Log.d(DetailPresenter.TAG, "voted")
        Snackbar.make(containerView, "voted: $rating", Snackbar.LENGTH_SHORT).show()
    }

    override fun onVotingError(message: String) {
        Snackbar.make(containerView, "Voting Error: $message", Snackbar.LENGTH_SHORT).show()
    }

    override fun onFavourited() {
        fab.animate()
                .rotationBy(360f)
                .scaleX(1.5f)
                .scaleY(1.5f)
                .setListener(object : DefaultAnimatorListener() {
                    override fun onAnimationEnd(a: Animator) {
                        fab.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setListener(object : DefaultAnimatorListener() {
                                    override fun onAnimationEnd(a: Animator) {
                                        val sb = Snackbar.make(containerView, "Added as favourite <3", Snackbar.LENGTH_SHORT)
                                        sb.setAction(getString(R.string.undo), { v ->
                                            // todo
                                        })
                                        sb.show()
                                    }
                                })

                    }
                })


    }

    override fun onDefavourited() {
        Snackbar.make(containerView, "Removed as favourite :(", Snackbar.LENGTH_SHORT).show()
    }

    override fun onFavouritedError(message: String) {
        Snackbar.make(containerView, "Favourite Error: $message", Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        private val TAG = DetailActivity::class.java.name
        const val EXTRA_CAT = "extra_cat"
    }
}
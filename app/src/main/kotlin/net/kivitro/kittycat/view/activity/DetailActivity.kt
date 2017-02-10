package net.kivitro.kittycat.view.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.transition.Transition
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import butterknife.bindView
import net.kivitro.kittycat.R
import net.kivitro.kittycat.action
import net.kivitro.kittycat.loadUrl
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.presenter.DetailPresenter
import net.kivitro.kittycat.snack
import net.kivitro.kittycat.util.DefaultAnimatorListener
import net.kivitro.kittycat.util.DefaultTransitionListener
import net.kivitro.kittycat.view.DetailView
import timber.log.Timber

/**
 * Created by Max on 10.03.2016.
 */
class DetailActivity : LowProfileActivity(), DetailView {

	private lateinit var presenter: DetailPresenter<DetailView>
	private lateinit var cat: Image
	private var mutedColor: Int = 0
	private var vibrantColor: Int = 0
	private var vibrantDarkColor: Int = 0
	private var issFinishing = false
	private var scrollRange = -1
	private var isAppbarTitleShown = false
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

		collapseToolbarLayout.title = " "
		appbarLayout.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
			if (scrollRange == -1) {
				scrollRange = (appBarLayout.totalScrollRange)
			}
			if (scrollRange + verticalOffset == 0) {
				collapseToolbarLayout.title = getString(R.string.app_name)
				isAppbarTitleShown = true
			} else if (isAppbarTitleShown) {
				collapseToolbarLayout.title = " " //carefully, there should a space between double quote otherwise it wont work
				isAppbarTitleShown = false
			}
		}

		presenter = DetailPresenter(this)

		cat = intent.getParcelableExtra<Image>(EXTRA_CAT)

		initViewWithCat(cat)

		fab.setOnClickListener { v -> presenter.onFavourited(cat) }
		image.setOnClickListener { v ->
			fab.animate()
					.scaleX(0f)
					.scaleY(0f)
					.setListener(object : DefaultAnimatorListener() {
						override fun onAnimationEnd(a: Animator) {
							Timber.d("onTransitionEnd enter")
							fab.hide()
							presenter.onImageClicked(cat, mutedColor, vibrantColor, vibrantDarkColor, v)
						}
					})
		}

		image.loadUrl(cat.url!!, {
			val bitmap = ((image.drawable) as BitmapDrawable).bitmap
			Palette.from(bitmap).generate { applyColor(it) }
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
				Timber.d("onTransitionEnd enter")
				fab.animate()
						.scaleX(1f)
						.scaleY(1f)
				window.enterTransition.removeListener(this)
			}
		})
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
							fab.hide()
							super@DetailActivity.onBackPressed()
						}
					})
		}
	}

	/* View */

	private fun initViewWithCat(cat: Image) {
		txtID.text = cat.id

		if (cat.favourite!!) {
			fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_black))
		}

		initRatingBar((cat.score ?: 0 / 2).toFloat())
	}

	private fun initRatingBar(rating: Float) {
		Timber.d("setRatingBarValue %f", rating)
		ratingBar.rating = 0f
		val anim = ObjectAnimator.ofFloat(ratingBar, "rating", 0f, rating)
		anim.duration = 1000
		anim.addListener(object : DefaultAnimatorListener() {
			override fun onAnimationEnd(a: Animator) {
				ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
					presenter.onVoted(cat, (rating * 2).toInt())
				}
			}
		})
		anim.start()
	}

	private fun applyColor(palette: Palette) {
		mutedColor = palette.getMutedColor(ContextCompat.getColor(this, R.color.colorPrimary))
		vibrantColor = palette.getVibrantColor(ContextCompat.getColor(this, R.color.colorAccent))
		vibrantDarkColor = palette.getDarkVibrantColor(ContextCompat.getColor(this, R.color.colorAccentDark))

		txtID.setTextColor(mutedColor)
		txtRate.setTextColor(mutedColor)
		collapseToolbarLayout.setContentScrimColor(mutedColor)
		collapseToolbarLayout.setStatusBarScrimColor(mutedColor)
		appbarLayout.setBackgroundColor(mutedColor)

		fab.backgroundTintList = ColorStateList.valueOf(vibrantColor)
		fab.rippleColor = vibrantDarkColor
	}

	/* @{link DetailView} */

	override val activity: Activity
		get() = this

	override fun onVoting(rating: Int) {
		Timber.d("onVoting")
		containerView.snack("voted: $rating")
	}

	override fun onVotingError(message: String) {
		Timber.d("onVotingError")
		containerView.snack("Voting Error: $message")
	}

	override fun onFavourited() {
		fab.animate()
				.rotationBy(360f)
				.scaleX(1.5f)
				.scaleY(1.5f)
				.setListener(object : DefaultAnimatorListener() {
					override fun onAnimationEnd(a: Animator) {
						cat.favourite = true
						fab.setImageDrawable(ContextCompat.getDrawable(this@DetailActivity, R.drawable.ic_favorite_black))

						fab.animate()
								.scaleX(1f)
								.scaleY(1f)
								.setListener(object : DefaultAnimatorListener() {
									override fun onAnimationEnd(a: Animator) {
										containerView.snack("Added as favourite <3") {
											action(getString(R.string.undo)) {
												presenter.onFavourited(cat)
											}
										}
									}
								})
					}
				})
	}

	override fun onDefavourited() {
		containerView.snack("Removed as favourite :(")
		cat.favourite = false
		fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_black))
	}

	override fun onFavouritedError(message: String) {
		containerView.snack("Favourite Error: $message")
	}

	companion object {
		const val EXTRA_CAT = "extra_cat"
	}
}
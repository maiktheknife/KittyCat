package net.kivitro.kittycat.view.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.transition.Transition
import android.view.View
import kotlinx.android.synthetic.main.ac_detail.*
import net.kivitro.kittycat.R
import net.kivitro.kittycat.action
import net.kivitro.kittycat.loadUrl
import net.kivitro.kittycat.model.Cat
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
	private lateinit var cat: Cat
	private var mutedColor: Int = 0
	private var vibrantColor: Int = 0
	private var vibrantDarkColor: Int = 0
	private var issFinishing = false
	private var scrollRange = -1
	private var isAppbarTitleShown = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.ac_detail)
		setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

		collapse_toolbar.title = " "
		appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset -> applyScroll(verticalOffset) }

		presenter = DetailPresenter(this)

		cat = intent.getParcelableExtra<Cat>(EXTRA_CAT)

		initViewWithCat(cat)

		ac_detail_favourite.setOnClickListener { v -> if (cat.favourite!!) presenter.onDefavourited(cat) else presenter.onFavourited(cat) }
		ac_detail_image.setOnClickListener { v -> animateImageClick(v) }

		ac_detail_image.loadUrl(cat.url!!, callback = {
			val bitmap = ((ac_detail_image.drawable) as BitmapDrawable).bitmap
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
				ac_detail_favourite.animate()
						.scaleX(1f)
						.scaleY(1f)
				window.enterTransition.removeListener(this)
			}
		})
	}

	override fun onBackPressed() {
		if (!issFinishing) {
			issFinishing = true
			ac_detail_favourite.animate()
					.scaleX(0f)
					.scaleY(0f)
					.setListener(object : DefaultAnimatorListener() {
						override fun onAnimationEnd(a: Animator) {
							a.removeListener(this)
							ac_detail_favourite.hide()
							super@DetailActivity.onBackPressed()
						}
					})
		}
	}

	/* View */

	private fun initViewWithCat(cat: Cat) {
		ac_detail_id.text = cat.id

		if (cat.favourite!!) {
			ac_detail_favourite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_black))
		}

		initRatingBar((cat.score ?: 0 / 2).toFloat())
	}

	private fun initRatingBar(rating: Float) {
		Timber.d("setRatingBarValue %f", rating)
		ac_detail_ratingbar.rating = 0f
		val anim = ObjectAnimator.ofFloat(ac_detail_ratingbar, "rating", 0f, rating)
		anim.duration = 1000
		anim.addListener(object : DefaultAnimatorListener() {
			override fun onAnimationEnd(a: Animator) {
				ac_detail_ratingbar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
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

		ac_detail_id.setTextColor(mutedColor)
		ac_detail_rate.setTextColor(mutedColor)
		collapse_toolbar.setContentScrimColor(mutedColor)
		collapse_toolbar.setStatusBarScrimColor(mutedColor)
		appbar.setBackgroundColor(mutedColor)

		ac_detail_favourite.backgroundTintList = ColorStateList.valueOf(vibrantColor)
		ac_detail_favourite.rippleColor = vibrantDarkColor
	}

	private fun applyScroll(verticalOffset: Int) {
		if (scrollRange == -1) {
			scrollRange = (appbar.totalScrollRange)
		}
		if (scrollRange + verticalOffset == 0) {
			collapse_toolbar.title = getString(R.string.app_name)
			isAppbarTitleShown = true
		} else if (isAppbarTitleShown) {
			collapse_toolbar.title = " " //carefully, there should a space between double quote otherwise it wont work
			isAppbarTitleShown = false
		}
	}

	private fun animateImageClick(v: View) {
		ac_detail_favourite.animate()
				.scaleX(0f)
				.scaleY(0f)
				.setListener(object : DefaultAnimatorListener() {
					override fun onAnimationEnd(a: Animator) {
						Timber.d("onTransitionEnd enter")
						ac_detail_favourite.hide()
						presenter.onImageClicked(cat, mutedColor, vibrantColor, vibrantDarkColor, v)
					}
				})
	}

	/* @{link DetailView} */

	override val activity: Activity
		get() = this

	override fun onVoting(rating: Int) {
		Timber.d("onVoting")
		ac_detail_container.snack("voted: $rating")
	}

	override fun onVotingError(message: String) {
		Timber.d("onVotingError")
		ac_detail_container.snack("Voting Error: $message")
	}

	override fun onFavourited() {
		ac_detail_favourite.animate()
				.rotationBy(360f)
				.scaleX(1.5f)
				.scaleY(1.5f)
				.setListener(object : DefaultAnimatorListener() {
					override fun onAnimationEnd(a: Animator) {
						cat.favourite = true
						ac_detail_favourite.setImageDrawable(ContextCompat.getDrawable(this@DetailActivity, R.drawable.ic_favorite_black))

						ac_detail_favourite.animate()
								.scaleX(1f)
								.scaleY(1f)
								.setListener(object : DefaultAnimatorListener() {
									override fun onAnimationEnd(a: Animator) {
										ac_detail_container.snack("Added as favourite <3") {
											action(getString(R.string.undo)) {
												presenter.onDefavourited(cat)
											}
										}
									}
								})
					}
				})
	}

	override fun onDefavourited() {
		ac_detail_container.snack("Removed as favourite :(")
		cat.favourite = false
		ac_detail_favourite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_black))
	}

	override fun onFavouritedError(message: String) {
		ac_detail_container.snack("Favourite Error: $message")
	}

	companion object {
		const val EXTRA_CAT = "extra_cat"
	}
}
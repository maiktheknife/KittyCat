package net.kivitro.kittycat.view.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.Toolbar
import android.transition.Transition
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
		txtID.text = cat.id

		initRatingBar((cat.score ?: 0 / 2).toFloat())

		fab.setOnClickListener { v -> presenter.onFavourited(cat) }
		image.setOnClickListener { v -> presenter.onImageClicked(cat, mutedColor, vibrantColor, vibrantDarkColor, v) }

		Picasso
				.with(this)
				.load(cat.url)
				.error(R.mipmap.ic_launcher)
				.into(image, object : Callback {
					override fun onSuccess() {
						val bitmap = ((image.drawable) as BitmapDrawable).bitmap
						Palette.from(bitmap).generate { applyColor(it) }
					}

					override fun onError() {
						Timber.d("on Error")
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
				Timber.d("onTransitionEnd enter")
				fab.animate()
						.scaleX(1f)
						.scaleY(1f)
				window.enterTransition.removeListener(this)
			}
		})

		window.reenterTransition.addListener(object : DefaultTransitionListener() {
			override fun onTransitionStart(t: Transition) {
				Timber.d("onTransitionStart reenter")
				fab.scaleX = 0f
				fab.scaleY = 0f
			}

			override fun onTransitionEnd(t: Transition) {
				Timber.d("onTransitionEnd reenter")
				fab.animate()
						.scaleX(1f)
						.scaleY(1f)
			}
		})
	}

	override fun onStart() {
		super.onStart()
		window.enterTransition.addListener(object : DefaultTransitionListener() {
			override fun onTransitionEnd(t: Transition) {
				Timber.d("onTransitionEnd")
				Handler().postDelayed({
					hideSystemUI()
				}, 1000L)
			}
		})
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
		Timber.d("onVoting")
		Snackbar.make(containerView, "voted: $rating", Snackbar.LENGTH_SHORT).show()
	}

	override fun onVotingError(message: String) {
		Timber.d("onVotingError")
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
		const val EXTRA_CAT = "extra_cat"
	}
}
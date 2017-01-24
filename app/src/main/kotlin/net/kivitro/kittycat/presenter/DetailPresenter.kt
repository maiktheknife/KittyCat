package net.kivitro.kittycat.presenter

import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.View
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.network.TheCatAPI
import net.kivitro.kittycat.view.DetailView
import net.kivitro.kittycat.view.activity.FullScreenImageActivity
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by Max on 10.03.2016.
 */
class DetailPresenter<V : DetailView>(val view: V) : Presenter<V> {

	fun onVoted(cat: Image, rating: Int) {
		Timber.d("onVoted %s", cat)
		TheCatAPI.API
				.vote(cat.id!!, rating)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						{ d ->
							view.onVoting(rating)
						},
						{ t ->
							Timber.d(t, "onVoted")
							view.onVotingError(t.message ?: "Unknown Error")
						}
				)
	}

	fun onFavourited(cat: Image) {
		Timber.d("onFavourited %s", cat)
		if (cat.favourite == true) {
			view.onDefavourited()
		} else {
			TheCatAPI.API
					.favourite(cat.id!!, TheCatAPI.ACTION_ADD)
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(
							{ d ->
								view.onFavourited()
							},
							{ t ->
								Timber.d(t, "onFavourited")
								view.onFavouritedError(t.message ?: "Unknown Error")
							})
		}
	}

	fun onImageClicked(cat: Image, mutedColor: Int, vibrateColor: Int, vibrateColorDark: Int, v: View) {
		Timber.d("onImageClicked %s", cat)
		val ac = view.activity
		val intent = Intent(ac, FullScreenImageActivity::class.java)
		intent.putExtra(FullScreenImageActivity.EXTRA_CAT, cat)
		intent.putExtra(FullScreenImageActivity.EXTRA_COLOR_MUTED, mutedColor)
		intent.putExtra(FullScreenImageActivity.EXTRA_COLOR_VIBRATE, vibrateColor)
		intent.putExtra(FullScreenImageActivity.EXTRA_COLOR_VIBRATE_DARK, vibrateColorDark)

		val aoc = ActivityOptionsCompat.makeSceneTransitionAnimation(ac,
				Pair(v, ac.getString(R.string.transition_cat_image))
				//,Pair(view.getFABView() as View, ac.getString(R.string.transition_fab))
		)
		ac.startActivity(intent, aoc.toBundle())
	}

}
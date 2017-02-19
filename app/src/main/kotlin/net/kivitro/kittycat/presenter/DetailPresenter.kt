package net.kivitro.kittycat.presenter

import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.View
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Cat
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

	fun onVoted(cat: Cat, rating: Int) {
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

	fun onFavourited(cat: Cat) {
		Timber.d("onFavourited %s", cat)
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

	fun onDefavourited(cat: Cat): Unit {
		TheCatAPI.API
				.favourite(cat.id!!, TheCatAPI.ACTION_REMOVE)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						{ d ->
							view.onDefavourited()
						},
						{ t ->
							Timber.d(t, "onDefavourited")
							view.onFavouritedError(t.message ?: "Unknown Error")
						})
	}

	fun onImageClicked(cat: Cat, mutedColor: Int, vibrateColor: Int, vibrateColorDark: Int, v: View) {
		Timber.d("onImageClicked %s", cat)
		val ac = view.activity
		val intent = Intent(ac, FullScreenImageActivity::class.java).apply {
			putExtra(FullScreenImageActivity.EXTRA_CAT, cat)
			putExtra(FullScreenImageActivity.EXTRA_COLOR_MUTED, mutedColor)
			putExtra(FullScreenImageActivity.EXTRA_COLOR_VIBRATE, vibrateColor)
			putExtra(FullScreenImageActivity.EXTRA_COLOR_VIBRATE_DARK, vibrateColorDark)
		}

		val aoc = ActivityOptionsCompat.makeSceneTransitionAnimation(ac,
				Pair(v, ac.getString(R.string.transition_cat_image))
		)
		ac.startActivity(intent, aoc.toBundle())
	}

}
package net.kivitro.kittycat.presenter

import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import net.kivitro.kittycat.BuildConfig
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Cat
import net.kivitro.kittycat.network.TheCatAPI
import net.kivitro.kittycat.view.DetailView
import net.kivitro.kittycat.view.activity.FullScreenImageActivity
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Created by Max on 10.03.2016.
 */
class DetailPresenter : Presenter<DetailView>() {

	private var voteDisposable: Disposable? = null
	private var favDisposable: Disposable? = null
	private var defavDisposable: Disposable? = null

	override fun detachView() {
		super.detachView()
		voteDisposable?.dispose()
		favDisposable?.dispose()
		defavDisposable?.dispose()
	}

	fun onVoted(cat: Cat, rating: Int) {
		Timber.d("onVoted %s", cat)
		voteDisposable = TheCatAPI.API
				.vote(cat.id!!, rating)
				.timeout(BuildConfig.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						{ d ->
							view?.onVoting(rating)
						},
						{ t ->
							Timber.d(t, "onVoted")
							view?.onVotingError(t.message ?: "Unknown Error")
						}
				)
	}

	fun onFavourited(cat: Cat) {
		Timber.d("onFavourited %s", cat)
		favDisposable = TheCatAPI.API
				.favourite(cat.id!!, TheCatAPI.ACTION_ADD)
				.timeout(BuildConfig.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						{ d ->
							view?.onFavourited()
						},
						{ t ->
							Timber.d(t, "onFavourited")
							view?.onFavouritedError(t.message ?: "Unknown Error")
						})
	}

	fun onDefavourited(cat: Cat): Unit {
		Timber.d("onDefavourited %s", cat)
		defavDisposable = TheCatAPI.API
				.favourite(cat.id!!, TheCatAPI.ACTION_REMOVE)
				.timeout(BuildConfig.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						{ d ->
							view?.onDefavourited()
						},
						{ t ->
							Timber.d(t, "onDefavourited")
							view?.onFavouritedError(t.message ?: "Unknown Error")
						})
	}

	fun onImageClicked(cat: Cat, mutedColor: Int, vibrateColor: Int, vibrateColorDark: Int, v: View) {
		Timber.d("onImageClicked %s", cat)
		view?.let {
			val ac = it.activity
			val aoc = ActivityOptionsCompat.makeSceneTransitionAnimation(ac,
					Pair(v, ac.getString(R.string.transition_cat_image))
			)
			ac.startActivity(FullScreenImageActivity.getStarterIntent(ac, cat, mutedColor, vibrateColor, vibrateColorDark), aoc.toBundle())
		}
	}

}
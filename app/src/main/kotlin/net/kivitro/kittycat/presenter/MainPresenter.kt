package net.kivitro.kittycat.presenter

import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import net.kivitro.kittycat.BuildConfig
import net.kivitro.kittycat.R
import net.kivitro.kittycat.getConnectivityManager
import net.kivitro.kittycat.model.Cat
import net.kivitro.kittycat.network.TheCatAPI
import net.kivitro.kittycat.view.MainView
import net.kivitro.kittycat.view.activity.DetailActivity
import net.kivitro.kittycat.view.activity.SettingsActivity
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Created by Max on 08.03.2016.
 */
class MainPresenter : Presenter<MainView>() {

	private var categoryDisposable: Disposable? = null
	private var kittensDisposable: Disposable? = null
	private var favsDisposable: Disposable? = null

	override fun detachView() {
		super.detachView()
		Timber.d("detachView")
		categoryDisposable?.dispose()
		kittensDisposable?.dispose()
		favsDisposable?.dispose()
	}

	private fun isConnected() = view?.activity?.getConnectivityManager()?.activeNetworkInfo?.isConnected ?: false

	fun onSettingsClicked() {
		Timber.d("onSettingsClicked")
		view?.activity?.let { it.startActivity(SettingsActivity.getStarterIntent(it)) }
	}

	fun loadCategories() {
		Timber.d("loadCategories")
		if (isConnected()) {
			categoryDisposable = TheCatAPI.API
				.getCategories()
				.timeout(BuildConfig.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
					{ categories ->
						Timber.d("loadCategories got response")
						view?.onCategoriesLoaded(categories.data!!.categories!!)
					},
					{ t ->
						Timber.e(t, "loadCategories")
						view?.onCategoriesLoadError(t.message ?: "Unknown Error")
					}
				)
		} else {
			onNoConnection()
		}
	}

	fun loadKittens(category: String? = null) {
		Timber.d("loadKittens %s", category)
		if (isConnected()) {
			kittensDisposable = TheCatAPI.API
				.getKittens(category)
				.timeout(BuildConfig.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
					{ kittens ->
						Timber.d("loadKittens got response")
						view?.onKittensLoaded(kittens.data!!.images!!)
					},
					{ t ->
						Timber.e(t, "loadKittens")
						view?.onKittensLoadError(t.message ?: "Unknown Error")
					}
				)
		} else {
			onNoConnection()
		}
	}

	fun loadFavourites() {
		Timber.d("loadFavourites")
		if (isConnected()) {
			favsDisposable = TheCatAPI.API
				.getFavourites()
				.timeout(BuildConfig.REQUEST_TIMEOUT, TimeUnit.MILLISECONDS)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
					{ kittens ->
						Timber.d("loadFavourites got response")
						// set favourite = true, they ARE Favourites
						val favourites = kittens.data!!.images!!.map {
							it.apply {
								favourite = true
							}
						}
						view?.onKittensLoaded(favourites)
					},
					{ t ->
						Timber.e(t, "loadFavourites")
						view?.onKittensLoadError(t.message ?: "Unknown Error")
					}
				)
		} else {
			onNoConnection()
		}
	}

	fun onNoConnection() {
		Timber.d("onNoConnection $view")
		view?.showNoConnection()
	}

	fun onKittyClicked(v: View, cat: Cat) {
		Timber.d("onKittyClicked %s", cat)
		view?.let {
			val ac = it.activity
			val aoc = ActivityOptionsCompat.makeSceneTransitionAnimation(ac,
				Pair(v.findViewById(R.id.cat_row_id), ac.getString(R.string.transition_cat_id)),
				Pair(v.findViewById(R.id.cat_row_image), ac.getString(R.string.transition_cat_image))
			)
			ac.startActivity(DetailActivity.getStarterIntent(ac, cat), aoc.toBundle())
		}
	}

}


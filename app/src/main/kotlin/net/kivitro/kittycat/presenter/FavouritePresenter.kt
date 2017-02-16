package net.kivitro.kittycat.presenter

import net.kivitro.kittycat.network.TheCatAPI
import net.kivitro.kittycat.view.FavouriteView
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by Max on 15.02.2017.
 */
class FavouritePresenter<V : FavouriteView>(val view: FavouriteView) : Presenter<FavouriteView> {

	fun loadFavourites() {
		Timber.d("loadFavourites")
		TheCatAPI.API
				.getFavourites()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						{ favourites ->
							view.onFavouritesLoaded(favourites!!.data!!.images!!)
						},
						{ t ->
							Timber.e(t, "loadFavourites")
							view.onFavouritesLoadError(t.message ?: "Unknown Error")
						}
				)
	}
}
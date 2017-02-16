package net.kivitro.kittycat.view.activity

import android.app.Activity
import android.os.Bundle
import net.kivitro.kittycat.model.CatFavouritesImage
import net.kivitro.kittycat.presenter.FavouritePresenter
import net.kivitro.kittycat.view.FavouriteView
import timber.log.Timber

/**
 * Created by Max on 15.02.2017.
 */
class FavouriteActivity : LowProfileActivity(), FavouriteView {

	private lateinit var presenter: FavouritePresenter<FavouriteView>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		hideSystemUI()

		presenter = FavouritePresenter(this)
		presenter.loadFavourites()
	}

	/* {@link FavouriteView} */

	override val activity: Activity
		get() = this

	override fun onFavouritesLoaded(favourites: List<CatFavouritesImage>) {
		Timber.d("$favourites")
	}

	override fun onFavouritesLoadError(message: String) {
		Timber.d(message)
	}

}


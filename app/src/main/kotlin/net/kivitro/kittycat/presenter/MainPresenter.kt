package net.kivitro.kittycat.presenter

import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Cat
import net.kivitro.kittycat.network.TheCatAPI
import net.kivitro.kittycat.view.MainView
import net.kivitro.kittycat.view.activity.DetailActivity
import net.kivitro.kittycat.view.activity.SettingsActivity
import timber.log.Timber

/**
 * Created by Max on 08.03.2016.
 */
class MainPresenter<V : MainView>(val view: V) : Presenter<V> {

	fun onSettingsClicked() {
		Timber.d("onSettingsClicked")
		view.activity.startActivity(Intent(view.activity, SettingsActivity::class.java))
	}

	fun loadCategories() {
		Timber.d("loadCategories")
		TheCatAPI.API
				.getCategories()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						{ categories ->
							view.onCategoriesLoaded(categories.data!!.categories!!)
						},
						{ t ->
							Timber.e(t, "loadCategories")
							view.onCategoriesLoadError(t.message ?: "Unknown Error")
						}
				)
	}

	fun loadKittens(category: String? = null) {
		Timber.d("loadKittens %s", category)
		TheCatAPI.API
				.getKittens(category)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						{ kittens ->
							view.onKittensLoaded(kittens.data!!.images!!)
						},
						{ t ->
							Timber.e(t, "loadKittens")
							view.onKittensLoadError(t.message ?: "Unknown Error")
						}
				)
	}

	fun loadFavourites() {
		Timber.d("loadFavourites")
		TheCatAPI.API
				.getFavourites()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						{ kittens ->
							// set favourite = true, they ARE Favourites
							val favourites = kittens.data!!.images!!.map {
								it.apply {
									favourite = true
								}
							}
							view.onKittensLoaded(favourites)
						},
						{ t ->
							Timber.e(t, "loadFavourites")
							view.onKittensLoadError(t.message ?: "Unknown Error")
						}
				)

	}

	fun onNoConnection() {
		Timber.d("onNoConnection")
		view.showNoConnection()
	}

	fun onKittyClicked(v: View, cat: Cat) {
		Timber.d("onKittyClicked %s", cat)
		val ac = view.activity
		val intent = Intent(ac, DetailActivity::class.java).apply {
			putExtra(DetailActivity.EXTRA_CAT, cat)
		}

		val aoc = ActivityOptionsCompat.makeSceneTransitionAnimation(ac,
				Pair(v.findViewById(R.id.cat_row_id), ac.getString(R.string.transition_cat_id)),
				Pair(v.findViewById(R.id.cat_row_image), ac.getString(R.string.transition_cat_image))
		)
		ac.startActivity(intent, aoc.toBundle())
	}

}


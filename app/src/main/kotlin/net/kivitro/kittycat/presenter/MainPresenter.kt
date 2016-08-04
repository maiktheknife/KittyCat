package net.kivitro.kittycat.presenter

import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.view.View
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.network.TheCatAPI
import net.kivitro.kittycat.view.MainView
import net.kivitro.kittycat.view.activity.DetailActivity
import net.kivitro.kittycat.view.activity.SettingsActivity
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber

/**
 * Created by Max on 08.03.2016.
 */
class MainPresenter<V : MainView>(val view: V) : Presenter<V> {

    fun onSettingsClicked() {
        Timber.d("onSettingsClicked")
        view.activity.startActivity(Intent(view.activity, SettingsActivity::class.java))
    }

    fun onAboutClicked() {
        Timber.d("onAboutClicked")
        LibsBuilder()
                .withActivityTitle("About")
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .start(view.activity)
    }

    fun loadCategories() {
        Timber.d("loadCategories")
        TheCatAPI.API
                .getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { categories ->
                            view.onCategoriesLoaded(categories!!.data!!.categories!!)
                        },
                        { t ->
                            Timber.e(t, "loadCategories")
                            view.onCategoriesLoadError(t.message ?: "Unknown Error")
                        }
                )
    }

    fun loadKittens(category: String?) {
        Timber.d("loadKittens %s", category)
        TheCatAPI.API
                .getKittens(category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { kittens ->
                            view.onKittensLoaded(kittens!!.data!!.images!!)
                        },
                        { t ->
                            Timber.e(t, "loadKittens")
                            view.onKittensLoadError(t.message ?: "Unknown Error")
                        }
                )
    }

    fun onNoConnection() {
        Timber.d("onNoConnection")
        view.showNoConnection()
    }

    fun onKittyClicked(v: View, cat: Image) {
        Timber.d("onKittyClicked %s", cat)
        val ac = view.activity
        val intent = Intent(ac, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_CAT, cat)

        val aoc = ActivityOptionsCompat.makeSceneTransitionAnimation(ac,
                Pair(v.findViewById(R.id.cat_row_id), ac.getString(R.string.transition_cat_id)),
                Pair(v.findViewById(R.id.cat_row_image), ac.getString(R.string.transition_cat_image))
        )
        ac.startActivity(intent, aoc.toBundle())
    }

}


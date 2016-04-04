package net.kivitro.kittycat.presenter

import android.content.Intent
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.util.Log
import android.view.View
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.network.TheCatAPI
import net.kivitro.kittycat.view.MainView
import net.kivitro.kittycat.view.activity.DetailActivity
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Max on 08.03.2016.
 */
class MainPresenter<V : MainView>(val view: V) : Presenter<V> {

    fun onSettingsClicked() {
        view.showSettings()
    }

    fun loadCategories() {
        Log.d(TAG, "loadCategories")
        TheCatAPI.API
                .getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { categories ->
                            view.onCategoriesLoaded(categories!!.data!!.categories!!)
                        },
                        { t ->
                            Log.e(TAG, "loadCategories", t)
                            view.onCategoriesLoadError(t.message ?: "Unknown Error")
                        }
                )
    }

    fun loadKittens(category: String?, showLoading: Boolean) {
        Log.d(TAG, "loadKittens $category $showLoading")
        if (showLoading) {
            view.showState(MainView.State.LOADING)
        }
        TheCatAPI.API
                .getKittens(category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { kittens ->
                            view.showState(MainView.State.CONTENT)
                            view.onKittensLoaded(kittens!!.data!!.images!!)
                        },
                        { t ->
                            Log.e(TAG, "loadKittens", t)
                            view.showState(MainView.State.ERROR)
                            view.onKittensLoadError(t.message ?: "Unknown Error")
                        }
                )
    }

    fun onNoConnection() {
        Log.d(TAG, "onNoConnection")
        view.showState(MainView.State.ERROR)
        view.showNoConnection()
    }

    fun onKittyClicked(v: View, cat: Image) {
        Log.d(TAG, "onKittyClicked $cat")
        val ac = view.activity
        val intent = Intent(ac, DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_CAT, cat)

        val aoc = ActivityOptionsCompat.makeSceneTransitionAnimation(ac,
                Pair(v.findViewById(R.id.cat_row_id), ac.getString(R.string.transition_cat_id)),
                Pair(v.findViewById(R.id.cat_row_image), ac.getString(R.string.transition_cat_image))
        )
        ac.startActivity(intent, aoc.toBundle())
    }

    companion object {
        private final val TAG = MainPresenter::class.java.name
    }

}


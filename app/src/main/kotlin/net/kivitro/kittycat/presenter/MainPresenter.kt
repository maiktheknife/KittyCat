package net.kivitro.kittycat.presenter

import android.content.Intent
import android.support.design.widget.Snackbar
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
        Snackbar.make(view.container, "Settings", Snackbar.LENGTH_SHORT).show()
    }

    fun loadCategories() {
        TheCatAPI.API
                .getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { categories ->
                            view.onCategoriesLoaded(categories!!.data!!.categories!!)
                            view.getSwipeLayout().isRefreshing = false;
                        },
                        { t ->
                            Log.e(TAG, "loadCategories", t)
                            Snackbar.make(view.container, "Loading Error", Snackbar.LENGTH_SHORT).show()
                            view.getSwipeLayout().isRefreshing = false;
                        }
                )
    }

    fun loadKittens(category: String?) {
        TheCatAPI.API
                .getKittens(category)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { kittens ->
                            view.onKittensLoaded(kittens!!.data!!.images!!)
                            view.getSwipeLayout().isRefreshing = false;
                        },
                        { t ->
                            Log.e(TAG, "loadKittens", t)
                            Snackbar.make(view.container, "Loading Error", Snackbar.LENGTH_SHORT).show()
                            view.getSwipeLayout().isRefreshing = false;
                        }
                )
    }

    fun onNoConnection() {
        Snackbar.make(view.container, "No Connection", Snackbar.LENGTH_SHORT).show()
        view.getSwipeLayout().isRefreshing = false;
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
        @JvmField final val TAG = MainPresenter::class.java.name
    }

}


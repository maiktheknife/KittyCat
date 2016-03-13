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
import net.kivitro.kittycat.view.DetailActivity
import net.kivitro.kittycat.view.MainView
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Max on 08.03.2016.
 */
class MainPresenter : Presenter<MainView> {

    var mainView: MainView? = null

    override fun attachView(v: MainView) {
        mainView = v
    }

    override fun detachView() {
        mainView = null
    }

    fun onSettingsClicked() {
        Snackbar.make(mainView!!.getContainerView(), "Settings", Snackbar.LENGTH_SHORT).show()
    }

    fun loadCategories() {
        TheCatAPI.API
            .getCategories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                    { categories ->
                        mainView?.onCategoriesLoaded(categories!!.data!!.categories!!)
                        mainView?.getSwipeLayout()?.isRefreshing = false;
                    },
                    { t ->
                        Log.e(TAG, "loadCategories", t)
                        Snackbar.make(mainView!!.getContainerView(), "Loading Error", Snackbar.LENGTH_SHORT).show()
                        mainView?.getSwipeLayout()?.isRefreshing = false;
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
                        mainView?.onKittensLoaded(kittens!!.data!!.images!!)
                        mainView?.getSwipeLayout()?.isRefreshing = false;
                    },
                    { t ->
                        Log.e(TAG, "loadKittens", t)
                        Snackbar.make(mainView!!.getContainerView(), "Loading Error", Snackbar.LENGTH_SHORT).show()
                        mainView?.getSwipeLayout()?.isRefreshing = false;
                    }
            )
    }

    fun onNoConnection() {
        Snackbar.make(mainView!!.getContainerView(), "No Connection", Snackbar.LENGTH_SHORT).show()
        mainView!!.getSwipeLayout().isRefreshing = false;
    }

    fun onKittyClicked(view: View, cat: Image) {
        Log.d(TAG, "onKittyClicked $cat")
        val ac = mainView!!.getActivity()
        val intent = Intent(mainView!!.getActivity(), DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_CAT, cat)

        val aoc = ActivityOptionsCompat.makeSceneTransitionAnimation(ac,
                // Pair(view.findViewById(R.id.cat_row_layout), ac.getString(R.string.transition_layout)),
                Pair(view.findViewById(R.id.cat_row_image), ac.getString(R.string.transition_cat_image)),
                Pair(view.findViewById(R.id.cat_row_id), ac.getString(R.string.transition_cat_id))
        )
        ac.startActivity(intent, aoc.toBundle())
    }

    companion object {
        @JvmField final val TAG = MainPresenter::class.java.name
    }

}


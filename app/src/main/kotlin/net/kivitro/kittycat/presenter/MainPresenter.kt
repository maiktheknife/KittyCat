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

    private var mainView: MainView? = null

    override fun attachView(v: MainView) {
        mainView = v
    }

    override fun detachView() {
        mainView = null
    }

    fun onFABClicked() {
        Snackbar.make(mainView!!.getMainView(), "Reload soon...", Snackbar.LENGTH_SHORT).show()
    }

    fun onSettingsClicked() {
        Snackbar.make(mainView!!.getMainView(), "Settings", Snackbar.LENGTH_SHORT).show()
    }

    fun loadKittens() {
        TheCatAPI.API
            .getKittens(null)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                    { kittens -> mainView?.onKittensLoaded(kittens!!.data!!.images!!) },
                    { t ->
                        Log.e(TAG, "error", t)
                        Snackbar.make(mainView!!.getMainView(), "Loading Error", Snackbar.LENGTH_SHORT).show()
                    }
            )
    }

    fun onKittyClicked(view: View, cat: Image) {
        Log.d(TAG, "onKittyClicked $cat")
        val ac = mainView!!.getActivity()
        val intent = Intent(mainView!!.getActivity(), DetailActivity::class.java)
        intent.putExtra(DetailActivity.EXTRA_CAT, cat)

        val aoc = ActivityOptionsCompat.makeSceneTransitionAnimation(ac,
//                Pair(mainView!!.getFABView(), ac.getString(R.string.transition_fab)),
                Pair(view.findViewById(R.id.cat_row_image), ac.getString(R.string.transition_cat_image)),
                Pair(view.findViewById(R.id.cat_row_id), ac.getString(R.string.transition_cat_id)),
                Pair(view.findViewById(R.id.cat_row_image_url), ac.getString(R.string.transition_cat_url)),
                Pair(view.findViewById(R.id.cat_row_source_url), ac.getString(R.string.transition_cat_source_url))
        )
        ac.startActivity(intent, aoc.toBundle())
    }

    companion object {
        final val TAG = MainPresenter::class.java.name
    }

}


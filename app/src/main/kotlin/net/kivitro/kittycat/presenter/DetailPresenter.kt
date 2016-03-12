package net.kivitro.kittycat.presenter

import android.support.design.widget.Snackbar
import android.util.Log
import net.kivitro.kittycat.network.TheCatAPI
import net.kivitro.kittycat.view.DetailView
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Max on 10.03.2016.
 */
class DetailPresenter :Presenter<DetailView> {

    private var mainView: DetailView? = null

    override fun attachView(v: DetailView) {
        mainView = v
    }

    override fun detachView() {
        mainView = null
    }

    fun onFABClicked(image_id: String) {
        Log.d(TAG, "onFABClicked: $image_id")
        TheCatAPI.API
            .favourite(image_id, TheCatAPI.ACTION_ADD)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe (
                    { d ->
                        Log.d(TAG, "Favourite Success")
                        Snackbar.make(mainView!!.getMainView(), "Added as favourite <3", Snackbar.LENGTH_SHORT).show()
                    },
                    { t ->
                        Log.e(TAG, "error", t)
                        Snackbar.make(mainView!!.getMainView(), "Favourite Error", Snackbar.LENGTH_SHORT).show()
                    }
            )
    }

    companion object {
        final val TAG = DetailPresenter::class.java.name
    }

}
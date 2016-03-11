package net.kivitro.kittycat.presenter

import android.support.design.widget.Snackbar
import net.kivitro.kittycat.view.DetailView

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

    fun onFABClicked() {
        Snackbar.make(mainView!!.getMainView(), "Added as favourite <3", Snackbar.LENGTH_SHORT).show()
    }

}
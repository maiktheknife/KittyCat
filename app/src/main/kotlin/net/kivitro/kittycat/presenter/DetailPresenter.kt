package net.kivitro.kittycat.presenter

import android.animation.Animator
import android.support.design.widget.Snackbar
import android.util.Log
import net.kivitro.kittycat.R
import net.kivitro.kittycat.util.DefaultAnimator
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

    fun onVoted(image_id: String, rating: Int) {
        Log.d(TAG, "onVoted: $rating")
//        TheCatAPI.API
//            .vote(image_id, rating)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe (
//                    { d ->
//                        Log.d(TAG, "successful voted")
                        Snackbar.make(mainView!!.getMainView(), "voted", Snackbar.LENGTH_SHORT).show()
//                    },
//                    { t ->
//                        Log.e(TAG, "error", t)
//                        Snackbar.make(mainView!!.getMainView(), "Voting Error", Snackbar.LENGTH_SHORT).show()
//                    }
//            )
    }

    fun onFABClicked(image_id: String) {
        Log.d(TAG, "onFABClicked: $image_id")
//        TheCatAPI.API
//            .favourite(image_id, TheCatAPI.ACTION_ADD)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe (
//                    { d ->
//                        Log.d(TAG, "Favourite Success")
        mainView!!.getFABView().animate()
            .rotationBy(360f)
            .scaleX(1.5f)
            .scaleY(1.5f)
            .setListener(object : DefaultAnimator() {
                override fun onAnimationEnd(a: Animator) {
                    mainView!!.getFABView().animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setListener(object : DefaultAnimator() {
                            override fun onAnimationEnd(a: Animator) {
                                val sb = Snackbar.make(mainView!!.getMainView(), "Added as favourite <3", Snackbar.LENGTH_SHORT)
                                sb.setAction(mainView!!.getActivity().getString(R.string.undo), { view ->
                                    Snackbar.make(mainView!!.getMainView(), "Removed as favourite :(", Snackbar.LENGTH_SHORT).show()
                                })
                                sb.show()
                            }
                            }
                            )

                }
            }
                )

//
//                    },
//                    { t ->
//                        Log.e(TAG, "error", t)
//                        Snackbar.make(mainView!!.getMainView(), "Favourite Error", Snackbar.LENGTH_SHORT).show()
//                    }
//            )
    }

    companion object {
        final val TAG = DetailPresenter::class.java.name
    }

}
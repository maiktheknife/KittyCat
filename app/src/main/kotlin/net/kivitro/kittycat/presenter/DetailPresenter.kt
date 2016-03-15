package net.kivitro.kittycat.presenter

import android.animation.Animator
import android.support.design.widget.Snackbar
import android.util.Log
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.network.TheCatAPI
import net.kivitro.kittycat.util.DefaultAnimatorListener
import net.kivitro.kittycat.view.DetailView
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Max on 10.03.2016.
 */
class DetailPresenter<V : DetailView>(val view: V) : Presenter<V> {

    fun onVoted(image_id: String, rating: Int) {
        Log.d(TAG, "onVoted: $rating")
        TheCatAPI.API
                .vote(image_id, rating)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { d ->
                            Log.d(TAG, "successful voted")
                            Snackbar.make(view.container, "voted", Snackbar.LENGTH_SHORT).show()
                        },
                        { t ->
                            Log.e(TAG, "error", t)
                            Snackbar.make(view.container, "Voting Error", Snackbar.LENGTH_SHORT).show()
                        }
                )
    }

    fun onFABClicked(cat: Image) {
        Log.d(TAG, "onFABClicked: $cat")
        if (cat.favourite == true) {
            Snackbar.make(view.container, "Removed as favourite :(", Snackbar.LENGTH_SHORT).show()
        } else {
            TheCatAPI.API
                    .favourite(cat.id!!, TheCatAPI.ACTION_ADD)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe (
                            { d ->
                                Log.d(TAG, "Favourite Success")
                                view.getFABView().animate()
                                        .rotationBy(360f)
                                        .scaleX(1.5f)
                                        .scaleY(1.5f)
                                        .setListener(object : DefaultAnimatorListener() {
                                            override fun onAnimationEnd(a: Animator) {
                                                view.getFABView().animate()
                                                        .scaleX(1f)
                                                        .scaleY(1f)
                                                        .setListener(object : DefaultAnimatorListener() {
                                                            override fun onAnimationEnd(a: Animator) {
                                                                val sb = Snackbar.make(view.container, "Added as favourite <3", Snackbar.LENGTH_SHORT)
                                                                sb.setAction(view.activity.getString(R.string.undo), { v ->
                                                                    Snackbar.make(view.container, "Removed as favourite :(", Snackbar.LENGTH_SHORT).show()
                                                                })
                                                                sb.show()
                                                            }
                                                        }
                                                        )

                                            }
                                        })
                            },
                            { t ->
                                Log.e(TAG, "error", t)
                                Snackbar.make(view.container, "Favourite Error", Snackbar.LENGTH_SHORT).show()
                            })
        }
    }

    companion object {
        final val TAG = DetailPresenter::class.java.name
    }

}
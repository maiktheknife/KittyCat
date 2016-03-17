package net.kivitro.kittycat.presenter

import android.animation.Animator
import android.content.Intent
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.util.Pair
import android.util.Log
import android.view.View
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.network.TheCatAPI
import net.kivitro.kittycat.util.DefaultAnimatorListener
import net.kivitro.kittycat.view.DetailView
import net.kivitro.kittycat.view.activity.FullScreenImageActivity
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by Max on 10.03.2016.
 */
class DetailPresenter<V : DetailView>(val view: V) : Presenter<V> {

    fun onVoted(cat: Image, rating: Int) {
        Log.d(TAG, "onVoted: $cat")
        TheCatAPI.API
                .vote(cat.id!!, rating)
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

    fun onStartImageAC(cat: Image, mutedColor: Int, vibrateColor: Int, vibrateColorDark: Int, v: View) {
        Log.d(TAG, "onStartImageAC: $cat")
        val ac = view.activity
        val intent = Intent(ac, FullScreenImageActivity::class.java)
        intent.putExtra(FullScreenImageActivity.EXTRA_CAT, cat)
        intent.putExtra(FullScreenImageActivity.EXTRA_COLOR_MUTED, mutedColor)
        intent.putExtra(FullScreenImageActivity.EXTRA_COLOR_VIBRATE, vibrateColor)
        intent.putExtra(FullScreenImageActivity.EXTRA_COLOR_VIBRATE_DARK, vibrateColorDark)

        val aoc = ActivityOptionsCompat.makeSceneTransitionAnimation(ac,
                Pair(v, ac.getString(R.string.transition_cat_image))
                //                Pair(view.getFABView() as View, ac.getString(R.string.transition_fab))
        )
        ac.startActivity(intent, aoc.toBundle())
    }

    companion object {
        final val TAG = DetailPresenter::class.java.name
    }

}
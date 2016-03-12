package net.kivitro.kittycat.view

import android.app.Activity
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import net.kivitro.kittycat.model.Image

/**
 * Created by Max on 10.03.2016.
 */
interface MainView {
    fun getActivity(): Activity
    fun getContainerView(): View
    fun getSwipeLayout(): SwipeRefreshLayout
    fun onKittensLoaded(kittens: List<Image>)
}
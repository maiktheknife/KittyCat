package net.kivitro.kittycat.view

import android.support.v4.widget.SwipeRefreshLayout
import net.kivitro.kittycat.model.Category
import net.kivitro.kittycat.model.Image

/**
 * Created by Max on 10.03.2016.
 */
interface MainView : View {
    fun getSwipeLayout(): SwipeRefreshLayout

    fun onKittensLoaded(kittens: List<Image>)
    fun onCategoriesLoaded(categories: List<Category>)
}
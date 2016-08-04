package net.kivitro.kittycat.view

import net.kivitro.kittycat.model.Category
import net.kivitro.kittycat.model.Image

/**
 * Created by Max on 10.03.2016.
 */
interface MainView : View {
    fun onKittensLoaded(kittens: List<Image>)
    fun onKittensLoadError(message: String)

    fun onCategoriesLoaded(categories: List<Category>)
    fun onCategoriesLoadError(message: String)

    fun showNoConnection()
}
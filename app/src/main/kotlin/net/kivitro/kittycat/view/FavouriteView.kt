package net.kivitro.kittycat.view

import net.kivitro.kittycat.model.CatFavouritesImage

/**
 * Created by Max on 15.02.2017.
 */
interface FavouriteView : View {

	fun onFavouritesLoaded(favourites: List<CatFavouritesImage>)

	fun onFavouritesLoadError(message: String)

}
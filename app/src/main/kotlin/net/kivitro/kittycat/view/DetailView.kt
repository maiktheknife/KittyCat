package net.kivitro.kittycat.view

/**
 * Created by Max on 10.03.2016.
 */
interface DetailView : View {
	fun onVoting(rating: Int)
	fun onVotingError(message: String)

	fun onFavourited()
	fun onDefavourited()
	fun onFavouritedError(message: String)
}
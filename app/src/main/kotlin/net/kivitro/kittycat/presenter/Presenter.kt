package net.kivitro.kittycat.presenter

import net.kivitro.kittycat.view.View

/**
 * Created by Max on 08.03.2016.
 */
abstract class Presenter<V : View> {

	protected var view: V? = null

	open fun attachView(view: V) {
		this.view = view
	}

	open fun detachView() {
		this.view = null
	}
}
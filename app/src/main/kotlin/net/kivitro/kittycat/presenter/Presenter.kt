package net.kivitro.kittycat.presenter

/**
 * Created by Max on 08.03.2016.
 */
interface Presenter<V> {

    fun attachView(v: V)

    fun detachView()

}

package net.kivitro.kittycat.view

import android.app.Activity
import android.support.design.widget.FloatingActionButton
import android.view.View

/**
 * Created by Max on 10.03.2016.
 */
interface DetailView {
    fun getActivity(): Activity
    fun getMainView() : View
    fun getFABView() : FloatingActionButton
}
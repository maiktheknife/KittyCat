package net.kivitro.kittycat.util

import android.transition.Transition

/**
 * Created by Max on 12.03.2016.
 */
open class DefaultTransitionListener : Transition.TransitionListener {
    override fun onTransitionEnd(t: Transition) {}

    override fun onTransitionResume(t: Transition) {}

    override fun onTransitionPause(t: Transition) {}

    override fun onTransitionCancel(t: Transition) {}

    override fun onTransitionStart(t: Transition) {}
}
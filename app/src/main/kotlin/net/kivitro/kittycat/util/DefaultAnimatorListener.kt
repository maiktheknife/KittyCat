package net.kivitro.kittycat.util

import android.animation.Animator

/**
 * Created by Max on 12.03.2016.
 */
open class DefaultAnimatorListener : Animator.AnimatorListener {
	override fun onAnimationRepeat(a: Animator) {}

	override fun onAnimationEnd(a: Animator) {}

	override fun onAnimationCancel(a: Animator) {}

	override fun onAnimationStart(a: Animator) {}
}
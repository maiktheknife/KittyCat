package net.kivitro.kittycat

import android.app.Activity
import android.net.ConnectivityManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

/**
 * Created by Max on 09.02.2017.
 */

fun ImageView.loadUrl(url: String, callback: () -> Unit, errorCallback: () -> Unit = {}) {
	Picasso.with(context)
			.load(url)
			.error(R.mipmap.ic_launcher)
			.into(this, object : Callback {
				override fun onSuccess() {
					callback()
				}

				override fun onError() {
					errorCallback()
				}
			})

}

fun View.snack(message: String, length: Int = Snackbar.LENGTH_SHORT) {
	Snackbar.make(this, message, length).show()
}

fun View.snack(message: String, length: Int = Snackbar.LENGTH_SHORT, f: Snackbar.() -> Unit) {
	val snack = Snackbar.make(this, message, length)
	snack.f()
	snack.show()
}

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit) {
	setAction(action, listener)
	color?.let { setActionTextColor(color) }
}

fun Activity.getConnectivityManager() = getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as ConnectivityManager

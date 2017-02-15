package net.kivitro.kittycat

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.squareup.picasso.Picasso
import net.kivitro.kittycat.network.TheCatAPI
import net.kivitro.kittycat.util.UIUtil.setUpTheme
import timber.log.Timber

/**
 * Created by Max on 12.03.2016.
 */
class App : Application() {

	override fun onCreate() {
		super.onCreate()
		setUpLeakCanary()
		setUpTimber()
		setUpRetrofit()
		setUpPicasso()
		setUpTheme(this)
	}

	private fun setUpLeakCanary() {
		LeakCanary.install(this)
	}

	private fun setUpTimber() {
		if (BuildConfig.DEBUG) {
			Timber.plant(Timber.DebugTree())
		}
	}

	private fun setUpRetrofit() {
		TheCatAPI.create(this)
	}

	private fun setUpPicasso() {
		val picasso = Picasso.Builder(this)
				.loggingEnabled(false)
				.indicatorsEnabled(BuildConfig.DEBUG)
				.build()
		Picasso.setSingletonInstance(picasso)
	}

}
package net.kivitro.kittycat

import android.app.Application
import net.kivitro.kittycat.network.TheCatAPI
import timber.log.Timber

/**
 * Created by Max on 12.03.2016.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setUpTimber()
        setUpRetrofit()
    }

    private fun setUpTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun setUpRetrofit() {
        TheCatAPI.create(this)
    }

}
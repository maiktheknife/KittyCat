package net.kivitro.kittycat

import android.app.Application
import net.kivitro.kittycat.network.TheCatAPI

/**
 * Created by Max on 12.03.2016.
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        setUpRetrofit()
    }

    private fun setUpRetrofit() {
        TheCatAPI.create(this)
    }

}
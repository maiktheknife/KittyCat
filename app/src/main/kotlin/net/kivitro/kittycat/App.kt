package net.kivitro.kittycat

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import net.kivitro.kittycat.network.TheCatAPI
import net.kivitro.kittycat.util.UIUtil
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
        UIUtil.setUpTheme(this)
    }

    private fun setUpLeakCanary(){
        LeakCanary.install(this);
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
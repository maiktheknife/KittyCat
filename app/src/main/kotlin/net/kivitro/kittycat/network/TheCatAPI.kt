package net.kivitro.kittycat.network

import net.kivitro.kittycat.model.Cat
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import rx.Observable

/**
 * Created by Max on 08.03.2016.
 */
interface TheCatAPI {

    @GET("images/get?format=xml&type=png&size=med&results_per_page=50")
    fun getKittens(): Observable<Cat>

    companion object {
        final val API = create()

        private fun create(): TheCatAPI {
            val api = Retrofit.Builder()
                    .baseUrl("http://thecatapi.com/api/")
                    .client(OkHttpClient())
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()
                    .create(TheCatAPI::class.java)
            return api
        }

    }

}
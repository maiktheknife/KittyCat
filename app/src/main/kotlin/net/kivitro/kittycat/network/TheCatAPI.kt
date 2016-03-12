package net.kivitro.kittycat.network

import android.content.Context
import android.preference.PreferenceManager
import android.support.annotation.IntRange
import android.support.annotation.StringDef
import android.util.Log
import net.kivitro.kittycat.BuildConfig
import net.kivitro.kittycat.model.Cat
import net.kivitro.kittycat.model.CatCategory
import net.kivitro.kittycat.model.CatVote
import net.kivitro.kittycat.model.FavResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import rx.Observable

/**
 * Created by Max on 08.03.2016.
 */
interface TheCatAPI {

    @GET("images/get?format=xml&type=png&size=med&results_per_page=50&api_key=" + BuildConfig.THE_CAT_API_KEY)
    fun getKittens(@Query("category") category: String?): Observable<Cat>

    @GET("images/vote?api_key=" + BuildConfig.THE_CAT_API_KEY)
    fun vote(@Query("image_id") image_id: String, @Query("score") @IntRange(from = 0, to = 10) score: Int): Unit

    @GET("images/getvotes?api_key=" + BuildConfig.THE_CAT_API_KEY)
    fun getVotes(): Observable<CatVote>

    @GET("images/favourite?api_key=" + BuildConfig.THE_CAT_API_KEY)
    fun favourite(@Query("image_id") image_id: String, @FavAction @Query("action") action: String): Observable<FavResponse>

    @GET("images/getfavourites?api_key=" + BuildConfig.THE_CAT_API_KEY)
    fun getFavourites(): Unit

    @GET("categories/list")
    fun getCategories(): Observable<CatCategory>

    companion object {

        lateinit var API : TheCatAPI

        fun create(c: Context): TheCatAPI {

            val interceptor = HttpLoggingInterceptor();
            interceptor.level = HttpLoggingInterceptor.Level.NONE;

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(SubIDInterceptor(c))
                .build();

            val api = Retrofit.Builder()
                    .baseUrl("http://thecatapi.com/api/")
                    .client(client)
                    .addConverterFactory(SimpleXmlConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build()
                    .create(TheCatAPI::class.java)
            API = api
            return api
        }

        @Retention(AnnotationRetention.SOURCE)
        @StringDef(
                ACTION_ADD, ACTION_REMOVE
        )
        annotation class FavAction

        const val ACTION_ADD = "add";
        const val ACTION_REMOVE = "remove";
    }

    class SubIDInterceptor(private val c : Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response? {
            Log.d("SubIDInterceptor", "intercept")
            val requestUrl = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("sub_id", PreferenceManager.getDefaultSharedPreferences(c).getInt("sub_id", 0).toString())
                    .build()

            val request = chain.request().newBuilder().url(requestUrl).build()

            return chain.proceed(request)
        }

    }

}
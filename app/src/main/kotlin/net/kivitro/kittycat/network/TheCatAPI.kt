package net.kivitro.kittycat.network

import android.content.Context
import android.preference.PreferenceManager
import android.support.annotation.IntRange
import io.reactivex.Single
import net.kivitro.kittycat.BuildConfig
import net.kivitro.kittycat.model.*
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Max on 08.03.2016.
 */
interface TheCatAPI {

	/* API/IMAGES */

	@GET("images/get?format=xml&type=png&size=med")
	fun getKittens(@Query("category") category: String?): Single<CatResponse>

	@GET("images/vote")
	fun vote(@Query("image_id") image_id: String, @Query("score") @IntRange(from = 0, to = 10) score: Int): Single<VoteResponse>

	@GET("images/getvotes")
	fun getVotes(): Single<CatResponse>

	@GET("images/favourite")
	fun favourite(@Query("image_id") image_id: String, @Query("action") action: String): Single<FavResponse>

	@GET("images/getfavourites")
	fun getFavourites(): Single<CatResponse>

	/* API/CATEGORIES */

	@GET("categories/list")
	fun getCategories(): Single<CategoryResponse>

	/* API/STATS */

	@GET("stats/getoverview")
	fun getOverview(): Single<OverviewResponse>

	companion object {
		const val ACTION_ADD = "add"
		const val ACTION_REMOVE = "remove"
		lateinit var API: TheCatAPI
			private set

		fun create(c: Context) {
			val loggingInterceptor = HttpLoggingInterceptor()
			loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC

			val client = OkHttpClient.Builder()
					.addInterceptor(loggingInterceptor)
					.addInterceptor(ApiKeyInterceptor(c))
					.build()

			API = Retrofit.Builder()
					.baseUrl("http://thecatapi.com/api/")
					.client(client)
					.addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
					.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
					.validateEagerly(true)
					.build()
					.create(TheCatAPI::class.java)
		}
	}

	private class ApiKeyInterceptor(private val c: Context) : Interceptor {
		override fun intercept(chain: Interceptor.Chain): Response? {
			val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(c)
			val requestUrl = chain.request()
					.url()
					.newBuilder()
					.addQueryParameter("api_key", BuildConfig.THE_CAT_API_KEY)
					.addQueryParameter("sub_id", defaultSharedPreferences.getInt("sub_id", 0).toString())
					.addQueryParameter("results_per_page", defaultSharedPreferences.getInt("loading_count", 20).toString())
					.build()

			val request = chain.request().newBuilder().url(requestUrl).build()
			return chain.proceed(request)
		}
	}

}



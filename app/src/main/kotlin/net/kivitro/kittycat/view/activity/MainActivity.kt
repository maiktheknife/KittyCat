package net.kivitro.kittycat.view.activity

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import butterknife.bindView
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Category
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.presenter.MainPresenter
import net.kivitro.kittycat.snack
import net.kivitro.kittycat.view.MainView
import net.kivitro.kittycat.view.adapter.CategoryAdapter
import net.kivitro.kittycat.view.adapter.KittyAdapter
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity(), MainView, SwipeRefreshLayout.OnRefreshListener {
	private lateinit var presenter: MainPresenter<MainView>
	private lateinit var layoutManager: StaggeredGridLayoutManager
	private lateinit var adapter: KittyAdapter
	private lateinit var spinnerAdapter: CategoryAdapter

	private var firstLoad: Boolean = true
	private var isConnected: Boolean = false
	private var isGridView: Boolean = true
	private var kittens: List<Image>? = null
	private var categories: List<Category>? = null

	internal val containerView: View by bindView(R.id.ac_main_container)
	internal val loadingView: View by bindView(R.id.ac_main_loading_view)
	internal val errorView: View by bindView(R.id.ac_main_error_layout)
	internal val retryBtn: View by bindView(R.id.ac_main_error_btn)
	internal val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.ac_main_swipeLayout)
	internal val recyclerView: RecyclerView by bindView(R.id.ac_main_recyclerView)
	internal val categorySpinner: Spinner by bindView(R.id.ac_main_spinner)

	private enum class State {
		LOADING, ERROR, CONTENT
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.ac_main)

		Timber.d("access %s", ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)

		setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

		presenter = MainPresenter(this)

		swipeRefreshLayout.setOnRefreshListener(this)
		swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)

		retryBtn.setOnClickListener { loadKittiesIfPossible() }

		spinnerAdapter = CategoryAdapter()
		categorySpinner.adapter = spinnerAdapter
		categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				Timber.d("onItemSelected")
				loadKitties()
			}

			override fun onNothingSelected(parent: AdapterView<*>?) {
				Timber.d("onNothingSelected")
			}
		}

		initRecyclerView(presenter)

		val preferences = PreferenceManager.getDefaultSharedPreferences(this)
		var sub_id = preferences.getInt(PREF_SUB_ID, -1)
		if (sub_id == -1) {
			sub_id = (Math.random() * 10000000).toInt()
			preferences.edit().putInt(PREF_SUB_ID, sub_id).apply()
		}
		Timber.d("sub_id %s", sub_id)

		if (savedInstanceState != null) {
			firstLoad = false
			showState(State.CONTENT)
			onKittensLoaded(savedInstanceState.getParcelableArrayList<Parcelable>(EXTRA_KITTENS) as List<Image>)
			onCategoriesLoaded(savedInstanceState.getParcelableArrayList<Parcelable>(EXTRA_CATEGORIES) as List<Category>)
		}
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		Timber.d("onSaveInstanceState")
		if (kittens != null) {
			outState.putParcelableArrayList(EXTRA_KITTENS, kittens as ArrayList<Image>)
		}
		if (categories != null) {
			outState.putParcelableArrayList(EXTRA_CATEGORIES, categories as ArrayList<Category>)
		}
	}

	override fun onResume() {
		super.onResume()
		isConnected = getConnectivityManager().activeNetworkInfo?.isConnected ?: false
		Timber.d("onResume %s %s", isConnected, firstLoad)
		if (isConnected) {
			if (firstLoad) {
				presenter.loadCategories()
				loadKitties()
			}
		} else {
			presenter.onNoConnection()
		}
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.action_toggle -> {
				toggleView(item)
				return true
			}
			R.id.action_favorites -> {
				presenter.onFavouritedClicked()
				return true
			}
			R.id.action_settings -> {
				presenter.onSettingsClicked()
				return true
			}
			else -> return super.onOptionsItemSelected(item)
		}
	}

	private fun toggleView(item: MenuItem) {
		if (isGridView) {
			layoutManager.spanCount = SPAN_LIST
			item.setIcon(R.mipmap.ic_action_grid)
			isGridView = false
		} else {
			layoutManager.spanCount = SPAN_GRID
			item.setIcon(R.mipmap.ic_action_list)
			isGridView = true
		}
	}

	private fun initRecyclerView(presenter: MainPresenter<MainView>) {
		recyclerView.setHasFixedSize(true)
		recyclerView.itemAnimator = DefaultItemAnimator()

		layoutManager = StaggeredGridLayoutManager(SPAN_GRID, StaggeredGridLayoutManager.VERTICAL)
		recyclerView.layoutManager = layoutManager

		adapter = KittyAdapter(presenter)
		recyclerView.adapter = adapter
	}

	private fun loadKittiesIfPossible() {
		isConnected = getConnectivityManager().activeNetworkInfo?.isConnected ?: false
		if (isConnected) {
			loadKitties()
		} else {
			presenter.onNoConnection()
		}
	}

	private fun loadKitties() {
		if (firstLoad) {
			showState(State.LOADING)
		}
		val category = categorySpinner.selectedItem as Category?
		if (Category.ALL == category) {
			presenter.loadKittens(null)
		} else {
			presenter.loadKittens(category?.name)
		}
	}

	private fun showState(state: State) {
		Timber.d("showState %s", state)
		when (state) {
			State.LOADING -> {
				loadingView.visibility = View.VISIBLE
				errorView.visibility = View.GONE
				swipeRefreshLayout.visibility = View.GONE
			}
			State.ERROR -> {
				loadingView.visibility = View.GONE
				errorView.visibility = View.VISIBLE
				swipeRefreshLayout.visibility = View.GONE
			}
			State.CONTENT -> {
				loadingView.visibility = View.GONE
				errorView.visibility = View.GONE
				swipeRefreshLayout.visibility = View.VISIBLE
			}
		}
	}

	/* @{link SwipeRefreshLayout.OnRefreshListener}*/

	override fun onRefresh() {
		Timber.d("onRefresh")
		loadKittiesIfPossible()
	}

	private fun getConnectivityManager(): ConnectivityManager {
		return getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
	}

	/* @{link MainView} */

	override val activity: Activity
		get() = this

	override fun onKittensLoaded(kittens: List<Image>) {
		Timber.d("onKittensLoaded %d", kittens.size)
		this.firstLoad = false
		this.kittens = kittens
		adapter.addItems(kittens)
		swipeRefreshLayout.isRefreshing = false
		showState(State.CONTENT)
	}

	override fun onKittensLoadError(message: String) {
		Timber.d("onKittensLoadError %s", message)
		containerView.snack("Loading Error: $message")
		swipeRefreshLayout.isRefreshing = false
		showState(State.ERROR)
	}

	override fun onCategoriesLoaded(categories: List<Category>) {
		Timber.d("onCategoriesLoaded %d", categories.size)
		this.categories = categories
		spinnerAdapter.addItems(categories)
	}

	override fun onCategoriesLoadError(message: String) {
		Timber.d("onCategoriesLoadError %s", message)
		containerView.snack("Loading Error: $message")
		swipeRefreshLayout.isRefreshing = false
	}

	override fun showNoConnection() {
		Timber.d("showNoConnection")
		containerView.snack("No Connection")
		swipeRefreshLayout.isRefreshing = false
		showState(State.ERROR)
	}

	companion object {
		private const val EXTRA_KITTENS = "extra_kittens"
		private const val EXTRA_CATEGORIES = "extra_categories"
		private const val PREF_SUB_ID = "sub_id"
		private const val SPAN_GRID = 2
		private const val SPAN_LIST = 1
	}

}

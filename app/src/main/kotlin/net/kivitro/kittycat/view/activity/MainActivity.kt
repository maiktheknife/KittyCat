package net.kivitro.kittycat.view.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.ac_main.*
import kotlinx.android.synthetic.main.view_content.*
import kotlinx.android.synthetic.main.view_error.*
import kotlinx.android.synthetic.main.view_loading.*
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Cat
import net.kivitro.kittycat.model.Category
import net.kivitro.kittycat.presenter.MainPresenter
import net.kivitro.kittycat.snack
import net.kivitro.kittycat.view.MainView
import net.kivitro.kittycat.view.adapter.CategoryAdapter
import net.kivitro.kittycat.view.adapter.KittyAdapter
import timber.log.Timber

class MainActivity : AppCompatActivity(), MainView, SwipeRefreshLayout.OnRefreshListener {
	private lateinit var presenter: MainPresenter
	private lateinit var layoutManager: StaggeredGridLayoutManager
	private lateinit var adapter: KittyAdapter
	private lateinit var spinnerAdapter: CategoryAdapter

	private var spinnerInit = false
	private var firstLoad = true
	private var isGridView = true
	private var selectedNavItem = 0
	private var kittens: List<Cat>? = null
	private var categories: List<Category>? = null

	private enum class State {
		LOADING, ERROR, CONTENT
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.ac_main)

		setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))

		presenter = MainPresenter()
		presenter.attachView(this)

		ac_main_swipeLayout.apply {
			setOnRefreshListener(this@MainActivity)
			setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary)
		}

		ac_main_error_btn.setOnClickListener { loadKittiesIfPossible(favourites = intent.extras?.getString("shortcut") == "favourites") }

		ac_bottom_navigation.setOnNavigationItemReselectedListener { }
		ac_bottom_navigation.setOnNavigationItemSelectedListener { menuItem ->
			when (menuItem.itemId) {
				R.id.action_random -> {
					Timber.d("bottom nav select action_random")
					selectedNavItem = 0
					ac_main_spinner.visibility = View.VISIBLE
					loadKittiesIfPossible(false)
					true
				}
				R.id.action_favorites -> {
					Timber.d("bottom nav select action_favorites")
					selectedNavItem = 1
					ac_main_spinner.visibility = View.INVISIBLE
					loadKittiesIfPossible(true)
					true
				}
				R.id.action_settings -> {
					Timber.d("bottom nav select action_settings")
					presenter.onSettingsClicked()
					false
				}
				else -> false
			}
		}

		spinnerAdapter = CategoryAdapter()
		ac_main_spinner.adapter = spinnerAdapter
		ac_main_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				Timber.d("onItemSelected")
				if (spinnerInit) {
					loadKittiesIfPossible()
				} else {
					spinnerInit = true
				}
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
			Timber.d("onCreate with savedState")
			firstLoad = false
			showState(State.CONTENT)
			onKittensLoaded(savedInstanceState.getParcelableArrayList<Parcelable>(EXTRA_KITTENS) as List<Cat>)
			onCategoriesLoaded(savedInstanceState.getParcelableArrayList<Parcelable>(EXTRA_CATEGORIES) as List<Category>)
		} else {
			Timber.d("onCreate without savedState")
			val fromFavoritesShortcut = intent.extras?.getString("shortcut") == "favourites"
			loadKittiesIfPossible(favourites = fromFavoritesShortcut)
			selectedNavItem = if (fromFavoritesShortcut) 1 else 0
			ac_bottom_navigation.menu.getItem(selectedNavItem).isChecked = true
		}
	}

	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		Timber.d("onSaveInstanceState")
		kittens?.let {
			outState.putParcelableArrayList(EXTRA_KITTENS, it as ArrayList<Cat>)
		}
		categories?.let {
			outState.putParcelableArrayList(EXTRA_CATEGORIES, it as ArrayList<Category>)
		}
	}

	override fun onStart() {
		super.onStart()
		presenter.attachView(this)
	}

	override fun onStop() {
		super.onStop()
		presenter.detachView()
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
		R.id.action_toggle -> {
			toggleView(item)
			true
		}
		else -> super.onOptionsItemSelected(item)
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

	private fun initRecyclerView(presenter: MainPresenter) {
		ac_main_recyclerView.setHasFixedSize(true)
		ac_main_recyclerView.itemAnimator = DefaultItemAnimator()

		layoutManager = StaggeredGridLayoutManager(SPAN_GRID, StaggeredGridLayoutManager.VERTICAL)
		ac_main_recyclerView.layoutManager = layoutManager

		adapter = KittyAdapter(presenter)
		ac_main_recyclerView.adapter = adapter
	}

	private fun loadKittiesIfPossible(favourites: Boolean = false) {
		if (firstLoad) {
			showState(State.LOADING)
			presenter.loadCategories()
		}
		if (favourites) {
			presenter.loadFavourites()
		} else {
			val category = ac_main_spinner.selectedItem as Category?
			if (Category.ALL == category) {
				presenter.loadKittens(null)
			} else {
				presenter.loadKittens(category?.name)
			}
		}
	}

	private fun showState(state: State) {
		Timber.d("showState %s", state)
		when (state) {
			State.LOADING -> {
				ac_main_loading_view.visibility = View.VISIBLE
				ac_main_error_layout.visibility = View.GONE
				ac_main_swipeLayout.visibility = View.GONE
			}
			State.ERROR -> {
				ac_main_loading_view.visibility = View.GONE
				ac_main_error_layout.visibility = View.VISIBLE
				ac_main_swipeLayout.visibility = View.GONE
			}
			State.CONTENT -> {
				ac_main_loading_view.visibility = View.GONE
				ac_main_error_layout.visibility = View.GONE
				ac_main_swipeLayout.visibility = View.VISIBLE
			}
		}
	}


	/* @{link SwipeRefreshLayout.OnRefreshListener}*/

	override fun onRefresh() {
		Timber.d("onRefresh")
		loadKittiesIfPossible(favourites = selectedNavItem == 1)
	}

	/* @{link MainView} */

	override val activity: Activity
		get() = this

	override fun onKittensLoaded(kittens: List<Cat>) {
		Timber.d("onKittensLoaded %d", kittens.size)
		this.firstLoad = false
		this.kittens = kittens
		adapter.addItems(kittens)
		ac_main_swipeLayout.isRefreshing = false
		showState(State.CONTENT)
	}

	override fun onKittensLoadError(message: String) {
		Timber.d("onKittensLoadError %s", message)
		ac_main_container.snack("Loading Error: $message")
		ac_main_swipeLayout.isRefreshing = false
		showState(State.ERROR)
	}

	override fun onCategoriesLoaded(categories: List<Category>) {
		Timber.d("onCategoriesLoaded %d", categories.size)
		this.categories = categories
		spinnerAdapter.addItems(categories)
	}

	override fun onCategoriesLoadError(message: String) {
		Timber.d("onCategoriesLoadError %s", message)
		ac_main_container.snack("Loading Error: $message")
		ac_main_swipeLayout.isRefreshing = false
	}

	override fun showNoConnection() {
		Timber.d("showNoConnection")
		ac_main_container.snack("No Connection")
		ac_main_swipeLayout.isRefreshing = false
		showState(State.ERROR)
	}

	companion object {
		private const val EXTRA_KITTENS = "extra_kittens"
		private const val EXTRA_CATEGORIES = "extra_categories"
		private const val PREF_SUB_ID = "sub_id"
		private const val SPAN_GRID = 2
		private const val SPAN_LIST = 1

		fun getStarterIntent(context: Context) = Intent(context, MainActivity::class.java)

	}

}

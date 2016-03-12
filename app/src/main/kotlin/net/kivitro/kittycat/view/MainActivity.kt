package net.kivitro.kittycat.view

import android.app.Activity
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Parcelable
import android.preference.PreferenceManager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import butterknife.bindView
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.presenter.MainPresenter
import net.kivitro.kittycat.view.adapter.KittyAdapter
import java.util.*

class MainActivity : AppCompatActivity(), MainView, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var adapter: KittyAdapter
    private lateinit var presenter: MainPresenter
    private lateinit var layoutManager: StaggeredGridLayoutManager

    private var hasLoaded: Boolean = false
    private var isConnected: Boolean = false
    private var isGridView: Boolean = true
    private var kittens: List<Image>? = null

    internal val containerView: View by bindView(R.id.ac_main_container)
    internal val swipeRefreshLayout: SwipeRefreshLayout by bindView(R.id.ac_main_swipeLayout)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_main)

        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        presenter = MainPresenter()
        presenter.attachView(this)

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

        initRecyclerView(presenter)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this);
        var sub_id = preferences.getInt(PREF_SUB_ID, -1)
        if (sub_id == -1) {
            sub_id = (Math.random() * 10000000).toInt();
            preferences.edit().putInt(PREF_SUB_ID, sub_id).apply()
        }

        if (savedInstanceState != null) {
            hasLoaded = true
            kittens = savedInstanceState.getParcelableArrayList<Parcelable>(EXTRA_KITTENS) as List<Image>
            onKittensLoaded(kittens!!)
        } else {
            hasLoaded = false
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.d(TAG, "onSaveInstanceState ${kittens!!.size}")
        outState.putParcelableArrayList(EXTRA_KITTENS, kittens as ArrayList<Image>)
    }

    override fun onResume() {
        super.onResume()
        isConnected = getConnectivityManager().activeNetworkInfo?.isConnected ?: false
        Log.d(TAG, "onResume $isConnected $hasLoaded")
        if (isConnected) {
            if (!hasLoaded) {
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
            item.setIcon(R.mipmap.ic_action_list)
            isGridView = false
        } else {
            layoutManager.spanCount = SPAN_GRID
            item.setIcon(R.mipmap.ic_action_grid)
            isGridView = true
        }
    }

    private fun initRecyclerView(presenter: MainPresenter) {
        val recyclerView = findViewById(R.id.ac_main_recyclerView) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = DefaultItemAnimator()

        layoutManager = StaggeredGridLayoutManager(SPAN_GRID, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = layoutManager

        adapter = KittyAdapter(presenter)
        recyclerView.adapter = adapter
    }

    private fun loadKitties() {
        presenter.loadKittens()
    }

    /* @{link SwipeRefreshLayout.OnRefreshListener}*/

    override fun onRefresh() {
        Log.d(TAG, "onRefresh")
        isConnected = getConnectivityManager().activeNetworkInfo?.isConnected ?: false
        if (isConnected) {
            loadKitties()
        } else {
            presenter.onNoConnection()
        }
    }

    private fun getConnectivityManager(): ConnectivityManager {
        return getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    /* @{link MainView} */

    override fun getActivity(): Activity {
        return this
    }

    override fun getContainerView(): View {
        return containerView
    }

    override fun getSwipeLayout(): SwipeRefreshLayout {
        return swipeRefreshLayout
    }

    override fun onKittensLoaded(kittens: List<Image>) {
        Log.d(TAG, "onKittensLoaded: ${kittens.size}")
        this.hasLoaded = true
        this.kittens = kittens
        adapter.addItems(kittens)
    }

    companion object {
        private final val TAG = MainActivity::class.java.name
        private const val EXTRA_KITTENS = "extra_kittens"
        private const val PREF_SUB_ID = "sub_id"
        private const val SPAN_GRID = 2
        private const val SPAN_LIST = 1
    }

}

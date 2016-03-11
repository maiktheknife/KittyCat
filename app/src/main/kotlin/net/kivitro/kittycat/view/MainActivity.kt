package net.kivitro.kittycat.view

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.View
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.presenter.MainPresenter
import net.kivitro.kittycat.view.adapter.KittyAdapter

class MainActivity : AppCompatActivity(), MainView {

    lateinit var adapter: KittyAdapter
    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_main)

        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        presenter = MainPresenter()
        presenter.attachView(this)

        initRecyclerView(presenter)
        loadKitties()

        (findViewById(R.id.ac_main_fab) as FloatingActionButton).setOnClickListener { view ->
            presenter.onFABClicked();
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private fun initRecyclerView(presenter: MainPresenter) {
        val recyclerView = findViewById(R.id.ac_main_recyclerView) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = KittyAdapter(presenter)
        recyclerView.adapter = adapter
    }

    private fun loadKitties() {
        presenter.loadKittens()
    }

    override fun onKittensLoaded(kittens: List<Image>) {
        Log.d(TAG, "onKittensLoaded")
        adapter.addItems(kittens)
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun getMainView(): View {
        return findViewById(R.id.ac_main_container)
    }

    companion object {
        val TAG = MainActivity::class.java.name
    }

}

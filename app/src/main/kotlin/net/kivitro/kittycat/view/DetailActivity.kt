package net.kivitro.kittycat.view

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.bindView
import com.squareup.picasso.Picasso
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.presenter.DetailPresenter

/**
 * Created by Max on 10.03.2016.
 */
class DetailActivity : AppCompatActivity(), DetailView {
    val containerView: View by bindView(R.id.ac_detail_container)
    val fab: FloatingActionButton by bindView(R.id.ac_detail_favourite)

    lateinit var presenter: DetailPresenter
    lateinit var cat: Image

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_detail)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        presenter = DetailPresenter()
        presenter.attachView(this)

        fab.setOnClickListener { view -> onFabClicked() }

        cat = intent.getParcelableExtra<Image>(EXTRA_CAT)
        val txtID = findViewById(R.id.ac_detail_id) as TextView
        val txtURL = findViewById(R.id.ac_detail_url) as TextView
        val txtSourceURL = findViewById(R.id.ac_detail_source_url) as TextView
        val txtImage = findViewById(R.id.ac_detail_image) as ImageView

        txtID.text = cat.id
        txtURL.text = cat.url
        txtSourceURL.text = cat.source_url

        Picasso
                .with(this)
                .load(cat.url)
                .into(txtImage)
    }

    fun onFabClicked() {
        Log.d(TAG, "onFabClicked")
        presenter.onFABClicked(cat.id!!);
    }

    /* @{link DetailView} */

    override fun getActivity(): Activity {
        return this
    }

    override fun getMainView(): View {
        return containerView
    }

    override fun getFABView(): FloatingActionButton {
        return fab
    }

    companion object {
        private val TAG = DetailActivity::class.java.name
        const val EXTRA_CAT = "extra_cat"
    }
}
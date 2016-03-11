package net.kivitro.kittycat.view

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.presenter.DetailPresenter

/**
 * Created by Max on 10.03.2016.
 */
class DetailActivity : AppCompatActivity(), DetailView {

    lateinit var presenter: DetailPresenter
    var containerView: View? = null
    var fab: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_detail)
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

        presenter = DetailPresenter()
        presenter.attachView(this)

        containerView = findViewById(R.id.ac_detail_container)

        fab = findViewById(R.id.ac_detail_favourite) as FloatingActionButton
        fab!!.setOnClickListener { view ->
            presenter.onFABClicked();
        }

        val cat = intent.getParcelableExtra<Image>(EXTRA_CAT)
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

    /* @{link DetailView} */

    override fun getActivity(): Activity {
        return this
    }

    override fun getMainView(): View {
        return containerView!!
    }

    override fun getFABView(): FloatingActionButton {
        return fab!!
    }

    companion object {
        private val TAG = DetailActivity::class.java.name
        final val EXTRA_CAT = "extra_cat"
    }
}
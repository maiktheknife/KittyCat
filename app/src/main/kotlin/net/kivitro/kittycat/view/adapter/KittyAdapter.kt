package net.kivitro.kittycat.view.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.presenter.MainPresenter

/**
 * Created by Max on 08.03.2016.
 */
class KittyAdapter(val presenter: MainPresenter) : RecyclerView.Adapter<KittyAdapter.KittyHolder>() {

    private var cats: MutableList<Image> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KittyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cat, parent, false)
        return KittyHolder(view, object : KittyHolder.KittyActions {
            override fun onKittyClicked(view: View, pos: Int) {
                presenter.onKittyClicked(view, cats[pos])
            }
        })
    }

    override fun getItemCount(): Int {
        return cats.size
    }

    override fun onBindViewHolder(holder: KittyHolder, position: Int) {
        val cat = cats[position]
        holder.id.text = cat.id
        Picasso
            .with(holder.itemView.context)
            .load(cat.url)
            .into(holder.image)
    }

    fun addItems(cats: List<Image>) {
        Log.d(TAG, "addItems: ${cats.size}")
        this.cats.clear();
        this.cats.addAll(cats)
        notifyItemRangeChanged(0, this.cats.size);
    }

    class KittyHolder(view: View, callback: KittyActions) : RecyclerView.ViewHolder(view) {

        interface KittyActions {
            fun onKittyClicked(view: View, pos: Int): Unit
        }

        val image = view.findViewById(R.id.cat_row_image) as ImageView
        val id = view.findViewById(R.id.cat_row_id) as TextView

        init {
            view.setOnClickListener { v -> callback.onKittyClicked(itemView, adapterPosition) }
        }
    }

    companion object {
        private val TAG = KittyAdapter::class.java.simpleName
    }

}
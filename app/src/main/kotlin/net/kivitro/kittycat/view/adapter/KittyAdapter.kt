package net.kivitro.kittycat.view.adapter

import android.graphics.drawable.BitmapDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.graphics.Palette
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import net.kivitro.kittycat.R
import net.kivitro.kittycat.loadUrl
import net.kivitro.kittycat.model.Image
import net.kivitro.kittycat.presenter.MainPresenter
import net.kivitro.kittycat.view.MainView
import timber.log.Timber

/**
 * Created by Max on 08.03.2016.
 */
class KittyAdapter(val presenter: MainPresenter<MainView>) : RecyclerView.Adapter<KittyAdapter.KittyHolder>() {
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
		val context = holder.itemView.context
		val cat = cats[position]
		holder.id.text = cat.id
		holder.image.loadUrl(cat.url!!, callback = {
			val bitmap = ((holder.image.drawable) as BitmapDrawable).bitmap
			Palette.from(bitmap).generate { palette ->
				val color = palette.getMutedColor(ContextCompat.getColor(context, R.color.colorPrimary))
				holder.id.setTextColor(color)
			}
		})
	}

	fun addItems(catss: List<Image>) {
		Timber.d("addItems: ${cats.size} -> ${catss.size}")
		cats.clear()
		cats.addAll(catss)
		notifyDataSetChanged()
	}

	class KittyHolder(view: View, callback: KittyActions) : RecyclerView.ViewHolder(view) {

		interface KittyActions {
			fun onKittyClicked(view: View, pos: Int)
		}

		val image = view.findViewById(R.id.cat_row_image) as ImageView
		val id = view.findViewById(R.id.cat_row_id) as TextView

		init {
			view.setOnClickListener { v -> callback.onKittyClicked(itemView, adapterPosition) }
		}
	}

}
package net.kivitro.kittycat.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import net.kivitro.kittycat.R
import net.kivitro.kittycat.model.Category
import timber.log.Timber

/**
 * Created by Max on 10.04.2016.
 */
class CategoryAdapter : BaseAdapter() {
	private var categories: MutableList<Category> = arrayListOf()

	override fun getCount(): Int = categories.size

	override fun getItem(position: Int): Category = categories[position]

	override fun getItemId(position: Int): Long = 0

	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
		val v = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.item_spinner_item, parent, false)
		v.findViewById<TextView>(android.R.id.text1).text = getItem(position).name
		return v
	}

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
		val v = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.item_spinner, parent, false)
		v.findViewById<TextView>(android.R.id.text1).text = getItem(position).name
		return v
	}

	fun addItems(categories: List<Category>) {
		Timber.d("addItems %d", categories.size)
		this.categories.apply {
			clear()
			add(Category.ALL)
			addAll(categories)
		}
		notifyDataSetChanged()
	}

}

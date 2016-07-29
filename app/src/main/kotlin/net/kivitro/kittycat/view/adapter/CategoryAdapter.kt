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
class CategoryAdapter() : BaseAdapter() {
    private var categories: MutableList<Category> = arrayListOf()

    override fun getCount(): Int {
        return categories.size
    }

    override fun getItem(position: Int): Category {
        return categories[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val v: View = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.item_spinner_item, parent, false)
        val text = v.findViewById(android.R.id.text1) as TextView
        text.text = getItem(position).name
        return v
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val v: View = convertView ?: LayoutInflater.from(parent.context).inflate(R.layout.item_spinner, parent, false)
        val text = v.findViewById(android.R.id.text1) as TextView
        text.text = getItem(position).name
        return v
    }

    fun addItems(categories: List<Category>) {
        Timber.d("addItems %f", categories.size)
        this.categories.clear()
        this.categories.add(Category.ALL)
        this.categories.addAll(categories)
        notifyDataSetChanged()
    }

}

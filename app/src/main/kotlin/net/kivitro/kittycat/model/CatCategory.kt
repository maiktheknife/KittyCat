package net.kivitro.kittycat.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Max on 11.03.2016.
 */
data class CatCategory(var data: Categories? = null)

data class Categories(var categories: List<Category>? = null)

data class Category(var id: Int? = -1, var name: String? = ""): Parcelable {

    private constructor(p: Parcel) : this(p.readInt(), p.readString())

    override fun writeToParcel(p: Parcel, p1: Int) {
        p.writeInt(id!!)
        p.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Category(id: $id, name=$name)"
    }

    companion object {
        @JvmField
        final val CREATOR = object : Parcelable.Creator<Category> {
            override fun createFromParcel(source: Parcel): Category {
                return Category(source)
            }

            override fun newArray(size: Int): Array<Category?> {
                return arrayOfNulls(size)
            }
        }
        val ALL: Category = Category(0, "All")
    }
}
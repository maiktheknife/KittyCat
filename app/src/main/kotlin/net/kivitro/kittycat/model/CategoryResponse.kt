package net.kivitro.kittycat.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Max on 11.03.2016.
 */

/*
<response>
	<data>
		<categories>
			<category>
				<id>1</id>
				<name>hats</name>
			</category>
			<category>
				<id>2</id>
				<name>space</name>
			</category>
		</categories>
	</data>
</response>
 */
data class CategoryResponse(var data: CategoryList? = null)

data class CategoryList(var categories: List<Category>? = null)

data class Category(var id: Int? = -1, var name: String? = "") : Parcelable {

	private constructor(p: Parcel) : this(p.readInt(), p.readString())

	override fun writeToParcel(p: Parcel, p1: Int) {
		p.writeInt(id!!)
		p.writeString(name)
	}

	override fun describeContents() = 0

	override fun toString() = "Category(id: $id, name=$name)"

	companion object {
		@JvmField
		val CREATOR = object : Parcelable.Creator<Category> {
			override fun createFromParcel(source: Parcel) = Category(source)

			override fun newArray(size: Int): Array<Category?> = arrayOfNulls(size)
		}
		val ALL: Category = Category(0, "All")
	}
}
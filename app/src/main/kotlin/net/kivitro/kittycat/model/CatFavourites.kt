package net.kivitro.kittycat.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Max on 15.02.2017.
 */

data class CatFavourites(var data: CatFavouritesData? = null)

data class CatFavouritesData(var images: List<CatFavouritesImage>? = null)

data class CatFavouritesImage(var id: String? = "",
                              var url: String? = "",
                              var created: String? = "") : Parcelable {

	private constructor(p: Parcel) : this(p.readString(), p.readString(), p.readString())

	override fun writeToParcel(p: Parcel, p1: Int) {
		p.writeString(id)
		p.writeString(url)
		p.writeString(created)
	}

	override fun describeContents(): Int {
		return 0
	}

	override fun toString(): String {
		return "Cat(id: $id, url=$url, created=$created)"
	}

	companion object {
		@JvmField
		val CREATOR = object : Parcelable.Creator<CatFavouritesImage> {
			override fun createFromParcel(source: Parcel): CatFavouritesImage {
				return CatFavouritesImage(source)
			}

			override fun newArray(size: Int): Array<CatFavouritesImage?> {
				return arrayOfNulls(size)
			}
		}
	}

}

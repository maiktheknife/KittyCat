package net.kivitro.kittycat.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Max on 08.03.2016.
 */

data class Cat(var data: Data? = null)

data class Data(var images: List<Image>? = null)

data class Image(var url: String? = "", /* var favourite: Boolean?, var score: Int?, */ var id: String? = "", var source_url: String? = "") : Parcelable {

    private constructor(p: Parcel) : this(p.readString(), /* false, 0,*/ p.readString(), p.readString())

    override fun writeToParcel(p: Parcel, p1: Int) {
        p.writeString(url)
        p.writeString(id)
        p.writeString(source_url)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun toString(): String {
        return "Cat(id: $id, url=$url, source_url=$source_url)"
    }

    companion object {
        @JvmField
        final val CREATOR = object : Parcelable.Creator<Image> {
            override fun createFromParcel(source: Parcel): Image {
                return Image(source)
            }

            override fun newArray(size: Int): Array<Image?> {
                return arrayOfNulls(size)
            }
        }
    }

}

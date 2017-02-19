package net.kivitro.kittycat.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Max on 08.03.2016.
 */

//              GET
//<image>
//	<url>http://24.media.tumblr.com/tumblr_lun2ysP9CZ1r62m14o1_500.png</url>
//	<id>dt7</id>
//	<source_url>http://thecatapi.com/?id=dt7</source_url>
//</image>
//
//              GET VOTES
//<image>
//	<sub_id>12345</sub_id>
//	<created>2017-02-16 19:52:46</created>      vote date
//	<score>10</score>
//</image>
//
//              GET FAVS
//<image>
//	<id>1tq</id>
//	<url>http://25.media.tumblr.com/tumblr_ls5cvtY8Jn1qi23vmo1_500.png</url>
//	<created>2016-03-12 11:22:49</created>      fav date
//</image>

data class CatResponse(var data: CatData? = null)

data class CatData(var images: List<Cat>? = null)

data class Cat(
		var url: String? = "",
		var favourite: Boolean? = false,
		var score: Int? = 0,
		var id: String? = "",
		var source_url: String? = "",
		var created : String = "") : Parcelable {

	private constructor(p: Parcel) : this(p.readString(), p.readInt() == 0, p.readInt(), p.readString(), p.readString(), p.readString())

	override fun writeToParcel(p: Parcel, p1: Int) {
		p.writeString(url)
		if (favourite == true) {
			p.writeInt(0)
		} else {
			p.writeInt(1)
		}
		p.writeInt(score!!)
		p.writeString(id)
		p.writeString(source_url)
		p.writeString(created)
	}

	override fun describeContents(): Int {
		return 0
	}

	override fun toString(): String {
		return "Cat(id: $id, url=$url, source_url=$source_url, favourite=$favourite, score=$score, created=$created)"
	}

	companion object {
		@JvmField
		val CREATOR = object : Parcelable.Creator<Cat> {
			override fun createFromParcel(source: Parcel): Cat {
				return Cat(source)
			}

			override fun newArray(size: Int): Array<Cat?> {
				return arrayOfNulls(size)
			}
		}
	}

}

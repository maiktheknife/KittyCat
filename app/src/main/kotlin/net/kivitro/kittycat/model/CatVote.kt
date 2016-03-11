package net.kivitro.kittycat.model

import java.util.*

/**
 * Created by Max on 11.03.2016.
 */
data class CatVote(var data: DataVote? = null)

data class DataVote(var images: List<ImageVote>? = null)

data class ImageVote(var sub_id: String?, var created: Date?, var score: Int?)
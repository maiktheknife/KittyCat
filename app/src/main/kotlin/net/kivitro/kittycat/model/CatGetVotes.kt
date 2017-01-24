package net.kivitro.kittycat.model

import java.util.*

/**
 * Created by Max on 11.03.2016.
 */
data class CatGetVote(var data: CatGetVoteData? = null)

data class CatGetVoteData(var images: List<CatGetVoteImage>? = null)

data class CatGetVoteImage(var sub_id: String?, var created: Date?, var score: Int?)
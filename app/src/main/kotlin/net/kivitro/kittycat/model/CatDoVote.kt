package net.kivitro.kittycat.model

/**
 * Created by Max on 11.03.2016.
 */
data class CatDoVote(var data: CatDoVotes? = null)

data class CatDoVotes(var votes: List<CatDoVoteResult>? = null)

data class CatDoVoteResult(var score: String?, var image_id: String?, var sub_id: String?, var action: String?)
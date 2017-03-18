package net.kivitro.kittycat.model

/**
 * Created by Max on 11.03.2016.
 */

/*
<response>
	<data>
		<votes>
			<vote>
				<score>10</score>
				<image_id>bC24</image_id>
				<sub_id>12345</sub_id>
				<action>update</action>
			</vote>
		</votes>
	</data>
</response>
 */
data class VoteResponse(var data: VoteData? = null)

data class VoteData(var images: List<VoteResult>? = null)

data class VoteResult(var score: String?, var image_id: String?, var sub_id: String?, var action: String?)
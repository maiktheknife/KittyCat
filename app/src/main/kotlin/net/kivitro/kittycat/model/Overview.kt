package net.kivitro.kittycat.model

/**
 * Created by Max on 17.02.2017.
 */

//<response>
//	<data>
//		<stats>
//			<statsoverview>
//				<total_get_requests>0</total_get_requests>
//				<total_votes>318</total_votes>
//				<total_favourites>269</total_favourites>
//			</statsoverview>
//		</stats>
//	</data>
//</response>
data class OverviewResponse(var data: CatData? = null)

data class OverviewData(var stats: List<Cat>? = null)

data class Overview(
		var total_get_requests: Int? = 0,
		var total_votes: Int? = 0,
		var total_favourites: Int? = 0)
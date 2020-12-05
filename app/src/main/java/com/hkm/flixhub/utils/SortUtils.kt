package com.hkm.flixhub.utils

object SortUtils {
    const val POPULARITY = "popularity.desc"
    const val ORIGINAL_TITLE = "original_title.asc"
    const val SCORE = "vote_average.desc"
    const val VOTE_COUNT = "vote_count.desc"
    fun getSortedQuery(filter: String, type: String, page: Int): String {
        val simpleQuery = StringBuilder().append("SELECT * FROM show_entities where type = ")
        when (filter) {
            POPULARITY -> {
                return "popularity.desc"
//                simpleQuery.append(type)
//                simpleQuery.append(" ORDER BY showId DESC LIMIT ")
//                simpleQuery.append(page.toString())
            }
            ORIGINAL_TITLE -> {
                return "original_title.asc"
//                simpleQuery.append(type)
//                simpleQuery.append(" ORDER BY showId ASC LIMIT ")
//                simpleQuery.append(page.toString())
            }
            SCORE -> {
                return "vote_average.desc"
//                simpleQuery.append(type)
//                simpleQuery.append(" ORDER BY showId ASC LIMIT ")
//                simpleQuery.append(page.toString())
            }
            VOTE_COUNT -> {
                return "vote_count.desc"
//                simpleQuery.append(type)
//                simpleQuery.append(" ORDER BY RANDOM() LIMIT ")
//                simpleQuery.append(page.toString())
            }
            else -> return "showId DESC"
        }
//        return SimpleSQLiteQuery(simpleQuery.toString())
    }
}
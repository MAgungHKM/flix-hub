package com.hkm.flixhub.utils

import androidx.sqlite.db.SimpleSQLiteQuery

object SortUtils {
    const val POPULARITY = "popularity.desc"
    const val ORIGINAL_TITLE = "original_title.asc"
    const val SCORE = "vote_average.desc"
    const val VOTE_COUNT = "vote_count.desc"
    const val DEFAULT = "Default"
    const val TITLE_ASC = "Title (A-z)"
    const val TITLE_DESC = "Title (Z-a)"
    const val SCORE_HIGHEST = "Score (Highest)"
    const val SCORE_LOWEST = "Score (Lowest)"

    fun getSortedQuery(filter: String, type: String, page: Int): SimpleSQLiteQuery {
        val simpleQuery =
            StringBuilder().append("SELECT * FROM show_entities where favorited = 1 AND type = ")
        when (filter) {
            DEFAULT -> {
                simpleQuery.append("\'$type\'")
                simpleQuery.append(" LIMIT ")
                simpleQuery.append(page.toString())
            }
            TITLE_ASC -> {
                simpleQuery.append("\'$type\'")
                simpleQuery.append(" ORDER BY title ASC LIMIT ")
                simpleQuery.append(page.toString())
            }
            TITLE_DESC -> {
                simpleQuery.append("\'$type\'")
                simpleQuery.append(" ORDER BY title DESC LIMIT ")
                simpleQuery.append(page.toString())
            }
            SCORE_HIGHEST -> {
                simpleQuery.append("\'$type\'")
                simpleQuery.append(" ORDER BY score DESC LIMIT ")
                simpleQuery.append(page.toString())
            }
            SCORE_LOWEST -> {
                simpleQuery.append("\'$type\'")
                simpleQuery.append(" ORDER BY score ASC LIMIT ")
                simpleQuery.append(page.toString())
            }
        }
        return SimpleSQLiteQuery(simpleQuery.toString())
    }
}
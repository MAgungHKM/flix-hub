package com.hkm.flixhub.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class MovieDetailResponse(

	@field:SerializedName("backdrop_path")
	val backdropPath: String,

	@field:SerializedName("overview")
	val overview: String,

	@field:SerializedName("release_date")
	val releaseDate: String,

	@field:SerializedName("genres")
	val genres: List<GenresItem>,

	@field:SerializedName("vote_average")
	val voteAverage: Double,

	@field:SerializedName("id")
	val id: Int,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("tagline")
	val tagline: String,

	@field:SerializedName("poster_path")
	val posterPath: String
)

data class TvShowDetailResponse(

		@field:SerializedName("backdrop_path")
		val backdropPath: String,

		@field:SerializedName("first_air_date")
		val firstAirDate: String,

		@field:SerializedName("overview")
		val overview: String,

		@field:SerializedName("genres")
		val genres: List<GenresItem>,

		@field:SerializedName("vote_average")
		val voteAverage: Double,

		@field:SerializedName("name")
		val name: String,

		@field:SerializedName("tagline")
		val tagline: String,

		@field:SerializedName("id")
		val id: Int,

		@field:SerializedName("created_by")
		val createdBy: List<CreatedByItem>,

		@field:SerializedName("poster_path")
		val posterPath: String
)

data class GenresItem(

		@field:SerializedName("name")
		val name: String,

		@field:SerializedName("id")
		val id: Int
)

data class CreatedByItem(

		@field:SerializedName("gender")
		val gender: Int,

		@field:SerializedName("credit_id")
		val creditId: String,

		@field:SerializedName("name")
		val name: String,

		@field:SerializedName("profile_path")
		val profilePath: String,

		@field:SerializedName("id")
		val id: Int
)

data class MovieCreditsResponse(

		@field:SerializedName("id")
		val id: Int,

		@field:SerializedName("crew")
		val crew: List<CrewItem>
)

data class CrewItem(

		@field:SerializedName("known_for_department")
		val knownForDepartment: String,

		@field:SerializedName("name")
		val name: String,

		@field:SerializedName("department")
		val department: String,

		@field:SerializedName("job")
		val job: String
)

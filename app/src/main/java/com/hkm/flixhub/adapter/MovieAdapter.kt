package com.hkm.flixhub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hkm.flixhub.R
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.databinding.ItemsMovieBinding
import com.hkm.flixhub.ui.detail.DetailFragment

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    private lateinit var onClickListener: OnClickListener
    private var listShows = ArrayList<ShowEntity>()

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun setMovies(shows: ArrayList<ShowEntity>?) {
        if (shows.isNullOrEmpty()) return
        listShows.clear()
        listShows.addAll(shows)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemBinding =
            ItemsMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = listShows[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = listShows.size

    inner class MovieViewHolder(private val itemBinding: ItemsMovieBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(movie: ShowEntity) {
            with(itemBinding) {
                tvMovieName.text = movie.title
                root.setOnClickListener { onClickListener.onClick(movie) }
                Glide.with(root.context)
                    .load(movie.posterPath)
                    .apply(
                        RequestOptions
                            .placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                            .override(DetailFragment.IMG_POSTER_WIDTH,
                                DetailFragment.IMG_POSTER_HEIGHT)
                    )
                    .into(imgMovie)
                imgMovie.clipToOutline = true
            }
        }
    }

    interface OnClickListener {
        fun onClick(show: ShowEntity)
    }
}
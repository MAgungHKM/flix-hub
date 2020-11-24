package com.hkm.flixhub.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hkm.flixhub.R
import com.hkm.flixhub.databinding.ItemsTvShowBinding
import com.hkm.flixhub.entity.ShowEntity

class TvShowAdapter : RecyclerView.Adapter<TvShowAdapter.TvShowViewHolder>() {
    private lateinit var onClickListener: OnClickListener
    private var listShows = ArrayList<ShowEntity>()

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun setTvShows(shows: ArrayList<ShowEntity>?) {
        if (shows.isNullOrEmpty()) return
        listShows.clear()
        listShows.addAll(shows)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowViewHolder {
        val itemBinding =
            ItemsTvShowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TvShowViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: TvShowViewHolder, position: Int) {
        val movie = listShows[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int = listShows.size

    inner class TvShowViewHolder(private val itemBinding: ItemsTvShowBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(tvShow: ShowEntity) {
            with(itemBinding) {
                tvShowName.text = tvShow.title
                root.setOnClickListener { onClickListener.onClick(tvShow) }
                Glide.with(root.context)
                    .load(tvShow.imagePath)
                    .apply(
                        RequestOptions
                            .placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                            .override(250, 500)
                    )
                    .into(imgTvShow)
                imgTvShow.clipToOutline = true
            }
        }
    }

    interface OnClickListener {
        fun onClick(show: ShowEntity)
    }
}
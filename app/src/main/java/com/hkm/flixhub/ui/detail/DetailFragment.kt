package com.hkm.flixhub.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hkm.flixhub.R
import com.hkm.flixhub.data.source.local.entity.DetailShowEntity
import com.hkm.flixhub.databinding.FragmentDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment() {
    companion object {
        const val TYPE_MOVIE = "movie"
        const val TYPE_TV_SHOW = "tv_show"
    }

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding as FragmentDetailBinding
    private val mDetailFragmentArgs: DetailFragmentArgs by navArgs()

    // Lazy Inject ViewModel
    private val viewModel: DetailViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            (activity as AppCompatActivity).supportActionBar?.title = mDetailFragmentArgs.showTitle
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

            binding.progressBarDetail.visibility = View.VISIBLE

            val showId = mDetailFragmentArgs.showId
            if (showId != null)
                viewModel.setSelectedShow(showId)

            val showType = mDetailFragmentArgs.showType
            if (showType != null)
                viewModel.setSelectedShowType(showType)

            viewModel.getShowDetail().observe(viewLifecycleOwner, { detail ->
                with(detail) {
                    if (errorMessage != "null")
                        Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_LONG)
                            .show()
                }

                if (detail.director == "null" && detail.errorMessage == "null") {
                    binding.textDirected.visibility = View.GONE
                    binding.tvDirector.visibility = View.GONE
                }

                if (detail.quote == "null") {
                    binding.tvQuote.visibility = View.GONE
                }

                binding.progressBarDetail.visibility = View.GONE
                populateView(detail)
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                (activity as AppCompatActivity).onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun populateView(detail: DetailShowEntity) {
        with(binding) {
            tvTitle.text = detail.title
            tvDate.text = detail.releaseDate
            tvDirector.text = detail.director
            tvGenre.text = detail.genre
            tvQuote.text = detail.quote
            tvScore.text = detail.score
            tvSynopsis.text = detail.synopsis

            Glide.with(this@DetailFragment)
                .load(detail.posterPath)
                .apply(
                    RequestOptions
                        .placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                        .override(250, 500)
                )
                .into(imgPoster)
            imgPoster.clipToOutline = true

            Glide.with(this@DetailFragment)
                .load(detail.bannerPath)
                .apply(
                    RequestOptions
                        .placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                        .override(500, 750)
                )
                .into(imgBanner)
            imgBanner.clipToOutline = true

            iconShare.setOnClickListener { setOnClickShare(detail.title, detail.showId) }
        }
    }

    private fun setOnClickShare(title: String, showId: String) {
        val mimeType = "text/plain"
        ShareCompat.IntentBuilder.from(requireActivity()).apply {
            setType(mimeType)
            setChooserTitle(getString(R.string.share_title))

            val showType = mDetailFragmentArgs.showType
            if (showType == TYPE_TV_SHOW)
                setText(resources.getString(R.string.share_tv_show, title, showId))
            else
                setText(resources.getString(R.string.share_movie, title, showId))

            startChooser()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
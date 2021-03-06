package com.hkm.flixhub.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hkm.flixhub.R
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.databinding.FragmentDetailBinding
import com.hkm.flixhub.utils.ShowType.TYPE_TV_SHOW
import com.hkm.flixhub.vo.Status
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailFragment : Fragment() {
    companion object {
        const val IMG_POSTER_WIDTH = 250
        const val IMG_POSTER_HEIGHT = 500
        const val IMG_BANNER_WIDTH = 500
        const val IMG_BANNER_HEIGHT = 750
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

            val showId = mDetailFragmentArgs.showId
            if (showId != null)
                viewModel.setSelectedShow(showId)

            val showType = mDetailFragmentArgs.showType
            if (showType != null)
                viewModel.setSelectedShowType(showType)

            viewModel.getShowDetail().observe(viewLifecycleOwner, { show ->
                when (show.status) {
                    Status.LOADING -> binding.progressBarDetail.visibility = View.VISIBLE
                    Status.SUCCESS -> {
                        if (show.data != null) {
                            with(show.data) {
                                if (this.errorMessage != "null")
                                    Toast.makeText(requireActivity(),
                                        this.errorMessage,
                                        Toast.LENGTH_LONG)
                                        .show()
                            }

                            if (show.data.director == "null" && show.data.errorMessage == "null") {
                                binding.textDirected.visibility = View.GONE
                                binding.tvDirector.visibility = View.GONE
                            }

                            if (show.data.quote == "null") {
                                binding.tvQuote.visibility = View.GONE
                            }

                            val state = show.data.favorited
                            setFavoriteState(state)

                            binding.progressBarDetail.visibility = View.GONE
                            populateView(show.data)
                        }
                    }
                    Status.ERROR -> {
                        binding.progressBarDetail.visibility = View.GONE
                        Toast.makeText(context, show.data?.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                (activity as AppCompatActivity).onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun populateView(show: ShowEntity?) {
        if (show != null) {
            with(binding) {
                tvTitle.text = show.title
                tvDate.text = show.releaseDate
                tvDirector.text = show.director
                tvGenre.text = show.genre
                tvQuote.text = show.quote
                tvScore.text = show.score
                tvSynopsis.text = show.synopsis

                Glide.with(this@DetailFragment)
                    .load(show.posterPath)
                    .apply(
                        RequestOptions
                            .placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                            .override(IMG_POSTER_WIDTH, IMG_POSTER_HEIGHT)
                    )
                    .into(imgPoster)
                imgPoster.clipToOutline = true

                Glide.with(this@DetailFragment)
                    .load(show.bannerPath)
                    .apply(
                        RequestOptions
                            .placeholderOf(R.drawable.ic_loading)
                            .error(R.drawable.ic_error)
                            .override(IMG_BANNER_WIDTH, IMG_BANNER_HEIGHT)
                    )
                    .into(imgBanner)
                imgBanner.clipToOutline = true

                btnShare.setOnClickListener {
                    val anim = AnimationUtils.loadAnimation(it.context, R.anim.button_click_anim)
                    btnShare.startAnimation(anim).also {
                        setOnClickShare(show.title, show.showId)
                    }
                }

                btnFavorite.setOnClickListener {
                    val anim = AnimationUtils.loadAnimation(it.context, R.anim.button_click_anim)
                    btnFavorite.startAnimation(anim)
                    if (viewModel.setFavorite())
                        Toast.makeText(requireActivity(),
                            getString(R.string.text_add_favorite, show.title),
                            Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(requireActivity(),
                            getString(R.string.text_remove_favorite, show.title),
                            Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setFavoriteState(state: Boolean) {
        if (state)
            binding.btnFavorite.setImageResource(R.drawable.ic_favorited_white)
        else
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_white)
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
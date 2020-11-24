package com.hkm.flixhub.ui.detail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hkm.flixhub.R
import com.hkm.flixhub.databinding.FragmentDetailBinding
import com.hkm.flixhub.entity.ShowEntity
import com.hkm.flixhub.utils.OnMyFragmentListener

class DetailFragment : Fragment() {
    companion object {
        const val TYPE_MOVIE = "movie"
        const val TYPE_TV_SHOW = "tv_show"
    }

    private lateinit var mOnMyFragmentListener: OnMyFragmentListener
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding as FragmentDetailBinding
    private val mDetailFragmentArgs: DetailFragmentArgs by navArgs()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMyFragmentListener) {
            mOnMyFragmentListener = context
        } else {
            throw RuntimeException(
                "$context must implement OnFragmentInteractionListener"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            mOnMyFragmentListener.onChangeToolbarDisplayHome(true)
            mOnMyFragmentListener.onChangeToolbarTitle(mDetailFragmentArgs.showTitle)

            val viewModel = ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory()
            )[DetailViewModel::class.java]
            val showId = mDetailFragmentArgs.showId
            if (showId != null)
                viewModel.setSelectedShow(showId)

            val showType = mDetailFragmentArgs.showType
            if (showType != null)
                viewModel.setSelectedShowType(showType)

            val show = viewModel.getShow()
            populateView(show)
        }
    }

    private fun populateView(show: ShowEntity) {
        with(binding) {
            tvTitle.text = show.title
            tvDate.text = show.releaseDate
            tvDirector.text = show.director
            tvGenre.text = show.genre
            tvQuote.text = show.quote
            tvScore.text = show.score
            tvSynopsis.text = show.synopsis

            Glide.with(this@DetailFragment)
                .load(show.imagePath)
                .apply(
                    RequestOptions
                        .placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                        .override(250, 500)
                )
                .into(imgPoster)
            imgPoster.clipToOutline = true

            Glide.with(this@DetailFragment)
                .load(show.imagePath)
                .apply(
                    RequestOptions
                        .placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                        .override(500, 750)
                )
                .into(imgBanner)
            imgBanner.clipToOutline = true

            iconShare.setOnClickListener { setOnClickShare(show.title, show.showId) }
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
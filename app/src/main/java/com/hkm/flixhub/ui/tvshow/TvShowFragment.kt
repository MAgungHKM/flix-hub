package com.hkm.flixhub.ui.tvshow

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hkm.flixhub.adapter.TvShowAdapter
import com.hkm.flixhub.databinding.FragmentTvShowBinding
import com.hkm.flixhub.entity.ShowEntity
import com.hkm.flixhub.ui.detail.DetailFragment
import com.hkm.flixhub.ui.home.HomeFragmentDirections
import com.hkm.flixhub.utils.OnMyFragmentListener

class TvShowFragment : Fragment() {
    private lateinit var tvShowAdapter: TvShowAdapter
    private lateinit var mOnMyFragmentListener: OnMyFragmentListener
    private var _binding: FragmentTvShowBinding? = null
    private val binding get() = _binding as FragmentTvShowBinding

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
        _binding = FragmentTvShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null) {
            val viewModel = ViewModelProvider(
                this,
                ViewModelProvider.NewInstanceFactory()
            )[TvShowViewModel::class.java]
            val tvShows = viewModel.getTvShows()
            tvShowAdapter = TvShowAdapter()
            tvShowAdapter.setTvShows(tvShows)
            setItemOnClickListener()
            with(binding.rvTvShow) {
                val numOfColumn = if (getScreenWidth() < 1500) 2 else 4
                layoutManager = GridLayoutManager(context, numOfColumn)
                setHasFixedSize(true)
                adapter = tvShowAdapter
            }
        }
    }

    private fun setItemOnClickListener() {
        tvShowAdapter.setOnClickListener(object : TvShowAdapter.OnClickListener {
            override fun onClick(show: ShowEntity) {
                val toDetailFragment = HomeFragmentDirections.actionHomeFragmentToDetailFragment(
                    show.showId,
                    show.title,
                    DetailFragment.TYPE_TV_SHOW
                )
                view?.findNavController()?.navigate(toDetailFragment)
            }
        })
    }

    private fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
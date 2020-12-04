package com.hkm.flixhub.ui.tvshow

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hkm.flixhub.adapter.TvShowAdapter
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.databinding.FragmentTvShowBinding
import com.hkm.flixhub.ui.home.HomeFragmentDirections
import com.hkm.flixhub.utils.ShowType
import com.hkm.flixhub.vo.Status
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvShowFragment : Fragment() {
    private lateinit var tvShowAdapter: TvShowAdapter
    private var _binding: FragmentTvShowBinding? = null
    private val binding get() = _binding as FragmentTvShowBinding

    // Lazy Inject ViewModel
    private val viewModel: TvShowViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTvShowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null) {
            tvShowAdapter = TvShowAdapter()
            binding.progressBarTvShow.visibility = View.VISIBLE

            viewModel.getPages().observe(viewLifecycleOwner, { page ->
                viewModel.getTvShows(page).observe(viewLifecycleOwner, { tvShows ->
                    if (tvShows != null) {
                        when (tvShows.status) {
                            Status.LOADING -> binding.progressBarTvShow.visibility = View.VISIBLE
                            Status.SUCCESS -> {
                                with(tvShows.data?.get(0)) {
                                    if (this?.errorMessage != "null")
                                        Toast.makeText(requireActivity(),
                                            this?.errorMessage,
                                            Toast.LENGTH_LONG)
                                            .show()
                                }

                                binding.progressBarTvShow.visibility = View.GONE
                                tvShowAdapter.submitList(tvShows.data)
                                setItemOnClickListener()
                                tvShowAdapter.notifyDataSetChanged()
                            }
                            Status.ERROR -> {
                                binding.progressBarTvShow.visibility = View.GONE
                                Toast.makeText(context,
                                    tvShows.data?.get(0)?.errorMessage,
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
            })

            with(binding.rvTvShow) {
                val numOfColumn = if (getScreenWidth() < 1500) 2 else 4
                layoutManager = GridLayoutManager(context, numOfColumn)
                setHasFixedSize(true)

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        val layoutManager =
                            LinearLayoutManager::class.java.cast(recyclerView.layoutManager) as LinearLayoutManager
                        val totalItemCount = layoutManager.itemCount
                        val lastVisible = layoutManager.findLastVisibleItemPosition()

                        val endHasBeenReached = lastVisible + 1 >= totalItemCount
                        if (totalItemCount > 0 && endHasBeenReached) {
                            viewModel.nextPage()
                        }
                    }
                })

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
                    ShowType.TYPE_TV_SHOW
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
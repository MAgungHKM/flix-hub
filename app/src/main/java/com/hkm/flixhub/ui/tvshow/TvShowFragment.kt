package com.hkm.flixhub.ui.tvshow

import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.Transformations
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hkm.flixhub.R
import com.hkm.flixhub.adapter.TvShowAdapter
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.databinding.FragmentTvShowBinding
import com.hkm.flixhub.ui.home.HomeFragmentDirections
import com.hkm.flixhub.utils.ShowType
import com.hkm.flixhub.utils.SortUtils
import com.hkm.flixhub.vo.Status
import org.koin.androidx.viewmodel.ext.android.viewModel

class TvShowFragment : Fragment() {
    private lateinit var tvShowAdapter: TvShowAdapter
    private var _binding: FragmentTvShowBinding? = null
    private val binding get() = _binding as FragmentTvShowBinding

    // Lazy Inject ViewModel
    private val viewModel: TvShowViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

            Transformations.switchMap(viewModel.getSortBy()) { sortBy ->
                Transformations.switchMap(viewModel.getPages()) { selectedPage ->
                    viewModel.getTvShows(sortBy, selectedPage)
                }
            }.observe(viewLifecycleOwner, { tvShows ->
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
                        if (totalItemCount > 0 && endHasBeenReached && binding.progressBarTvShow.isGone) {
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.sort_menu, menu)
        viewModel.getSortBy().observe(viewLifecycleOwner, { sort ->
            when (sort) {
                SortUtils.POPULARITY -> menu.findItem(R.id.action_popularity).isChecked = true
                SortUtils.ORIGINAL_TITLE -> menu.findItem(R.id.action_original_title).isChecked =
                    true
                SortUtils.SCORE -> menu.findItem(R.id.action_score).isChecked = true
                SortUtils.VOTE_COUNT -> menu.findItem(R.id.action_vote_count).isChecked = true
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var sortBy = ""
        when (item.itemId) {
            R.id.action_popularity -> sortBy = SortUtils.POPULARITY
            R.id.action_original_title -> sortBy = SortUtils.ORIGINAL_TITLE
            R.id.action_score -> sortBy = SortUtils.SCORE
            R.id.action_vote_count -> sortBy = SortUtils.VOTE_COUNT
        }

        viewModel.refreshTvShows()
        viewModel.setSortBy(sortBy)
        tvShowAdapter.submitList(null)
        item.isChecked = true

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
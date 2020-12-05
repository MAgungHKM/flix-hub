package com.hkm.flixhub.ui.movie

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
import com.hkm.flixhub.adapter.MovieAdapter
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.databinding.FragmentMovieBinding
import com.hkm.flixhub.ui.home.HomeFragmentDirections
import com.hkm.flixhub.utils.ShowType
import com.hkm.flixhub.utils.SortUtils
import com.hkm.flixhub.vo.Status
import org.koin.androidx.viewmodel.ext.android.viewModel


class MovieFragment : Fragment() {
    private lateinit var movieAdapter: MovieAdapter
    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding as FragmentMovieBinding

    // Lazy Inject ViewModel
    private val viewModel: MovieViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null) {
            movieAdapter = MovieAdapter()

            Transformations.switchMap(viewModel.getSortBy()) { sortBy ->
                Transformations.switchMap(viewModel.getPages()) { selectedPage ->
                    viewModel.getMovies(sortBy, selectedPage)
                }
            }.observe(viewLifecycleOwner, { movies ->
                if (movies != null) {
                    when (movies.status) {
                        Status.LOADING -> binding.progressBarMovie.visibility = View.VISIBLE
                        Status.SUCCESS -> {
                            if (!movies.data.isNullOrEmpty()) {
                                with(movies.data[0]) {
                                    if (this?.errorMessage != "null")
                                        Toast.makeText(requireActivity(),
                                            this?.errorMessage,
                                            Toast.LENGTH_LONG)
                                            .show()
                                }

                                binding.progressBarMovie.visibility = View.GONE
                                movieAdapter.submitList(movies.data)
                                setItemOnClickListener()
                                movieAdapter.notifyDataSetChanged()
                            }
                        }
                        Status.ERROR -> {
                            binding.progressBarMovie.visibility = View.GONE
                            Toast.makeText(context,
                                movies.data?.get(0)?.errorMessage,
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })

            with(binding.rvMovie) {
                val numOfColumn = if (getScreenWidth() < 1500) 2 else 4
                layoutManager = GridLayoutManager(context, numOfColumn)
                setHasFixedSize(true)

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        val layoutManager =
                            LinearLayoutManager::class.java.cast(recyclerView.layoutManager) as LinearLayoutManager
                        val totalItemCount = layoutManager.itemCount
                        val lastVisible = layoutManager.findLastVisibleItemPosition()

                        val endHasBeenReached = lastVisible + 3 >= totalItemCount
                        if (totalItemCount > 0 && endHasBeenReached && binding.progressBarMovie.isGone) {
                            viewModel.nextPage()
                        }
                    }
                })

                adapter = movieAdapter
            }
        }
    }

    private fun setItemOnClickListener() {
        movieAdapter.setOnClickListener(object : MovieAdapter.OnClickListener {
            override fun onClick(show: ShowEntity) {
                val toDetailFragment = HomeFragmentDirections.actionHomeFragmentToDetailFragment(
                    show.showId,
                    show.title,
                    ShowType.TYPE_MOVIE
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

        viewModel.refreshMovies()
        viewModel.setSortBy(sortBy)
        movieAdapter.submitList(null)
        item.isChecked = true

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
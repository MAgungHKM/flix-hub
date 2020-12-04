package com.hkm.flixhub.ui.movie

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
import com.hkm.flixhub.adapter.MovieAdapter
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.databinding.FragmentMovieBinding
import com.hkm.flixhub.ui.home.HomeFragmentDirections
import com.hkm.flixhub.utils.ShowType
import com.hkm.flixhub.vo.Status
import org.koin.androidx.viewmodel.ext.android.viewModel


class MovieFragment : Fragment() {
    private lateinit var movieAdapter: MovieAdapter
    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding as FragmentMovieBinding

    // Lazy Inject ViewModel
    private val viewModel: MovieViewModel by viewModel()

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

            viewModel.getPages().observe(viewLifecycleOwner, { page ->
                viewModel.getMovies(page).observe(viewLifecycleOwner, { movies ->
                    if (movies != null) {
                        when (movies.status) {
                            Status.LOADING -> binding.progressBarMovie.visibility = View.VISIBLE
                            Status.SUCCESS -> {
                                with(movies.data?.get(0)) {
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
                            Status.ERROR -> {
                                binding.progressBarMovie.visibility = View.GONE
                                Toast.makeText(context,
                                    movies.data?.get(0)?.errorMessage,
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
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

                        val endHasBeenReached = lastVisible + 1 >= totalItemCount
                        if (totalItemCount > 0 && endHasBeenReached) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
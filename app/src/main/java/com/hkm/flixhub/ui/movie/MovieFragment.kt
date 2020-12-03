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
import com.hkm.flixhub.adapter.MovieAdapter
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.databinding.FragmentMovieBinding
import com.hkm.flixhub.ui.detail.DetailFragment
import com.hkm.flixhub.ui.home.HomeFragmentDirections
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
            binding.progressBarMovie.visibility = View.VISIBLE

            viewModel.getMovies().observe(viewLifecycleOwner, { movies ->
                if (movies.isNotEmpty()) {
                    with(movies[0]) {
                        if (errorMessage != "null")
                            Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_LONG)
                                .show()
                    }

                    binding.progressBarMovie.visibility = View.GONE
                    movieAdapter.setMovies(movies)
                    setItemOnClickListener()
                    movieAdapter.notifyDataSetChanged()
                }
            })

            with(binding.rvMovie) {
                val numOfColumn = if (getScreenWidth() < 1500) 2 else 4
                layoutManager = GridLayoutManager(context, numOfColumn)
                setHasFixedSize(true)
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
                    DetailFragment.TYPE_MOVIE
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
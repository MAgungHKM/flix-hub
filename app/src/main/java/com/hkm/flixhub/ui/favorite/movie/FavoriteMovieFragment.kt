package com.hkm.flixhub.ui.favorite.movie

import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hkm.flixhub.R
import com.hkm.flixhub.adapter.MovieAdapter
import com.hkm.flixhub.data.source.local.entity.ShowEntity
import com.hkm.flixhub.databinding.FragmentFavoriteMovieBinding
import com.hkm.flixhub.ui.favorite.FavoriteFragmentDirections
import com.hkm.flixhub.utils.ShowType
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteMovieFragment : Fragment() {
    private lateinit var movieAdapter: MovieAdapter
    private var _binding: FragmentFavoriteMovieBinding? = null
    private val binding get() = _binding as FragmentFavoriteMovieBinding

    // Lazy Inject ViewModel
    private val viewModel: FavoriteMovieViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity != null) {
            movieAdapter = MovieAdapter()

            binding.progressBarFavoriteMovie.visibility = View.VISIBLE
            viewModel.getPages().observe(viewLifecycleOwner, { page ->
                viewModel.getMovies(page).observe(viewLifecycleOwner, { movies ->
                    if (!movies.isNullOrEmpty()) {
                        with(binding) {
                            with(movies[0]) {
                                if (this?.errorMessage != "null")
                                    Toast.makeText(requireActivity(),
                                        this?.errorMessage,
                                        Toast.LENGTH_LONG)
                                        .show()
                            }

                            this.progressBarFavoriteMovie.visibility = View.GONE
                            movieAdapter.submitList(movies)
                            setItemOnClickListener()
                            movieAdapter.notifyDataSetChanged()
                        }
                    } else {
                        with(binding) {
                            this.progressBarFavoriteMovie.visibility = View.GONE
                            this.tvFavoriteMovieNotFound.visibility = View.VISIBLE
                        }
                    }
                })
            })

            with(binding.rvFavoriteMovie) {
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
                val toDetailFragment =
                    FavoriteFragmentDirections.actionFavoriteFragmentToDetailFragment(
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
        inflater.inflate(R.menu.favorite_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                (activity as AppCompatActivity).onBackPressed()
                true
            }
            R.id.menu_home -> {
                val toHomeFragment =
                    FavoriteFragmentDirections.actionFavoriteFragmentToHomeFragment()
                view?.findNavController()?.navigate(toHomeFragment)
                true
            }
            R.id.menu_delete_all -> {
                showDeleteDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteDialog() {
        val mAlertDialog: AlertDialog
        val mBuilder: AlertDialog.Builder =
            AlertDialog.Builder(requireActivity(), R.style.MyPopupMenu)

        mBuilder.setTitle(getString(R.string.dialog_delete_title))
        mBuilder.setMessage(getString(R.string.dialog_delete_message, "movie"))

        mBuilder.setPositiveButton(getString(R.string.dialog_confirm_yes)) { _, _ ->
            viewModel.removeAllFavorite()
            movieAdapter.submitList(null)
        }

        mBuilder.setNegativeButton(getString(R.string.dialog_confirm_no)) { dialog, _ ->
            dialog.cancel()
        }

        mAlertDialog = mBuilder.create()
        mAlertDialog.setCanceledOnTouchOutside(true)
        mAlertDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
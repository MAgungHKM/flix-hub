package com.hkm.flixhub.ui.favorite

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.hkm.flixhub.R
import com.hkm.flixhub.adapter.SectionsPagerAdapter
import com.hkm.flixhub.databinding.FragmentFavoriteBinding
import com.hkm.flixhub.ui.favorite.movie.FavoriteMovieViewModel
import com.hkm.flixhub.ui.favorite.tvshow.FavoriteTvShowViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding as FragmentFavoriteBinding

    // Lazy Inject ViewModel
    private val movieViewModel: FavoriteMovieViewModel by viewModel()
    private val tvShowViewModel: FavoriteTvShowViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sectionsPagerAdapter = SectionsPagerAdapter(FavoriteFragment::class.java.simpleName,
            requireActivity(),
            childFragmentManager)
        binding.viewPagerFavorite.adapter = sectionsPagerAdapter
        binding.viewPagerFavorite.offscreenPageLimit = 2
        binding.tabsFavorite.setupWithViewPager(binding.viewPagerFavorite)
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.menu_favorite)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
                val mAlertDialog: AlertDialog
                val mBuilder: AlertDialog.Builder =
                    AlertDialog.Builder(requireActivity(), R.style.MyPopupMenu)

                mBuilder.setTitle(getString(R.string.dialog_delete_title))
                mBuilder.setMessage(getString(R.string.dialog_delete_message))

                mBuilder.setPositiveButton(getString(R.string.dialog_confirm_yes)) { _, _ ->
                    movieViewModel.removeAllFavorite()
                    tvShowViewModel.removeAllFavorite()
                }

                mBuilder.setNegativeButton(getString(R.string.dialog_confirm_no)) { dialog, _ ->
                    dialog.cancel()
                }

                mAlertDialog = mBuilder.create()
                mAlertDialog.setCanceledOnTouchOutside(true)
                mAlertDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
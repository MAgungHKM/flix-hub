package com.hkm.flixhub.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hkm.flixhub.R
import com.hkm.flixhub.adapter.SectionsPagerAdapter
import com.hkm.flixhub.databinding.FragmentHomeBinding
import com.hkm.flixhub.utils.OnMyFragmentListener

class HomeFragment : Fragment() {
    private lateinit var mOnMyFragmentListener: OnMyFragmentListener
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding as FragmentHomeBinding

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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sectionsPagerAdapter = SectionsPagerAdapter(context as Context, childFragmentManager)
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.tabs.setupWithViewPager(binding.viewPager)
    }

    override fun onStart() {
        super.onStart()
        mOnMyFragmentListener.onChangeToolbarTitle(getString(R.string.app_name))
        mOnMyFragmentListener.onChangeToolbarDisplayHome(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
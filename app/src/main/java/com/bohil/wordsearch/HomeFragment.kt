package com.bohil.wordsearch

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import com.bohil.wordsearch.databinding.FragmentHomeBinding

/**
 * Home Screen Fragment
 * Handles the navigation from app launch to the help and game fragments
 */
class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Data binding variable
        val binding: FragmentHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.buttonStart.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(HomeFragmentDirections.actionHomeFragmentToGameFragment())
        }

        binding.helpText.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(HomeFragmentDirections.actionHomeFragmentToHelpFragment())
        }

        binding.buttonQuit.setOnClickListener {
            System.exit(0)
        }

        binding.lifecycleOwner = this
        return binding.root

    }


}

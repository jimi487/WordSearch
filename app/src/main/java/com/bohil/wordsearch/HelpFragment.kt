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
import com.bohil.wordsearch.databinding.FragmentHelpBinding


class HelpFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Data binding variable
        val binding: FragmentHelpBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_help, container, false)

        binding.backButton.setOnClickListener {
            NavHostFragment.findNavController(this)
                .navigate(HelpFragmentDirections.actionHelpFragmentToHomeFragment())
        }


        binding.lifecycleOwner = this
        return binding.root
    }


}

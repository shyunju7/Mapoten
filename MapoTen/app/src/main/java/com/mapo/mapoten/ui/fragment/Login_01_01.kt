package com.mapo.mapoten.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapo.mapoten.R
import com.mapo.mapoten.databinding.FragmentLogin0101Binding

class Login_01_01 : Fragment() {
    private var _binding: FragmentLogin0101Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogin0101Binding.inflate(inflater,container,false)

        return binding.root
    }
}
package com.example.monkhoodassignment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.monkhoodassignment.databinding.FragmentFirebaseBinding
import com.example.monkhoodassignment.databinding.FragmentSharedPreferenceBinding

class SharedPreferenceFragment : Fragment() {

    private var _binding: FragmentSharedPreferenceBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SharedPreferenceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSharedPreferenceBinding.inflate(inflater,container,false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}
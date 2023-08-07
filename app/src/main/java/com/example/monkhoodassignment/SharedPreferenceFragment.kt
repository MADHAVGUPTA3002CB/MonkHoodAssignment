package com.example.monkhoodassignment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monkhoodassignment.adapters.adapter
import com.example.monkhoodassignment.databinding.FragmentSharedPreferenceBinding
import com.example.monkhoodassignment.mvvm.ViewModel

class SharedPreferenceFragment : Fragment() {

    private lateinit var sharedPreferences : SharedPreferences
    private var _binding: FragmentSharedPreferenceBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: SharedPreferenceViewModel

    private lateinit var vm : ViewModel
    private lateinit var adapter : adapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSharedPreferenceBinding.inflate(inflater,container,false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(this)[ViewModel::class.java]

        adapter = adapter()
        binding.sharedRecycle.adapter = adapter
        binding.sharedRecycle.layoutManager = LinearLayoutManager(requireContext())

        vm.usersFromSp().observe(viewLifecycleOwner, Observer {
            adapter.setUserList(it)
        })



    }
    override fun onResume() {
        super.onResume()
        vm.usersFromSp().observe(this , Observer {
            adapter.setUserList(it)
        })
    }
}
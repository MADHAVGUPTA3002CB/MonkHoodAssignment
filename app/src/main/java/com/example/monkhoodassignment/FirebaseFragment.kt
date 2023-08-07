package com.example.monkhoodassignment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monkhoodassignment.adapters.adapter
import com.example.monkhoodassignment.databinding.FragmentFirebaseBinding
import com.example.monkhoodassignment.mvvm.ViewModel

class FirebaseFragment : Fragment() {


    private var _binding: FragmentFirebaseBinding? = null
    private val binding get() = _binding!!

    private lateinit var vm : ViewModel
    private lateinit var adapter : adapter





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirebaseBinding.inflate(inflater,container,false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        vm = ViewModelProvider(this).get(ViewModel::class.java)
        adapter = adapter()
        binding.firerecycle.adapter= adapter
        binding.firerecycle.layoutManager =LinearLayoutManager(requireContext())


        vm.usersFromFB().observe(viewLifecycleOwner, Observer {
            adapter.setUserList(it)
        })

    }



}
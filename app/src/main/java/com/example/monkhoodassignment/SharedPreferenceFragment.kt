package com.example.monkhoodassignment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class SharedPreferenceFragment : Fragment() {

    companion object {
        fun newInstance() = SharedPreferenceFragment()
    }

    private lateinit var viewModel: SharedPreferenceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_shared_preference, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(SharedPreferenceViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
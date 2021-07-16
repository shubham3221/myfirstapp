package com.example.myfirstapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.myfirstapp.Myconstants.Companion.TAG
import com.example.myfirstapp.databinding.FragmentBlankBinding
import kotlinx.android.synthetic.main.activity_google.*
import kotlinx.android.synthetic.main.fragment_blank.*
import kotlinx.android.synthetic.main.fragment_blank.view.*


class BlankFragment : Fragment(R.layout.fragment_blank) {
    var _binding:FragmentBlankBinding? = null
    val binding get() = _binding!!
    val registerForActivityResult1 = registerForActivityResult(SimpleContract()) {
        binding.textview.text = it!!
    }
    lateinit var registerForActivityResult:ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerForActivityResult =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                textview.text = it.toString()
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBlankBinding.inflate(inflater,container,false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textview.text = "hi"

        binding.textview.setOnClickListener {
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
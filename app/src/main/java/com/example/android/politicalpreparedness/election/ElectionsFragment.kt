package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment : Fragment() {

    // Declare ViewModel
    private lateinit var viewModel: ElectionsViewModel
    private lateinit var binding: FragmentElectionBinding
    private lateinit var electionAdapter: ElectionListAdapter
    private lateinit var followedAdapter: ElectionListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        // Add ViewModel values and create ViewModel
        viewModel = ViewModelProvider(
            this,
            ElectionsViewModelFactory(requireActivity().application)).get(ElectionsViewModel::class.java
        )

        // Add binding values
        binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this

        setupAdapters()
        setupObservers()

        return binding.root
    }

    private fun setupObservers() {
        // Link elections to voter info
        viewModel.navigateToDetailElection.observe(viewLifecycleOwner) { election ->
            election?.let {
                findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(
                    it))
                viewModel.onElectionNavigated()
            }
        }

        // Refresh adapters when fragment loads
        viewModel.electionUpcoming.observe(viewLifecycleOwner
        ) { electionList ->
            electionList.apply {
                electionAdapter.submitList(electionList)
                if (electionList.isEmpty()) {
                    binding.notDataUp.visibility = View.VISIBLE
                } else {
                    binding.notDataUp.visibility = View.GONE
                }
            }
        }

        viewModel.electionFolllowed.observe(viewLifecycleOwner) { electionList ->
            electionList.apply {
                followedAdapter.submitList(electionList)
                if (electionList.isEmpty()) {
                    binding.emptyData.visibility = View.VISIBLE
                } else {
                    binding.emptyData.visibility = View.GONE
                }
            }
        }
    }

    private fun setupAdapters() {
        electionAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.onElectionClicked(election)
        })
        electionAdapter.setHasStableIds(true)
        binding.electionRecycler.adapter = electionAdapter

        followedAdapter = ElectionListAdapter(ElectionListener { election ->
            viewModel.onElectionClicked(election)
        })
        followedAdapter.setHasStableIds(true)
        binding.saveRecycler.adapter = followedAdapter
    }
}
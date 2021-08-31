package com.mapo.mapoten.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.mapo.mapoten.R
import com.mapo.mapoten.databinding.FragmentBusinessAccount0104Binding
import com.mapo.mapoten.ui.adpater.EmploymentPostingAdapter
import com.mapo.mapoten.ui.data.EmploymentPostingContents


class BusinessAccount_01_04 : Fragment() {

    lateinit var binding : FragmentBusinessAccount0104Binding
    private lateinit var adapter : EmploymentPostingAdapter
    private val testList = mutableListOf<EmploymentPostingContents>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentBusinessAccount0104Binding.inflate(inflater, container, false)
        val view = binding.root

        adapter  = EmploymentPostingAdapter(this.requireContext())
        binding.employmentPostingBoard.adapter = adapter

        // testData
        testList.apply {
            add( EmploymentPostingContents("채용공고제목", "일반일자리","승인", "등록일: 2021년 08월 31일 17시 34분"))
            add( EmploymentPostingContents("채용공고제목", "일반일자리","비승인", "등록일: 2021년 08월 31일 17시 34분"))
            add( EmploymentPostingContents("채용공고제목", "일반일자리","승인심사중", "등록일: 2021년 08월 31일 17시 34분"))
            add( EmploymentPostingContents("채용공고제목", "일반일자리","승인", "등록일: 2021년 08월 31일 17시 34분"))
            add( EmploymentPostingContents("채용공고제목", "일반일자리","승인심사중", "등록일: 2021년 08월 31일 17시 34분"))
            add( EmploymentPostingContents("채용공고제목", "일반일자리","비승인", "등록일: 2021년 08월 31일 17시 34분"))
            add( EmploymentPostingContents("채용공고제목", "일반일자리","승인심사중", "등록일: 2021년 08월 31일 17시 34분"))
            add( EmploymentPostingContents("채용공고제목", "일반일자리","승인", "등록일: 2021년 08월 31일 17시 34분"))
            add( EmploymentPostingContents("채용공고제목", "일반일자리","승인심사중", "등록일: 2021년 08월 31일 17시 34분"))
            add( EmploymentPostingContents("채용공고제목", "일반일자리","비승인", "등록일: 2021년 08월 31일 17시 34분"))
            adapter.data = testList
            adapter.notifyDataSetChanged()
        }

        // selectbox
        val spinnerItem = resources.getStringArray(R.array.states)
        val spinnerAdapter  = ArrayAdapter(this.requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerItem)
        binding.filter.adapter = spinnerAdapter
        binding.filter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0 -> {
                        Toast.makeText(context, "전체", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        Toast.makeText(context, "승인심사중", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        Toast.makeText(context, "승인", Toast.LENGTH_SHORT).show()
                    }
                    3 -> {
                        Toast.makeText(context, "비승인", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show()}
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        return view
    }


}
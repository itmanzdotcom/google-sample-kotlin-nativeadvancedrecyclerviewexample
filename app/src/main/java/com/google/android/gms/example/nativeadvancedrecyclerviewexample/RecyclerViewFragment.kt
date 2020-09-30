/*
 * Copyright (C) 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.example.nativeadvancedrecyclerviewexample

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class RecyclerViewFragment : Fragment() {
  // List of Native ads and MenuItems that populate the RecyclerView.
  private var mRecyclerViewItems: List<Any>? = null
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // retain this fragment
    retainInstance = true
    val activity = activity as MainActivity
    mRecyclerViewItems = activity.recyclerViewItems
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?): View? {

    // Inflate the layout for this fragment
    val rootView = inflater.inflate(R.layout.fragment_recycler_view, container, false)
    val mRecyclerView = rootView.findViewById<View>(R.id.recycler_view) as RecyclerView

    // Use this setting to improve performance if you know that changes
    // in content do not change the layout size of the RecyclerView.
    mRecyclerView.setHasFixedSize(true)

    // Specify a linear layout manager.
    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(
      context)
    mRecyclerView.layoutManager = layoutManager

    // Specify an adapter.
    val adapter: RecyclerView.Adapter<*> = ListAdapter(requireContext(), mRecyclerViewItems!!)
    mRecyclerView.adapter = adapter
    return rootView
  }
}
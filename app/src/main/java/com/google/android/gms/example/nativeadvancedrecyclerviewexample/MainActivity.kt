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

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.formats.UnifiedNativeAd
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

/**
 * A simple activity showing the use of [NativeAd] ads in
 * a [RecyclerView] widget.
 */
class MainActivity : AppCompatActivity() {
  // The AdLoader used to load ads.
  private var adLoader: AdLoader? = null

  // List of MenuItems and native ads that populate the RecyclerView.
  private val dataList: MutableList<Any> = ArrayList()

  // List of native ads that have been successfully loaded.
  private val nativeAds: MutableList<UnifiedNativeAd> = ArrayList()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Initialize the Mobile Ads SDK.
    MobileAds.initialize(this) {}
    if (savedInstanceState == null) {
      // Create new fragment to display a progress spinner while the data set for the
      // RecyclerView is populated.
      val loadingScreenFragment: Fragment = LoadingScreenFragment()
      val transaction = supportFragmentManager.beginTransaction()
      transaction.add(R.id.fragment_container, loadingScreenFragment)

      // Commit the transaction.
      transaction.commit()

      // Update the RecyclerView item's list with menu items.
      addMenuItemsFromJson()
      // Update the RecyclerView item's list with native ads.
      loadNativeAds()
    }
  }

  val recyclerViewItems: List<Any>
    get() = dataList

  private fun insertAdsInMenuItems() {
    if (nativeAds.size <= 0) {
      return
    }
    val offset = dataList.size / nativeAds.size + 1
    var index = 0
    for (ad in nativeAds) {
      dataList.add(index, ad)
      index += offset
    }

    showScreen()
  }

  @SuppressLint("MissingPermission")
  private fun loadNativeAds() {
    val builder = AdLoader.Builder(this, getString(R.string.ad_unit_id))
    adLoader = builder.forUnifiedNativeAd { unifiedNativeAd -> // A native ad loaded successfully, check if the ad loader has finished loading
      // and if so, insert the ads into the list.
      nativeAds.add(unifiedNativeAd)
      if (!adLoader!!.isLoading) {
        insertAdsInMenuItems()
      }
    }.withAdListener(
      object : AdListener() {
        override fun onAdFailedToLoad(errorCode: Int) {
          // A native ad failed to load, check if the ad loader has finished loading
          // and if so, insert the ads into the list.
          Log.e("MainActivity", "The previous native ad failed to load. Attempting to"
            + " load another.")
          if (!adLoader!!.isLoading) {
            insertAdsInMenuItems()
          }
        }
      }).build()

    // Load the Native ads.
    adLoader?.loadAds(AdRequest.Builder().build(), 20)
  }

  private fun showScreen() {
    // Create new fragment and transaction
    val newFragment: Fragment = RecyclerViewFragment()
    val transaction = supportFragmentManager.beginTransaction()

    // Replace whatever is in the fragment_container view with this fragment,
    // and add the transaction to the back stack
    transaction.replace(R.id.fragment_container, newFragment)
    transaction.addToBackStack(null)

    // Commit the transaction
    transaction.commit()
  }

  /**
   * Adds [MenuItem]'s from a JSON file.
   */
  private fun addMenuItemsFromJson() {
    try {
      val jsonDataString = readJsonDataFromFile()
      val menuItemsJsonArray = JSONArray(jsonDataString)
      for (i in 0 until menuItemsJsonArray.length()) {
        val menuItemObject = menuItemsJsonArray.getJSONObject(i)
        val menuItemName = menuItemObject.getString("name")
        val menuItemDescription = menuItemObject.getString("description")
        val menuItemPrice = menuItemObject.getString("price")
        val menuItemCategory = menuItemObject.getString("category")
        val menuItemImageName = menuItemObject.getString("photo")
        val menuItem = MenuItem(menuItemName, menuItemDescription, menuItemPrice,
          menuItemCategory, menuItemImageName)
        dataList.add(menuItem)
      }
    } catch (exception: IOException) {
      Log.e(MainActivity::class.java.name, "Unable to parse JSON file.", exception)
    } catch (exception: JSONException) {
      Log.e(MainActivity::class.java.name, "Unable to parse JSON file.", exception)
    }
  }

  /**
   * Reads the JSON file and converts the JSON data to a [String].
   *
   * @return A [String] representation of the JSON data.
   * @throws IOException if unable to read the JSON file.
   */
  @Throws(IOException::class)
  private fun readJsonDataFromFile(): String {
    var inputStream: InputStream? = null
    val builder = StringBuilder()
    try {
      var jsonDataString: String? = null
      inputStream = resources.openRawResource(R.raw.menu_items_json)
      val bufferedReader = BufferedReader(
        InputStreamReader(inputStream, "UTF-8"))
      while (bufferedReader.readLine().also { jsonDataString = it } != null) {
        builder.append(jsonDataString)
      }
    } finally {
      inputStream?.close()
    }
    return String(builder)
  }

  companion object {
    // The number of native ads to load.
    const val NUMBER_OF_ADS = 5
  }
}
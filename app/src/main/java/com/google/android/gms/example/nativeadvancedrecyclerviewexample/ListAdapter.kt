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

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.google.android.gms.example.nativeadvancedrecyclerviewexample.ListAdapter.MenuItemViewHolder

/**
 * The [ListAdapter] class.
 *
 * The adapter provides access to the items in the [MenuItemViewHolder]
 */
internal class ListAdapter
/**
 * For this example app, the recyclerViewItems list contains only
 * [MenuItem] and [UnifiedNativeAd] types.
 */(  // An Activity's Context.
  private val mContext: Context,   // The list of Native ads and menu items.
  private val mRecyclerViewItems: List<Any>) : RecyclerView.Adapter<ViewHolder>() {
  /**
   * The [MenuItemViewHolder] class.
   * Provides a reference to each view in the menu item view.
   */
  inner class MenuItemViewHolder internal constructor(view: View) : ViewHolder(view) {
    var menuItemName: TextView = view.findViewById<View>(R.id.menu_item_name) as TextView
    var menuItemDescription: TextView = view.findViewById<View>(R.id.menu_item_description) as TextView
    var menuItemPrice: TextView = view.findViewById<View>(R.id.menu_item_price) as TextView
    var menuItemCategory: TextView = view.findViewById<View>(R.id.menu_item_category) as TextView
    var menuItemImage: ImageView = view.findViewById<View>(R.id.menu_item_image) as ImageView
  }

  override fun getItemCount(): Int {
    return mRecyclerViewItems.size
  }

  /**
   * Determines the view type for the given position.
   */
  override fun getItemViewType(position: Int): Int {
    val recyclerViewItem = mRecyclerViewItems[position]
    return if (recyclerViewItem is UnifiedNativeAd) {
      UNIFIED_NATIVE_AD_VIEW_TYPE
    } else MENU_ITEM_VIEW_TYPE
  }

  /**
   * Creates a new view for a menu item view or a Native ad view
   * based on the viewType. This method is invoked by the layout manager.
   */
  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
    return when (viewType) {
      UNIFIED_NATIVE_AD_VIEW_TYPE -> {
        val unifiedNativeLayoutView = LayoutInflater.from(
          viewGroup.context).inflate(R.layout.ad_unified,
          viewGroup, false)
        UnifiedNativeAdViewHolder(unifiedNativeLayoutView)
      }
      MENU_ITEM_VIEW_TYPE -> {
        val menuItemLayoutView = LayoutInflater.from(viewGroup.context).inflate(
          R.layout.menu_item_container, viewGroup, false)
        MenuItemViewHolder(menuItemLayoutView)
      }
      else -> {
        val menuItemLayoutView = LayoutInflater.from(viewGroup.context).inflate(
          R.layout.menu_item_container, viewGroup, false)
        MenuItemViewHolder(menuItemLayoutView)
      }
    }
  }

  /**
   * Replaces the content in the views that make up the menu item view and the
   * Native ad view. This method is invoked by the layout manager.
   */
  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    when (getItemViewType(position)) {
      UNIFIED_NATIVE_AD_VIEW_TYPE -> {
        val nativeAd = mRecyclerViewItems[position] as UnifiedNativeAd
        populateNativeAdView(nativeAd, (holder as UnifiedNativeAdViewHolder).adView)
      }
      MENU_ITEM_VIEW_TYPE -> {
        val menuItemHolder = holder as MenuItemViewHolder
        val menuItem = mRecyclerViewItems[position] as MenuItem

        // Get the menu item image resource ID.
        val imageName = menuItem.imageName
        val imageResID = mContext.resources.getIdentifier(imageName, "drawable",
          mContext.packageName)

        // Add the menu item details to the menu item view.
        menuItemHolder.menuItemImage.setImageResource(imageResID)
        menuItemHolder.menuItemName.text = menuItem.name
        menuItemHolder.menuItemPrice.text = menuItem.price
        menuItemHolder.menuItemCategory.text = menuItem.category
        menuItemHolder.menuItemDescription.text = menuItem.description
      }
      else -> {
        val menuItemHolder = holder as MenuItemViewHolder
        val menuItem = mRecyclerViewItems[position] as MenuItem
        val imageName = menuItem.imageName
        val imageResID = mContext.resources.getIdentifier(imageName, "drawable",
          mContext.packageName)
        menuItemHolder.menuItemImage.setImageResource(imageResID)
        menuItemHolder.menuItemName.text = menuItem.name
        menuItemHolder.menuItemPrice.text = menuItem.price
        menuItemHolder.menuItemCategory.text = menuItem.category
        menuItemHolder.menuItemDescription.text = menuItem.description
      }
    }
  }

  private fun populateNativeAdView(nativeAd: UnifiedNativeAd,
    adView: UnifiedNativeAdView) {
    // Some assets are guaranteed to be in every UnifiedNativeAd.
    (adView.headlineView as TextView).text = nativeAd.headline
    (adView.bodyView as TextView).text = nativeAd.body
    (adView.callToActionView as Button).text = nativeAd.callToAction

    // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
    // check before trying to display them.
    val icon = nativeAd.icon
    if (icon == null) {
      adView.iconView.visibility = View.INVISIBLE
    } else {
      (adView.iconView as ImageView).setImageDrawable(icon.drawable)
      adView.iconView.visibility = View.VISIBLE
    }
    if (nativeAd.price == null) {
      adView.priceView.visibility = View.INVISIBLE
    } else {
      adView.priceView.visibility = View.VISIBLE
      (adView.priceView as TextView).text = nativeAd.price
    }
    if (nativeAd.store == null) {
      adView.storeView.visibility = View.INVISIBLE
    } else {
      adView.storeView.visibility = View.VISIBLE
      (adView.storeView as TextView).text = nativeAd.store
    }
    if (nativeAd.starRating == null) {
      adView.starRatingView.visibility = View.INVISIBLE
    } else {
      (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
      adView.starRatingView.visibility = View.VISIBLE
    }
    if (nativeAd.advertiser == null) {
      adView.advertiserView.visibility = View.INVISIBLE
    } else {
      (adView.advertiserView as TextView).text = nativeAd.advertiser
      adView.advertiserView.visibility = View.VISIBLE
    }

    // Assign native ad object to the native view.
    adView.setNativeAd(nativeAd)
  }

  companion object {
    // A menu item view type.
    private const val MENU_ITEM_VIEW_TYPE = 0
    private const val UNIFIED_NATIVE_AD_VIEW_TYPE = 1
  }
}
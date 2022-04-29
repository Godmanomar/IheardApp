package com.novusvista.omar.iheardapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.novusvista.omar.iheardapp.Interface.FilterListFragmentListener
import com.novusvista.omar.iheardapp.R
import com.zomato.photofilters.utils.ThumbnailItem
import kotlinx.android.synthetic.main.thumbnail_list_item.view.*

class ThumbnailAdapter (private val context:Context, private val thumbnaiilItemList:List<ThumbnailItem>,
private val listener: FilterListFragmentListener):RecyclerView.Adapter<ThumbnailAdapter.MyViewholder>() {

    private var selectIndex=0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder {
      val itemView=LayoutInflater.from(context).inflate(R.layout.thumbnail_list_item,parent,false)
        return  MyViewholder(itemView)
    }

    override fun getItemCount(): Int {
    return thumbnaiilItemList.size
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int) {
        val thumbnailItem = thumbnaiilItemList[position]
        holder.thumbNail.setImageBitmap(thumbnailItem.image)
        holder.thumbNail.setOnClickListener {
            listener.onFilterSelected(thumbnailItem.filter)
            selectIndex = position
            notifyDataSetChanged()
        }
        holder.filterName.text = thumbnailItem.filterName
        if (selectIndex == position)
            holder.filterName.setTextColor(ContextCompat.getColor(context, R.color.filter_label_selected))
        else
            holder.filterName.setTextColor(ContextCompat.getColor(context,R.color.filter_label_normal))
        }



    class MyViewholder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var thumbNail: ImageView
        var filterName: TextView

        init {
            thumbNail = itemView.thumbnail
            filterName = itemView.filter_name

        }
    }
}



package com.novusvista.omar.iheardapp


import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.novusvista.omar.iheardapp.Adapter.ThumbnailAdapter
import com.novusvista.omar.iheardapp.Interface.FilterListFragmentListener
import com.novusvista.omar.iheardapp.Utils.BitmapUtils
import com.novusvista.omar.iheardapp.Utils.SpaceItemDecoration
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import kotlinx.android.synthetic.main.fragment_filter_list.*
import java.util.logging.Filter
import kotlin.concurrent.thread

/**
 * A simple [Fragment] subclass.
 */
class FilterListFragment : Fragment(),FilterListFragmentListener {

        internal  lateinit var recycler_views:RecyclerView
       internal var listener:FilterListFragmentListener?= null
     internal  lateinit var  adapter: ThumbnailAdapter
    internal lateinit var  thumbnailItemList: MutableList<ThumbnailItem>



            fun setListener(listFragmentListener: FilterListFragmentListener)
            {
                listener=listFragmentListener
            }
    override fun onFilterSelected(filter: com.zomato.photofilters.imageprocessors.Filter) {
        if(listener!=null)
        {
            listener!!.onFilterSelected(filter)
        }
    }
    //Ctrl+O
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val itemView= inflater.inflate(R.layout.fragment_filter_list, container, false)
        thumbnailItemList=ArrayList()
        adapter= ThumbnailAdapter(activity!!,thumbnailItemList,this)

        recycler_views=itemView.findViewById<RecyclerView>(R.id.recycler_views)
        recycler_views.layoutManager=LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        recycler_views.itemAnimator=DefaultItemAnimator()
        val space=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,8f,resources.displayMetrics)
            .toInt()

recycler_views.addItemDecoration(SpaceItemDecoration(space))
        recycler_views.adapter=adapter
        
        displayImage(null)

        return itemView
    }

    fun displayImage(bitmap: Bitmap?) {
        val r= Runnable{
            var thumbImage:Bitmap?=null

               if(bitmap==null) {


                   thumbImage = BitmapUtils.getBitmapFromAssets(activity!!, MainFilterActivity.Main.IMAGE_NAME, 100, 100
                   )
               }
            else


                thumbImage=Bitmap.createScaledBitmap(bitmap,100,100,false)

                if(thumbImage==null)
                    return@Runnable
               ThumbnailsManager.clearThumbs()
                thumbnailItemList.clear()

                //add normal Bitmap first
                val thumbnailItem=ThumbnailItem()
                thumbnailItem.image=thumbImage
                thumbnailItem.filterName="Normal"
                ThumbnailsManager.addThumb(thumbnailItem)

                //Add Filter pack
                val filters = FilterPack.getFilterPack(activity!!)

                for(filter in filters)
                {
                    val item=ThumbnailItem()
                    item.image=thumbImage
                    item.filter=filter
                    item.filterName=filter.name
                    ThumbnailsManager.addThumb(item)

                }
                thumbnailItemList.addAll(ThumbnailsManager.processThumbs(activity))
                activity!!.runOnUiThread{
                    adapter.notifyDataSetChanged()
                }





        }
        Thread(r).start()

    }


}

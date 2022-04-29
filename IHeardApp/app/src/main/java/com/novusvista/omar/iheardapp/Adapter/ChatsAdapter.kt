package com.novusvista.omar.iheardapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ActionMenuView
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.novusvista.omar.iheardapp.Model.Chat
import com.novusvista.omar.iheardapp.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.message_item_left.view.*

class ChatsAdapter(
    mContext: Context,
    mChatsList:List<Chat>,
    imageUrl:String
    ):RecyclerView.Adapter<ChatsAdapter.viewHolder>()
{
    private val mContext:Context
    private val mChatsList:List<Chat>
    private val imageUrl:String
var firebaseUser: FirebaseUser=FirebaseAuth.getInstance().currentUser!!
    init {
        this.mChatsList=mChatsList
        this.mContext=mContext
        this.imageUrl=imageUrl
    }


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): viewHolder {
       return if(position==1)
       {
           val view = LayoutInflater.from(mContext).inflate(com.novusvista.omar.iheardapp.R.layout.message_item_right,parent, false)
           viewHolder(view)
       }else
       {
           val view = LayoutInflater.from(mContext).inflate(com.novusvista.omar.iheardapp.R.layout.message_item_left,parent, false)
           viewHolder(view)
       }
    }

    override fun getItemCount(): Int {
     return mChatsList.size
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
       val chat:Chat=mChatsList[position]
        Picasso.get().load(imageUrl).into(holder.profile_image)

        //image messages
   if(chat.getMessage().equals("sent you an image.")&& !chat.getUrl().equals(""))
   {
       //image message-right side
       if(chat.getSender().equals(firebaseUser!!.uid))
       {
           holder.show_text_message!!.visibility=View.GONE
           holder.right_image_view!!.visibility=View.VISIBLE
           Picasso.get().load(chat.getUrl()).into(holder.right_image_view)
       }
       //image message-left side
       else if(!chat.getSender().equals(firebaseUser!!.uid))
       {
           holder.show_text_message!!.visibility=View.GONE
           holder.left_image_view!!.visibility=View.VISIBLE
           Picasso.get().load(chat.getUrl()).into(holder.left_image_view)

       }
   }
        //text Messages
        else {
      holder.show_text_message!!.text=chat.getMessage()
     }

        //sent and seen message
        if(position==mChatsList.size-1)
        {

            if(chat.isIsSeen())
            {
                holder.text_seen!!.text="Seen"

                if(chat.getMessage().equals("sent you an image.")&& !chat.getUrl().equals(""))
                {
                    val lp:RelativeLayout.LayoutParams?=holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    lp!!.setMargins(0,245,10,0)
                    holder.text_seen!!.layoutParams=lp

                }
                else
                {
                    holder.text_seen!!.text="Sent"

                    if(chat.getMessage().equals("sent you an image.")&& !chat.getUrl().equals(""))
                    {
                        val lp:RelativeLayout.LayoutParams?=holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                        lp!!.setMargins(0,245,10,0)
                        holder.text_seen!!.layoutParams=lp

                    }

                }

            }
        }
        else
        {
            holder.text_seen!!.visibility=View.GONE

        }
    }

    inner class viewHolder(itemview:View):RecyclerView.ViewHolder(itemview)
    {
        var profile_image:CircleImageView?=null
        var show_text_message:TextView?=null
        var left_image_view:ImageView?=null
        var text_seen:TextView?=null
        var right_image_view:ImageView?=null

        init {
            profile_image=itemview.findViewById(R.id.profile_image)
            show_text_message=itemview.findViewById(R.id.show_text_message)
            left_image_view=itemview.findViewById(R.id.left_image_view)
            text_seen=itemview.findViewById(R.id.text_seen)
            right_image_view=itemview.findViewById(R.id.right_image_view)


        }
    }

    override fun getItemViewType(position: Int): Int
    {
        return if(mChatsList[position].getSender().equals(firebaseUser!!.uid))
        {
            1
        }
        else
        {
            0
        }
    }
}
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.subgpt.R
import com.subgpt.models.GenericModel
import com.subgpt.utils.SharedPrefrenceManager


class MessageListAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private lateinit var mContext: Context
    private lateinit var mMessageList: List<GenericModel>
    private lateinit var mTime: String
    private lateinit var mDate: String


    constructor(
        context: Context, messageList: List<GenericModel>
    ) : this() {
        this.mContext = context
        this.mMessageList = messageList
    }

    override fun getItemCount(): Int {
        return mMessageList.size
    }

    // Determines the appropriate ViewType according to the sender of the message.
    override fun getItemViewType(position: Int): Int {
        return if (mMessageList.get(position).owner == 0) {
            // If the current user is the sender of the message
            VIEW_TYPE_MESSAGE_SENT
        } else {
            // If some other user sent the message
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_sent, parent, false)
            return SentMessageHolder(view)
        } else {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_received, parent, false)
            return ReceivedMessageHolder(view)
        }

    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_MESSAGE_SENT -> mMessageList.get(position).message?.let {
                (holder as SentMessageHolder?)!!.bind(
                    it
                )
            }
            VIEW_TYPE_MESSAGE_RECEIVED -> mMessageList.get(
                position
            ).message?.let {
                (holder as ReceivedMessageHolder?)!!.bind(
                    it
                )
            }
        }
    }

    private inner class SentMessageHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageText: TextView

        var timeText: TextView
        var dateText: TextView
        fun bind(message: String) {
            messageText.setText(message)

            // Format the stored timestamp into a readable String using method.
            timeText.setText(mTime)
            dateText.visibility = View.VISIBLE
            dateText.setText(mDate)

        }

        init {
            messageText = itemView.findViewById(R.id.text_gchat_message_me)
            timeText = itemView.findViewById(R.id.text_gchat_timestamp_me)
            dateText = itemView.findViewById(R.id.text_gchat_date_me)
        }
    }

    private inner class ReceivedMessageHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var messageText: TextView
        var timeText: TextView
        var dateText: TextView
        var assistantName: TextView

        fun bind(message: String) {
            messageText.setText(message)

            // Format the stored timestamp into a readable String using method.
            timeText.setText(mTime)
            dateText.visibility = View.VISIBLE
            dateText.setText(mDate)
            assistantName.setText(SharedPrefrenceManager.getSharedPrefInstance().assistantName)

        }

        init {
            messageText = itemView.findViewById(R.id.text_gchat_message_other)
            assistantName = itemView.findViewById(R.id.text_gchat_user_other)
            timeText = itemView.findViewById(R.id.text_gchat_timestamp_other)
            dateText = itemView.findViewById(R.id.text_gchat_date_other)
        }
    }

    fun loadData(receivedMessage: ArrayList<GenericModel>, time: String, date: String) {
        mMessageList.apply {
            mMessageList = receivedMessage
            mTime = time
            mDate = date
            notifyDataSetChanged()
        }
    }

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }
}
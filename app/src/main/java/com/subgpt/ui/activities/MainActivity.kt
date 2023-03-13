package com.subgpt.ui.activities

import MessageListAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.stcc.mystore.network.api.ApiHelper
import com.stcc.mystore.network.api.RetrofitBuilder
import com.subgpt.R
import com.subgpt.models.BaseMessageSender
import com.subgpt.models.GenericModel
import com.subgpt.models.Message
import com.subgpt.network.Status
import com.subgpt.ui.viewmodel.HomeViewModel
import com.subgpt.viewmodels.ViewModelFactory
import org.apache.commons.text.similarity.LevenshteinDistance
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var homeViewModel: HomeViewModel
    private var mMessageRecycler: RecyclerView? = null
    private var submitButton: Button? = null
    private var textToSend: EditText? = null
    private var mMessageAdapter: MessageListAdapter? = null
    private var messageList: ArrayList<GenericModel> = arrayListOf()
    val historyMessages: ArrayList<Message> = arrayListOf()
    var questionsRef: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()
        initiliseDatabaseTable()
        mMessageRecycler = findViewById<View>(R.id.recycler_gchat) as RecyclerView
        submitButton = findViewById<View>(R.id.button_gchat_send) as Button
        textToSend = findViewById<View>(R.id.edit_gchat_message) as EditText
        mMessageAdapter = MessageListAdapter(this, messageList)
        mMessageRecycler?.setLayoutManager(LinearLayoutManager(this))
        mMessageRecycler?.setAdapter(mMessageAdapter)


        submitButton?.setOnClickListener {
            if (!textToSend?.text.isNullOrEmpty()) {
                constructRequestGPT(textToSend?.text.toString())


            }
        }
    }

    private fun constructRequestGPT(msg: String) {
        val senderRequest = BaseMessageSender()

        senderRequest.model = "gpt-3.5-turbo"
        historyMessages.add(Message(msg, "user"))
        senderRequest.messages = historyMessages


        val genericModel = GenericModel()
        genericModel.message = msg
        genericModel.owner = 0
        messageList.add(genericModel)
        SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())


        mMessageAdapter?.loadData(
            messageList,
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
            SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date())
        )


        handleQuestion(msg, senderRequest)
//        getChatGPTResponse(senderRequest)
        mMessageRecycler?.smoothScrollToPosition(messageList.size)
        textToSend?.setText("")

    }

    private fun initiliseDatabaseTable() {
        questionsRef = FirebaseDatabase.getInstance().reference.child("questions")

    }

    // Listen for new questions
    fun handleQuestion(question: String, senderRequest: BaseMessageSender) {
        // Try to find the answer in the database
        questionsRef?.child(question)?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                val answer = snapshot.getValue(String::class.java)
                val answer: String?
                val dbQuestion = snapshot.key
                val similarity = similarityScore(question, dbQuestion)
                if (similarity >= 0.8) {
                    answer = snapshot.getValue(String::class.java)
                    Log.d("MyApp", "Answer: $answer")
                } else {
                    answer = snapshot.getValue(String::class.java)
                }

                if (answer != null) {
                    val genericModel = GenericModel()
                    genericModel.message = answer
                    genericModel.owner = 1
                    messageList.add(genericModel)

                    historyMessages.add(Message(answer, "assistant"))

                    mMessageAdapter?.loadData(
                        messageList,
                        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                        SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date())
                    )

                    mMessageRecycler?.smoothScrollToPosition(messageList.size)
                } else {
                    getChatGPTResponse(question, senderRequest)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                println("Error: ${error.message}")
            }
        })
    }


    fun similarityScore(str1: String?, str2: String?): Double {
        if (str1 == null || str2 == null) {
            return 0.0
        }
        val distance = LevenshteinDistance().apply(str1.toLowerCase(), str2.toLowerCase())
        val maxLength = maxOf(str1.length, str2.length)
        return 1.0 - (distance.toDouble() / maxLength.toDouble())
    }

    private fun setupViewModel() {

        homeViewModel =
            ViewModelProvider(this, ViewModelFactory(ApiHelper(RetrofitBuilder.apiService))).get(
                HomeViewModel::class.java
            )

    }


    private fun getChatGPTResponse(question: String, senderRequest: BaseMessageSender) {
        homeViewModel.sendToChatGPT(senderRequest).observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {

                        if (resource.data?.raw()?.code == 200) {
                            if (resource.data.body()?.choices?.get(0)?.message?.content?.isNotEmpty() == true) {
                                val filteredString =
                                    validateGotString(resource.data.body()?.choices?.get(0)?.message?.content.toString())

                                val genericModel = GenericModel()
                                genericModel.message = filteredString
                                genericModel.owner = 1
                                messageList.add(genericModel)

                                questionsRef?.child(question)?.setValue(filteredString)
                                historyMessages.add(Message(filteredString, "assistant"))

                                mMessageAdapter?.loadData(
                                    messageList,
                                    SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
                                    SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date())
                                )
                                mMessageRecycler?.smoothScrollToPosition(messageList.size)

                            }
                        }

                    }
                    Status.ERROR -> {

                    }
                    Status.LOADING -> {

                    }
                    else -> {

                    }
                }
            }
        })
    }

    private fun validateGotString(input: String): String {
        val lastIndex = input.lastIndexOf("\n")
        val secondPart = input.substring(lastIndex + 1)
        return secondPart
    }


}
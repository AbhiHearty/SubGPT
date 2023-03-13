package com.subgpt.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.subgpt.R
import com.subgpt.utils.SharedPrefrenceManager

class MasterSettingsActivity : AppCompatActivity() {
    lateinit var apiString: TextInputEditText
    lateinit var assistantName: TextInputEditText
    lateinit var resetButton: MaterialButton
    lateinit var submitButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_master_settings)
        apiString = findViewById(R.id.et_api_key)
        assistantName = findViewById(R.id.et_assistant_name)
        resetButton = findViewById(R.id.bt_reset)
        submitButton = findViewById(R.id.bt_submit)


        resetButton.setOnClickListener {

        }

        submitButton.setOnClickListener {
            SharedPrefrenceManager.getSharedPrefInstance().assistantName =
                assistantName.getText().toString()
            SharedPrefrenceManager.getSharedPrefInstance().apiKey =
                apiString.getText().toString()

            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}
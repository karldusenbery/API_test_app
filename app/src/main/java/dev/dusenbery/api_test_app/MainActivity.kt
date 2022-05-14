package dev.dusenbery.api_test_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONObject
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener
    {

        var sunSign = "Aries"
        var resultView: TextView? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            var buttonView: Button = findViewById(R.id.button)
            button.setOnClickListener {
                GlobalScope.async {
                    getPredictions(buttonView)
                }

            }

            val spinner = findViewById<Spinner>(R.id.spinner)
            val adapter = ArrayAdapter.createFromResource(this,R.array.sunsigns,android.R.layout.simple_spinner_item)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter;
            spinner.onItemSelectedListener = this

            resultView = findViewById(R.id.resultView)

        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            sunSign = "Aries"
        }
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (parent != null) {
                sunSign = parent.getItemAtPosition(position).toString()
            }
        }

        public suspend fun getPredictions(view: android.view.View) {
            try {
                val result = GlobalScope.async {
                    callAztroAPI("https://sameer-kumar-aztro-v1.p.rapidapi.com/?sign=" + sunSign + "&day=today")
                }.await()

                onResponse(result)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun callAztroAPI(apiUrl:String ):String?{
            var result: String? = ""
            val url: URL;
            var connection: HttpURLConnection? = null
            try {
                url = URL(apiUrl)
                connection = url.openConnection() as HttpURLConnection
                // set headers for the request
                // set host name
                connection.setRequestProperty("x-rapidapi-host", "sameer-kumar-aztro-v1.p.rapidapi.com")

                // set the rapid-api key
                connection.setRequestProperty("x-rapidapi-key", "<YOUR_RAPIDAPI_KEY>")
                connection.setRequestProperty("content-type", "application/x-www-form-urlencoded")
                // set the request method - POST
                connection.requestMethod = "POST"
                val `in` = connection.inputStream
                val reader = InputStreamReader(`in`)

                // read the response data
                var data = reader.read()
                while (data != -1) {
                    val current = data.toChar()
                    result += current
                    data = reader.read()
                }
                return result
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // if not able to retrieve data return null
            return null

        }

    }
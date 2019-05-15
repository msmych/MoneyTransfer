package transfer

import com.google.gson.Gson
import com.google.inject.Singleton
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

@Singleton
class RestClient {

    private val baseUrl = "http://localhost:8080/"

    private val gson = Gson()

    fun <T> get(path: String, type: Class<T>): T? {
        val http = URL("$baseUrl$path").openConnection()
        http.connect()
        return gson.fromJson(BufferedReader(InputStreamReader(http.inputStream)), type)
    }

    fun post(path: String, body: Any): HttpURLConnection {
        val http = URL("$baseUrl$path").openConnection() as HttpURLConnection
        http.doOutput = true
        http.requestMethod = "POST"
        val osw = OutputStreamWriter(http.outputStream)
        osw.write(gson.toJson(body))
        osw.close()
        return http
    }

    fun delete(path: String): HttpURLConnection {
        val http = URL("$baseUrl$path").openConnection() as HttpURLConnection
        http.requestMethod = "DELETE"
        return http
    }
}
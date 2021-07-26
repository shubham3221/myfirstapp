package com.example.myfirstapp.GoogleMaps.autocomplete

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class Suggestions(lifecycle: Lifecycle,val  mAdapter: SuggestionAdapter) : TextWatcher, LifecycleObserver {

    private var scope: LifecycleCoroutineScope
    init {
        lifecycle.addObserver(this)
        scope = lifecycle.coroutineScope
    }


    fun getSuggestions(text: String, callback: (ArrayList<String>) -> Unit) {
        val url = URL("https://suggestqueries.google.com/complete/search?q=$text&client=toolbar")
        val dbf = DocumentBuilderFactory.newInstance()
        val db = dbf.newDocumentBuilder()
        val doc: Document = db.parse(InputSource(url.openStream()))
        doc.documentElement.normalize()

        val nodeList: NodeList = doc.getElementsByTagName("CompleteSuggestion")
        var list = ArrayList<String>()
        for (i in 0 until nodeList.length) {
            if (nodeList.item(i).nodeType == Node.ELEMENT_NODE) {
                val node: Node = nodeList.item(i)
                val element = node as Element
                val elementsByTagName = element.getElementsByTagName("suggestion")
                list.add(elementsByTagName.item(0).attributes.getNamedItem("data").nodeValue)
            }
        }
        Log.e("//", "getAutocomplete: size: ${list.size} ")
        callback(list)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s!!.length > 2) {
            scope.launch(Dispatchers.IO){
                getSuggestions(s.toString()) {
                    launch(Dispatchers.Main) {
                       mAdapter.updateList(it)
                    }
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {

    }

}
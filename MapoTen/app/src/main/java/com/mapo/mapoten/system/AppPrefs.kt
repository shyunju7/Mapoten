package com.mapo.mapoten.system

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE

class AppPrefs(context: Context) {

    private val prefNm="mPref"
    private val prefs=context.getSharedPreferences(prefNm,MODE_PRIVATE)
    private val context = context

    var token:String?
        get() = prefs.getString("token",null)
        set(value){
            prefs.edit().putString("token",value).apply()
        }

    var type:String?
        get() = prefs.getString("type",null)
        set(value){
            prefs.edit().putString("type",value).apply()
        }



    fun logout() {
        val editor = context.getSharedPreferences(prefNm, Application.MODE_PRIVATE)
        with(editor.edit()){
            clear()
            commit()
            apply()
        }

    }

}
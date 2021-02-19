package com.hongmei.garbagesort.util

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

object GsonFactory {
    const val TAG = "GsonFactory"

    @JvmStatic
    val GSON: Gson by lazy {
        GsonBuilder()
            .registerTypeAdapter(
                object : TypeToken<TreeMap<String, Any>>() {

                }.type, MapTypeAdapter()
            ).create()
    }

    fun toJson(o: Any?): String {
        return GSON.toJson(o)
    }

    fun <T> fromJson(json: String?, type: Type): T? {
        return try {
            GSON.fromJson(json, type)
        } catch (e: Exception) {
            Log.e(TAG, "fromJson ", e)
            null
        }
    }

    inline fun <reified T> mapToBean(map: Map<out Any?, Any?>?): T? {
        if (map == null) {
            return null
        }
        return map.toJson().toObject<T>(T::class.java)
    }

    class MapTypeAdapter : TypeAdapter<Any>() {

        @Throws(IOException::class)
        override fun read(input: JsonReader): Any? {
            when (input.peek()) {
                JsonToken.BEGIN_ARRAY -> {
                    val list = ArrayList<Any?>()
                    input.beginArray()
                    while (input.hasNext()) {
                        list.add(read(input))
                    }
                    input.endArray()
                    return list
                }

                JsonToken.BEGIN_OBJECT -> {
                    val map = LinkedTreeMap<String, Any>()
                    input.beginObject()
                    while (input.hasNext()) {
                        map[input.nextName()] = read(input)
                    }
                    input.endObject()
                    return map
                }

                JsonToken.STRING -> return input.nextString()

                JsonToken.NUMBER -> {
                    /**
                     * 改写数字的处理逻辑，将数字值分为整型与浮点型。
                     */
                    val dbNum = input.nextDouble()

                    // 数字超过long的最大值，返回浮点类型
                    if (dbNum > java.lang.Long.MAX_VALUE) {
                        return dbNum
                    }

                    // 判断数字是否为整数值
                    val lngNum = dbNum.toLong()
                    return if (dbNum == lngNum.toDouble()) {
                        lngNum
                    } else {
                        dbNum
                    }
                }

                JsonToken.BOOLEAN -> return input.nextBoolean()

                JsonToken.NULL -> {
                    input.nextNull()
                    return null
                }

                else -> throw IllegalStateException()
            }
        }

        @Throws(IOException::class)
        override fun write(out: JsonWriter, value: Any) {
            // 序列化无需实现
        }

    }

}

fun <T> String?.toObject(type: Type): T? = GsonFactory.fromJson(this, type)

fun Any?.toJson() = GsonFactory.toJson(this)
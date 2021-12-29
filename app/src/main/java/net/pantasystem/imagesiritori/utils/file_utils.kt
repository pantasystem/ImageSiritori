package net.pantasystem.imagesiritori.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

fun Context.getFileName(uri: Uri) : String{
    return when(uri.scheme){
        "content" ->{
            this.contentResolver
                .query(uri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)?.use{
                    if(it.moveToFirst()){
                        val index = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                        if(index != -1) {
                            it.getString(index)
                        }else{
                            null
                        }
                    }else{
                        null
                    }
                }?: throw IllegalArgumentException("ファイル名の取得に失敗しました")
        }
        "file" ->{
            java.io.File(uri.path!!).name
        }
        else -> throw IllegalArgumentException("scheme不明")
    }
}
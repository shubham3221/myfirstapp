package com.example.myfirstapp.music.helper

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.myfirstapp.Myconstants
import com.example.myfirstapp.music.MusicModel
import java.io.File

object MusicHelper {
    fun getPlayList(rootPath: String?): ArrayList<HashMap<String, String>>? {
        val fileList: ArrayList<HashMap<String, String>> = ArrayList()
        return try {
            val rootFolder = File(rootPath)
            val files: Array<File> =
                rootFolder.listFiles() //here you will get NPE if directory doesn't contains  any file,handle it like this.
            for (file in files) {
                if (file.isDirectory()) {
                    if (getPlayList(file.getAbsolutePath()) != null) {
                        fileList.addAll(getPlayList(file.getAbsolutePath())!!)
                    } else {
                        break
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    val song: HashMap<String, String> = HashMap()
                    song["file_path"] = file.getAbsolutePath()
                    song["file_name"] = file.getName()
                    fileList.add(song)
                }
            }
            fileList
        } catch (e: Exception) {
            Log.e(Myconstants.TAG, "getPlayList: " + e.message)
            null
        }
    }
    fun getAudioFromSpecificfolder(context: Context, path: String): MutableList<MusicModel> {
        val tempMusicList: MutableList<MusicModel> = ArrayList()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST)
        val c: Cursor? = context.contentResolver.query(uri,
            projection,
            MediaStore.Audio.Media.DATA + " like ? ",
            arrayOf(path),
            null)
        if (c != null) {
            while (c.moveToNext()) {
                val audioModel = MusicModel()
                val id = c.getString(c.getColumnIndex(MediaStore.Audio.Media._ID))
                val path: String = c.getString(0)
                val album: String = c.getString(1)
                val artist: String = c.getString(2)
                val name = path.substring(path.lastIndexOf("/") + 1)

                audioModel.id = id.toString().toLong()
                audioModel.name = name
                audioModel.album = (album)
                audioModel.artist = (artist)
                audioModel.path = (path)
                tempMusicList.add(audioModel)
            }
            c.close()
        }
        return tempMusicList
    }
    fun getAllAudioFromDevice(context: Context): MutableList<MusicModel> {
        val tempMusicList: MutableList<MusicModel> = ArrayList()
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.ArtistColumns.ARTIST,MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DURATION)
        val c: Cursor? = context.contentResolver.query(uri,
            projection, null, null, null)
        if (c != null) {
            while (c.moveToNext()) {
                val audioModel = MusicModel()
                val id = c.getString(c.getColumnIndex(MediaStore.Audio.Media._ID))
                val path: String = c.getString(0)
                val album: String = c.getString(1)
                val artist: String = c.getString(2)
                val duration =
                    c.getString(c.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val name = path.substring(path.lastIndexOf("/") + 1)
                audioModel.id = id.toString().toLong()
                audioModel.name = name
                audioModel.album = (album)
                audioModel.artist = (artist)
                audioModel.path = (path)
                audioModel.duration = millisecondsToTime(duration.toString().toLong())
                Log.e("Name :$name", " Album :$album")
                Log.e("Path :$path", " Artist :$artist")
                tempMusicList.add(audioModel)
            }
            c.close()
        }
        return tempMusicList
    }
    fun getsongs(context: Context): MutableList<MusicModel>{
        val music: MutableList<MusicModel> = ArrayList()
        val resolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val Selection = MediaStore.Audio.Media.IS_MUSIC
        val sortorder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = resolver.query(uri, null, Selection, null, sortorder)
        if (cursor != null) {
            while (cursor!!.moveToNext()) {
                val audioModel = MusicModel()
                val id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                val duration =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                val album: String? = cursor.getString(1)
                val songname =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                val path: String? = cursor.getString(0)
                var artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                if (artist.contentEquals("<unknown>")) {
                    artist = "unknown artist"
                }
                audioModel.id = id.toString().toLong()
                audioModel.name = songname
                audioModel.album = (album)
                audioModel.artist = (artist)
                audioModel.path = (path)
                val mpath: String = cursor.getString(0)
                Log.e("//", "getsongs: path: ${mpath}" )
                audioModel.duration = millisecondsToTime(duration.toString().toLong())
                music.add(audioModel)
            }
        }
        cursor!!.close()
        return music
    }
    private fun millisecondsToTime(milliseconds: Long): String? {
        val minutes = milliseconds / 1000 / 60
        val seconds = milliseconds / 1000 % 60
        val secondsStr = java.lang.Long.toString(seconds)
        val secs: String
        secs = if (secondsStr.length >= 2) {
            secondsStr.substring(0, 2)
        } else {
            "0$secondsStr"
        }
        return "$minutes:$secs"
    }

}
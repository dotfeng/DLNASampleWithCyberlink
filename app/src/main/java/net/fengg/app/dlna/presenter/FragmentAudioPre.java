package net.fengg.app.dlna.presenter;

import java.util.ArrayList;
import java.util.List;

import net.fengg.app.dlna.model.Audio;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

public class FragmentAudioPre {
	public static List<String> getAudioPathList(Context context) {
		List<String> audios = new ArrayList<String>();
		String[] str = new String[] { MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.DATA 
		};
		Cursor cursor = context.getContentResolver()
				.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, str,
						null,null, null);
		if(cursor == null) 
			return audios;
		if (cursor.moveToFirst()) {
			do {
				audios.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
			} while (cursor.moveToNext());
		}
		cursor.close();
		
		return audios;
	}
	
	public static List<Audio> getAudios(Context context) {
		List<Audio> audios = new ArrayList<Audio>();
		String[] str = new String[] { 
				MediaStore.Audio.Media._ID,
				MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.SIZE,
				MediaStore.Audio.Media.ALBUM,
				MediaStore.Audio.Media.TITLE,
				MediaStore.Audio.Media.ALBUM_ID,
				MediaStore.Audio.Media.DATA 
		};
		Cursor cursor = context.getContentResolver()
				.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, str,
						null,null, null);
		if(cursor == null) 
			return audios;
		if (cursor.moveToFirst()) {
			do {
				Audio audio = new Audio();
                audio.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
                audio.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                audio.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                audio.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
                audio.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)));
                audio.setTilte(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)));
                audio.setAlbumId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
				audio.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
				audios.add(audio);
			} while (cursor.moveToNext());
		}
		cursor.close();
		
		return audios;
	}
}

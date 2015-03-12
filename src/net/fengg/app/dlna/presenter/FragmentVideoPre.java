package net.fengg.app.dlna.presenter;

import java.util.ArrayList;
import java.util.List;

import net.fengg.app.dlna.model.Video;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;


public class FragmentVideoPre {
	public static List<String> getVideoPathList(Context context) {

		List<String> videos = new ArrayList<String>();
		String[] str = new String[] { MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DATA // 目录
		};
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, str, null, null,
				null);
		if(cursor == null)
			return videos;
		if (cursor.moveToFirst()) {
			do {
				videos.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
			} while (cursor.moveToNext());
		}

		cursor.close();
		return videos;
	}
	
	public static List<Video> getVideoFromSD(Context context) {

		List<Video> videos = new ArrayList<Video>();
		String[] str = new String[] { MediaStore.Video.Media._ID,
				MediaStore.Video.Media.ALBUM,
				MediaStore.Video.Media.TITLE // 标题
				,
				MediaStore.Video.Media.MIME_TYPE // 类型
				,
				MediaStore.Video.Media.DURATION // 时长
				, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
				MediaStore.Video.Media.DATA // 目录
				, MediaStore.Video.Media.DISPLAY_NAME // 名字带扩展名
				, MediaStore.Video.Media.RESOLUTION // 分辨率
				, MediaStore.Video.Media.SIZE // 大小
		};
		Cursor cursor = context.getContentResolver().query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, str, null, null,
				null);
		if(cursor == null)
			return videos;
		if (cursor.moveToFirst()) {
			do {
				Video video = new Video();
				video.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)));
				video.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)));
				video.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)));
				video.setType(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE)));
				video.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));
				video.setDirectory(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
				video.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
				video.setResolution(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION)));
				video.setSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));

				videos.add(video);
			} while (cursor.moveToNext());
		}

		cursor.close();
		return videos;
	}
}

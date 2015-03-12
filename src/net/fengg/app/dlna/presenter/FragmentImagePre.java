package net.fengg.app.dlna.presenter;

import java.util.ArrayList;
import java.util.List;

import net.fengg.app.dlna.model.Image;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;


public class FragmentImagePre {
	public static List<String> getImagePathList(Context context) {
		List<String> images = new ArrayList<String>();
		String[] str = new String[] { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA 
		};
		Cursor cursor = context.getContentResolver()
				.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, str,
						null,null, null);
		if(cursor == null) 
			return images;
		if (cursor.moveToFirst()) {
			do {
				images.add(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return images;
	}

	
	public static List<Image> getImageFromSD(Context context) {

		List<Image> images = new ArrayList<Image>();
		String[] str = new String[] { MediaStore.Images.Media._ID,
				MediaStore.Images.Media.DATA 
				, MediaStore.Images.Media.DISPLAY_NAME 
				, MediaStore.Images.Media.TITLE 
				, MediaStore.Images.Media.MIME_TYPE 
				, MediaStore.Images.Media.SIZE 
				, MediaStore.Images.Media.DATE_ADDED 
		// ,MediaStore.Images.Media.getBitmap(null, null);
		};
		Cursor cursor = context.getContentResolver()
				.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, str,
						null,null, null);
		if(cursor == null) 
			return images;
		if (cursor.moveToFirst()) {
			do {
				Image image = new Image();
				image.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)));
				image.setDirectory(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
				image.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME )));
				image.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE)));
				image.setType(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)));
				image.setSize(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE )));
				image.setDateAdded(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED )));
				images.add(image);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return images;
	}
}

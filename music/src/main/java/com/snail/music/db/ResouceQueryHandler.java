package com.snail.music.db;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.widget.CursorAdapter;

/**
 * Created by ping on 2016/5/16.
 */
public class ResouceQueryHandler extends AsyncQueryHandler {

    public ResouceQueryHandler(ContentResolver cr) {
        super(cr);
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        if(cookie instanceof CursorAdapter){
            ((CursorAdapter)cookie).changeCursor(cursor);
        }
    }
}

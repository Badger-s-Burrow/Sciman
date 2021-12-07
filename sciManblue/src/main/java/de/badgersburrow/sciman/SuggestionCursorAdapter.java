package de.badgersburrow.sciman;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

public class SuggestionCursorAdapter extends CursorAdapter
{

    /*@SuppressWarnings("deprecation")
	public SuggestionSimpleCursorAdapter(Context context, int layout, Cursor c,
            String[] from, int[] to) {
        super(context, layout, c, from, to);
    }*/

    public SuggestionCursorAdapter(Context context, Cursor c, boolean flag) {
        super(context, c, flag);
    }

    @Override
    public CharSequence convertToString(Cursor cursor) {

        int indexColumnSuggestion = cursor.getColumnIndex(SuggestionsDatabase.FIELD_SUGGESTION);

        return cursor.getString(indexColumnSuggestion);
    }

	@Override
	public void bindView(View arg0, Context arg1, Cursor arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}

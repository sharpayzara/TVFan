package tvfan.tv.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class MsgHelper {
	// "CREATE TABLE IF NOT EXISTS USER_TAB (id INTEGER PRIMARY KEY AUTOINCREMENT, userid TEXT, wxid TEXT, wxname TEXT, wxheadimgurl TEXT)"
	private LocalData _ld;

	public MsgHelper(Context context) {
		_ld = new LocalData(context);
	}

	/**
	 * 新增用户
	 **/
	public int addMsg(ContentValues msgData) {
		removeMsg(msgData.getAsString("msgid"));
		return _ld.execInsert("MSG_TAB", null, msgData);
	}

	/**
	 * 查询单条msg
	 **/
	public ContentValues getMsg(String msgid) {
		ContentValues cv = new ContentValues();
		Cursor cursor = _ld.runQuery("SELECT * FROM MSG_TAB WHERE msgid = \""
				+ msgid + "\"", null);
		if (cursor.moveToFirst()) {
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				cv.put(cursor.getColumnName(i), cursor.getString(i));
			}
		}
		cursor.close();

		return cv;
	}

	/**
	 * 获取所有Msg
	 */
	public String[] queryAllMsg() {
		Cursor cursor = null;
		String[] arrMsgInfo = null;
		try {
			cursor = _ld.runQuery("SELECT * FROM MSG_TAB", null);
			arrMsgInfo = new String[cursor.getCount()];
			int i = 0;
			while (cursor.moveToNext()) {
				arrMsgInfo[i] = cursor.getString(1);
				i++;
			}
		} catch (Exception ex) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return arrMsgInfo;
	}

	/**
	 * 删除信息
	 **/
	public void removeMsg(String msgid) {
		_ld.execSQL("DELETE FROM MSG_TAB WHERE msgid = \"" + msgid + "\"");
	}
}

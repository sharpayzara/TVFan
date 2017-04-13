package tvfan.tv.dal;

import java.util.ArrayList;

import tvfan.tv.dal.models.UserInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class UserHelper {
	// "CREATE TABLE IF NOT EXISTS USER_TAB (id INTEGER PRIMARY KEY AUTOINCREMENT, userid TEXT, wxid TEXT, wxname TEXT, wxheadimgurl TEXT)"
	private LocalData _ld;

	public UserHelper(Context context) {
		_ld = new LocalData(context);
	}

	/**
	 * 新增用户
	 * 
	 * @Example ContentValues userData = new ContentValues();<br />
	 *          userData.put("userid", userid);<br />
	 *          userData.put("wxid", wxid);<br />
	 *          userData.put("wxname", wxname);<br />
	 *          userData.put("wxheadimgurl", wxheadimgurl);
	 *          userData.put("token", token);
	 **/
	public int addUser(ContentValues userData) {
		removeUser(userData.getAsString("userid"));
		userData.put("ts", System.currentTimeMillis());
		return _ld.execInsert("USER_TAB", null, userData);
	}

	/**
	 * 查询单个用户
	 **/
	public ContentValues getUser(String userid) {
		ContentValues cv = new ContentValues();
		Cursor cursor = _ld.runQuery("SELECT * FROM USER_TAB WHERE userid = \""
				+ userid + "\"", null);
		if (cursor.moveToFirst()) {
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				cv.put(cursor.getColumnName(i), cursor.getString(i));
			}
		}
		cursor.close();

		return cv;
	}

	/**
	 * 获取所有用户
	 */
	public ArrayList<UserInfo> queryAllUser() {
		Cursor cursor = null;
		ArrayList<UserInfo> arrUserInfo = new ArrayList<UserInfo>();
		try {
			cursor = _ld.runQuery("SELECT * FROM USER_TAB", null);

			while (cursor.moveToNext()) {
				UserInfo userInfo = new UserInfo();
				userInfo.setId(cursor.getInt(0));
				userInfo.setUserid(cursor.getString(1));
				userInfo.setWxid(cursor.getString(2));
				userInfo.setWxname(cursor.getString(3));
				userInfo.setWxheadimgurl(cursor.getString(4));
				userInfo.setToken(cursor.getString(5));
				arrUserInfo.add(userInfo);
			}
		} catch (Exception ex) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return arrUserInfo;
	}

	/**
	 * 删除用户
	 **/
	public void removeUser(String userid) {
		_ld.execSQL("DELETE FROM USER_TAB WHERE userid = \"" + userid + "\"");
	}
}

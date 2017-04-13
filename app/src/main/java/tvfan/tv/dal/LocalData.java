package tvfan.tv.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

final public class LocalData {
	private static class SQLiteDBHelper extends SQLiteOpenHelper {
		private static final String DB_NAME = "cibnepg.sqlite";
		private static final String KV_TABLE = "CREATE TABLE IF NOT EXISTS KV_TAB (id INTEGER PRIMARY KEY AUTOINCREMENT, key TEXT, value TEXT)";
		private static final String USER_TABLE = "CREATE TABLE IF NOT EXISTS USER_TAB (id INTEGER PRIMARY KEY AUTOINCREMENT, userid TEXT, wxid TEXT, wxname TEXT, wxheadimgurl TEXT, token TEXT, ts TIMESTAMP)";
		private static final String FAVORITE_TABLE = "CREATE TABLE IF NOT EXISTS FAVORITE_TAB (id INTEGER PRIMARY KEY AUTOINCREMENT, programSeriesId TEXT, latestEpisode TEXT,ts TIMESTAMP)";
		private static final String MSG_TABLE = "CREATE TABLE IF NOT EXISTS MSG_TAB (id INTEGER PRIMARY KEY AUTOINCREMENT, msgid TEXT, mark TEXT)";
		private static final String PLAYREC_TABLE = "CREATE TABLE IF NOT EXISTS PLAYERECORD " 
				+"(ID INTEGER PRIMARY KEY AUTOINCREMENT, " 			
				+"EPGID VARCHAR(20)," //epgID
				+"DETAILSID VARCHAR(20),"//当前影片详细ID
				+"PLAYERNAME VARCHAR(100),"//当前影片NAME
				+"PLAYERPOS INTEGER,"//当前影片集数
				+"POINTIME INTEGER,"//当前断点
				+"TOTALTIME INTEGER,"//总时长
				+"PLAYEREND INTEGER," //影片是否播放完成 0：未播放完 1：已播放完 
				+"PLAYERFAV INTEGER,"//影片是否收藏 0：未收藏 1：已收藏		
				+"TYPE VARCHAR(20),"
				+"PICURL VARCHAR(20),"
				+"LIVENO VARCHAR(20),"
				+"ISSINGLE INTEGER,"
				+"ACTIONURL VARCHAR(400),"
				+"TYPEICON VARCHAR(400),"
				+"PRICEICON VARCHAR(400),"
				+"DATETIME NUMERIC,"
				+"CPID VARCHAR(20),"
				+"PAGENUM INTEGER,"
				+"YEARNUM INTEGER,"
				+"HISTORYINFO INTEGER)";//DATETIME DEFAULT CURRENT_TIMESTAMP
		
		public SQLiteDBHelper(Context context) {
			super(context, DB_NAME, null, 5);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(KV_TABLE);
			db.execSQL(USER_TABLE);
			db.execSQL(FAVORITE_TABLE);
			db.execSQL(PLAYREC_TABLE);
			db.execSQL(MSG_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

	private static SQLiteDBHelper _dbHelper;
	private static SQLiteDatabase _readableDatabase;
	private static SQLiteDatabase _writableDatabase;

	public LocalData(Context context){
//		_dbHelper = new SQLiteDBHelper(context);
//		_readableDatabase = _getReadableDatabase();
//		_writableDatabase = _getWritableDatabase();
	}

	public static void initLocalData(Context context){
		_dbHelper = new SQLiteDBHelper(context);
		_readableDatabase = _getReadableDatabase();
		_writableDatabase = _getWritableDatabase();
	}
	/**
	 * 获得可写实例
	 **/
	private static SQLiteDatabase _getWritableDatabase() {
		return _dbHelper.getWritableDatabase();
	}

	/**
	 * 获得只读实例
	 **/
	private static SQLiteDatabase _getReadableDatabase() {
		return _dbHelper.getReadableDatabase();
	}

	/**
	 * 运行SQL查询
	 **/
	public Cursor runQuery(String sql, String[] selectionArgs) {
		return _readableDatabase.rawQuery(sql, selectionArgs);
	}

	/**
	 * 执行更新
	 **/
	public int execUpdate(String table, ContentValues values, String whereClause, String[] whereArgs) {
		return _writableDatabase.update(table, values, whereClause, whereArgs);
	}

	/**
	 * 执行删除
	 **/
	public int execDelete(String table, String whereClause, String[] whereArgs) {
		return _writableDatabase.delete(table, whereClause, whereArgs);
	}

	/**
	 * 执行新增
	 **/
	public int execInsert(String table, String nullColumnHack, ContentValues values) {
		return (int) _writableDatabase.insert(table, nullColumnHack, values);
	}

	/**
	 * 执行SQL语句
	 **/
	public void execSQL(String sql) {
		_writableDatabase.execSQL(sql);
	}

	/**
	 * 新增键值对
	 **/
	public void setKV(String key, String value) {
		removeKV(key);
		ContentValues cv = new ContentValues();
		cv.put("key", key);
		cv.put("value", value);
		execInsert("KV_TAB", "id", cv);
//		execSQL("INSERT INTO KV_TAB (key,value) VALUES (\""+key+"\", \""+value+"\")");
	}

	public static void sSetKV(String key, String value) {
		_writableDatabase.execSQL("DELETE FROM KV_TAB WHERE key = \""+key+"\"");
		ContentValues cv = new ContentValues();
		cv.put("key", key);
		cv.put("value", value);
		_writableDatabase.insert("KV_TAB", "id", cv);
	}

	/**
	 * 读取键值对
	 **/
	public String getKV(String key) {
		String val = null;
		Cursor cursor = runQuery("SELECT value FROM KV_TAB WHERE key = \"" + key + "\"", null);
		if(cursor.moveToFirst())
			val = cursor.getString(0);
		cursor.close();
		return val;
	}

	public static String sGetKV(String key) {
		String val = null;
		Cursor cursor = _readableDatabase.rawQuery("SELECT value FROM KV_TAB WHERE key = \"" + key + "\"", null);
		if(cursor.moveToFirst())
			val = cursor.getString(0);
		cursor.close();
		return val;
	}

	/**
	 * 通过前缀读取键值对集合
	 **/
	public ContentValues getKVbyPrefix(String prefix) {
		ContentValues cv = new ContentValues();
		
		Cursor cursor = runQuery("SELECT key, value FROM KV_TAB WHERE key LIKE \"" + prefix + "%\"", null);
		while(cursor.moveToNext())
			cv.put(cursor.getString(0), cursor.getString(1));

		cursor.close();
		return cv;
	}

	/**
	 * 删除键值对
	 **/
	public void removeKV(String key) {
		execSQL("DELETE FROM KV_TAB WHERE key = \""+key+"\"");
	}
}

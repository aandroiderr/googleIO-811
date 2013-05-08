package com.example.signintest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
	private Context mContext;
	public static String USER_TABLE = "users";
	public static String IDP_TABLE = "providers";
	public static String KEY_ID = "id";
	public static String KEY_NAME = "name";
	public static String KEY_PROVIDER = "provider";
	public static String KEY_PROVIDER_ID = "providerId";
	public static String KEY_USER_ID = "userId";
	private DatabaseHelper mHelper;
	private SQLiteDatabase mDb;
	
	public DBAdapter(Context context) {
		mContext = context;
		mHelper = new DatabaseHelper(mContext);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		public DatabaseHelper(Context context) {
			super(context, "appdb", null, 1);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL("CREATE TABLE " + USER_TABLE + 
						KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
						KEY_NAME + " VARCHAR )");
				db.execSQL("CREATE TABLE " + IDP_TABLE + 
						KEY_PROVIDER + " VARCHAR," +
						KEY_USER_ID + " INTEGER," +
						KEY_PROVIDER_ID + " VARCHAR )"); 
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
			db.execSQL("DROP TABLE IF EXISTS " + IDP_TABLE);
			onCreate(db);
		}
	}
	
	public DBAdapter open() throws SQLException {
		mDb = mHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		mHelper.close();
	}
	
	/**
	 * Retrieve the user ID based on a IDP identifier. Will return
	 * null if no matching user exists.
	 * 
	 * @param provider
	 * @param userId
	 * @return
	 */
	public Integer getUserId (Provider provider, String idpUserId) {
		String[] projection = new String[] {
				KEY_PROVIDER,
				KEY_PROVIDER_ID,
				KEY_USER_ID
		};
		String where = String.format("%s=? AND %s=?", KEY_PROVIDER, KEY_PROVIDER_ID);
		Cursor cursor = mDb.query(IDP_TABLE, projection, 
				where, new String[] {provider.getId(), idpUserId}, 
				null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor.getCount() == 1 ? Integer.valueOf(cursor.getInt(2)) : null;
	}
	
	/**
	 * Create a user in the database associated with the given provider. 
	 * 
	 * @param provider
	 * @param userId
	 * @param name
	 * @return
	 */
	public long createUser(Provider provider, SignInUser user) {
		ContentValues row = new android.content.ContentValues();
		row.put(KEY_NAME, user.getName());
		long userId = mDb.insert(USER_TABLE, null, row);
		associateUser(provider, user, userId);
		return userId;
	}
	
	/**
	 * Associate a IDP identity with an existing application identity. 
	 * 
	 * @param provider
	 * @param providerId
	 * @param userId
	 * @return
	 */
	public boolean associateUser(Provider provider, SignInUser user, long userId) {
		ContentValues row = new android.content.ContentValues();
		row.put(KEY_PROVIDER, provider.getId());
		row.put(KEY_PROVIDER_ID, user.getProviderUserId(provider));
		row.put(KEY_USER_ID, userId);
		return mDb.insert(IDP_TABLE, null, row) > 0;
	}
	
	/**
	 * Disassociate a IDP identity with an existing application identity. 
	 * 
	 * @param provider
	 * @param userId
	 * @return
	 */
	public boolean deleteProviderUser(Provider provider, SignInUser user) {
		return mDb.delete(IDP_TABLE, 
				String.format("%s=? AND %s=?", KEY_PROVIDER, KEY_USER_ID), 
				new String[] {provider.getId(), user.getProviderUserId(provider)}) > 0;
	}
}

package com.example.retrievevideo.emergencymessaging;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.example.retrievevideo.BuildConfig;
import com.example.retrievevideo.R;




/**
 * Created by m.susmitha on 7/5/15.
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final String _TMP = "_tmp";
	public static final String TAG = "DBHelper";
	private Context context;
	private static DBHelper instance;
	public static final boolean storeDBOnSDCard = false;

	private static final String SQLITE_SEQUENCE_TABLE = "sqlite_sequence";
	private static final String SEQUENCE_COLUMN = "seq";
	private static final String TABLE_NAME_COLUMN = "name";

	private static final String TABLE_CONTACTS = "contacts";
	private static final String TABLE_CONTACTS_COLUMN_ID = "id";
	private static final String TABLE_CONTACTS_COLUMN_NAME = "name";
	private static final String TABLE_CONTACTS_COLUMN_NUMBER = "number";
	private static final String TABLE_CONTACTS_COLUMN_GROUPID = "groupid";
	private static final String TABLE_CONTACTS_COLUMN_EMAIL_ID = "email_id";

	private static final String TABLE_SETTINGS = "settings";
	private static final String TABLE_SETTINGS_COLUMN_ID = "id";
	private static final String TABLE_SETTINGS_COLUMN_KEY = "key";
	private static final String TABLE_SETTINGS_COLUMN_VALUE = "value";

	/**
	 * Returns the DBHelper singleton instance.
	 *
	 * @param context
	 *            This is used for constructing SQLiteOpenHelper and accessing
	 *            values stored in resources.
	 * @return
	 */
	public static DBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper(context);
		}

		return instance;
	}

	/**
	 * DBHelper is a singleton. Use the static getInstance(context) method.
	 *
	 * @param context
	 */
	private DBHelper(Context context) {
		super(context,
				(storeDBOnSDCard
						? Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_name) + "/"
						: "") + context.getString(R.string.db_name),
				null, Integer.parseInt(context.getString(R.string.db_version)));
		this.context = context;
	}

	@SuppressWarnings("unused")
	@Override
	public void onCreate(SQLiteDatabase db) {
		long now = System.currentTimeMillis();

		if (BuildConfig.DEBUG) {
			Log.d(TAG, "Creating table: " + TABLE_CONTACTS);
		}

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
		db.execSQL("CREATE TABLE " + TABLE_CONTACTS + "(" + TABLE_CONTACTS_COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + TABLE_CONTACTS_COLUMN_NAME + " TEXT, "
				+ TABLE_CONTACTS_COLUMN_NUMBER + " TEXT, " + TABLE_CONTACTS_COLUMN_GROUPID + " INTEGER, "
				+ TABLE_CONTACTS_COLUMN_EMAIL_ID + " TEXT )");

		insertSequence(db, TABLE_CONTACTS, now);

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);
		db.execSQL("CREATE TABLE " + TABLE_SETTINGS + "(" + TABLE_SETTINGS_COLUMN_ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + TABLE_SETTINGS_COLUMN_KEY + " TEXT NOT NULL, "
				+ TABLE_SETTINGS_COLUMN_VALUE + " TEXT)");

		insertSequence(db, TABLE_CONTACTS, now);

		// db.execSQL("INSERT INTO " + TABLE_CONTACTS + "(" +
		// TABLE_CONTACTS_COLUMN_GROUPID +
		// ") VALUES('" + 1 + "')");
		// db.execSQL("INSERT INTO " + TABLE_CONTACTS + "(" +
		// TABLE_CONTACTS_COLUMN_GROUPID +
		// ") VALUES('" + 2 + "')");
		// db.execSQL("INSERT INTO " + TABLE_CONTACTS + "(" +
		// TABLE_CONTACTS_COLUMN_GROUPID + ") VALUES('" + 3 + "')");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	private void insertSequence(SQLiteDatabase db, String tableName, long sequence) {
		db.execSQL("INSERT INTO " + SQLITE_SEQUENCE_TABLE + "(" + TABLE_NAME_COLUMN + ", " + SEQUENCE_COLUMN
				+ ") VALUES('" + tableName + "', " + sequence + ")");
	}

	public void addContact(ContactDto contactDto, Context applicationContext) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext).getReadableDatabase();
		db.execSQL("INSERT INTO " + TABLE_CONTACTS + "(" + TABLE_CONTACTS_COLUMN_NAME + ", "
				+ TABLE_CONTACTS_COLUMN_NUMBER + ", " + TABLE_CONTACTS_COLUMN_GROUPID + ", "
				+ TABLE_CONTACTS_COLUMN_EMAIL_ID + ") VALUES('" + contactDto.getName() + "', '" + contactDto.getNumber()
				+ "', " + contactDto.getGroupid() + ", '" + contactDto.getEmailId() + "' )");

	}

	public void addSetting(SettingDto contactDto, Context applicationContext) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext).getReadableDatabase();
		db.execSQL(
				"INSERT INTO " + TABLE_SETTINGS + "(" + TABLE_SETTINGS_COLUMN_KEY + ", " + TABLE_SETTINGS_COLUMN_VALUE
						+ ") VALUES('" + contactDto.getKey() + "', '" + contactDto.getValue() + "' )");

	}

	public List<ContactDto> getAllContactsByGroupId(Integer id, Context applicationContext) {
		SQLiteDatabase db = DBHelper.getInstance(applicationContext).getReadableDatabase();
		Cursor cursor = null;
		List<ContactDto> jobs = null;

		try {
			cursor = db.query(TABLE_CONTACTS, null, TABLE_CONTACTS_COLUMN_GROUPID + " = '" + id + "' ", null, null,
					null, TABLE_CONTACTS_COLUMN_GROUPID);

			if (cursor.getCount() > 0) {
				jobs = new ArrayList<ContactDto>(cursor.getCount());
			}

			while (cursor.moveToNext()) {
				ContactDto job = new ContactDto();
				job.setId(cursor.getLong(0));
				job.setName(cursor.getString(1));
				job.setNumber(cursor.getString(2));
				job.setGroupid(cursor.getInt(3));
				job.setEmailId(cursor.getString(4));

				jobs.add(job);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return jobs;
	}
}

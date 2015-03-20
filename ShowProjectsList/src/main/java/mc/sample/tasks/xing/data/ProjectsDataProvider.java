package mc.sample.tasks.xing.data;


import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class ProjectsDataProvider extends ContentProvider {
    private static final String LOGTAG = ProjectsDataProvider.class.getSimpleName();

    // Attributes
    public static final String ID = "_id";
    public static final String REPO_NAME = "name";
    public static final String PROJECT_ID = "project_id";
    public static final String DESCRIPTION = "description";
    public static final String OWNER_LOGIN = "owner_login";
    public static final String HTML_URL = "html_url";
    public static final String PAGE = "page";
    public static final String FORK = "fork";
    // DB
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "projects";
    private static final String PATH = DB_NAME;
    private static final String AUTHORITY = "mc.sample.projects.list";
    private static final String CONTACT_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY;
    static final Uri URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);
    // Table
    private static final String PROJECTS_TABLE = "project";
    private static final String DB_CREATE = "create table " + PROJECTS_TABLE + "("
            + ID + " integer primary key autoincrement, "
            + PROJECT_ID + " integer, "
            + REPO_NAME + " text, "
            + DESCRIPTION + " text,"
            + OWNER_LOGIN + " text,"
            + HTML_URL + " text,"
            + PAGE + " integer, "
            + FORK + " integer);";
    // Matcher
    private static final int QUERY_ID1 = 1;
    private static final int QUERY_ID2 = 2;
    private static final UriMatcher URI_MATCHER;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, PATH, QUERY_ID1);
        URI_MATCHER.addURI(AUTHORITY, PATH + "/#", QUERY_ID2);
    }

    private DBHelper mDbHelper;
    private SQLiteDatabase mDb;

    public boolean onCreate() {
        Log.d(LOGTAG, "create");
        mDbHelper = new DBHelper(getContext());
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(LOGTAG, "query: " + uri.toString());
        mDb = mDbHelper.getWritableDatabase();
        switch (URI_MATCHER.match(uri)) {
            case QUERY_ID1:
                return mDb.query(PROJECTS_TABLE, projection, null, null, null, null, sortOrder);
            case QUERY_ID2:
                return mDb.query(PROJECTS_TABLE, projection, "page = ?", new String[]{uri.getLastPathSegment()}, null, null, sortOrder);
        }
        throw new IllegalArgumentException("Wrong URI: " + uri);
    }

    public Uri insert(Uri uri, ContentValues values) {
        Log.d(LOGTAG, "insert: " + uri.toString());
        if (URI_MATCHER.match(uri) != QUERY_ID1) {
            throw new IllegalArgumentException("Wrong URI: " + uri);
        }
        mDb = mDbHelper.getWritableDatabase();
        long rowID = mDb.insert(PROJECTS_TABLE, null, values);
        return ContentUris.withAppendedId(URI, rowID);
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        Log.d(LOGTAG, "delete: " + uri.toString());
//        if (URI_MATCHER.match(uri) == QUERY_ID1) {
//            mDb = mDbHelper.getWritableDatabase();
//            int cnt = mDb.delete(PROJECTS_TABLE, selection, selectionArgs);
//            return cnt;
//        }
//        throw new IllegalArgumentException("Wrong URI: " + uri);
        return 0;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//        Log.d(LOGTAG, "update: " + uri.toString());
//        if (URI_MATCHER.match(uri) == QUERY_ID1) {
//            mDb = mDbHelper.getWritableDatabase();
//            int cnt = mDb.update(PROJECTS_TABLE, values, selection, selectionArgs);
//            return cnt;
//        }
//        throw new IllegalArgumentException("Wrong URI: " + uri);
        return 0;
    }

    public String getType(Uri uri) {
        return CONTACT_CONTENT_TYPE;
    }

    private static class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}
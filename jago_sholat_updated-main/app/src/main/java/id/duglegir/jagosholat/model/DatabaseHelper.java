package id.duglegir.jagosholat.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import id.duglegir.jagosholat.model.DataContract.DataEntry;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE = "jagosholat.db";
    private static final int DATABASE_VERSION = 2;

    // Definisi Tabel User
    public static final String TABLE_USER = "users";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tabel Jadwal Sholat (Lama)
        try {
            String sql_create_table = "CREATE TABLE IF NOT EXISTS " + DataEntry.TABLE_NAME + " (" +
                    DataEntry._ID + " TEXT PRIMARY KEY," +
                    DataEntry.COLUMN_TANGGAL + " TEXT NOT NULL," +
                    DataEntry.COLUMN_SHALAT + " TEXT NOT NULL," +
                    DataEntry.COLUMN_WAKTU + " TEXT NOT NULL," +
                    DataEntry.COLUMN_STATUS + " TEXT NOT NULL);";
            db.execSQL(sql_create_table);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Tabel User (BARU)
        String create_user_table = "CREATE TABLE " + TABLE_USER + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_EMAIL + " TEXT, " +
                COL_PASSWORD + " TEXT)";
        db.execSQL(create_user_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Hapus tabel lama jika versi naik, lalu buat ulang
        db.execSQL("DROP TABLE IF EXISTS " + DataEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    // --- FUNGSI SIMPAN AKUN (REGISTER) ---
    public boolean registerUser(String name, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);

        long result = db.insert(TABLE_USER, null, values);
        return result != -1;
    }

    // --- FUNGSI CEK LOGIN ---
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_ID };
        String selection = COL_EMAIL + " = ? AND " + COL_PASSWORD + " = ?";
        String[] selectionArgs = { email, password };

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    // --- AMBIL NAMA USER ---
    public String getUserName(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{COL_NAME},
                COL_EMAIL + "=?", new String[]{email}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(0);
            cursor.close();
            return name;
        }
        return "Pengguna";
    }

    // --- FUNGSI AMBIL SEMUA DATA USER (NAMA, EMAIL, PASSWORD) ---
    public Cursor getUserData(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        // Query: Pilih semua kolom dari tabel users dimana email = email yang login
        String query = "SELECT * FROM " + TABLE_USER + " WHERE " + COL_EMAIL + " = ?";
        return db.rawQuery(query, new String[]{email});
    }

    public void hapusSemuaData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + DataContract.DataEntry.TABLE_NAME);
        db.close();
    }

}
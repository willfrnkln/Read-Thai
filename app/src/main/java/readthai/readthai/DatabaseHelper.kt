package readthai.readthai

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        /*
        Probably don't need this but save it just in case
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY, " +
                THAI + " TEXT, " +
                ENGLISH + " TEXT, " +
                PART_OF_SPEECH + " TEXT, " +
                ENG_SYN + " TEXT, " +
                THAI_SYN + " TEXT, " +
                THAI_ANT + " TEXT, " +
                EXAMPLE + " TEXT, " +
                THAI_DEF + " TEXT, " +
                COUNTER + " TEXT, " +
                IPA + " TEXT, " +
                ROMANIZATION + " TEXT" + ")")
         */
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }

    companion object {
        private val DATABASE_NAME = "volubilis.db"
        private val DATABASE_VERSION = 1
        val TABLE_NAME = "entry"
        val ID = "id"
        val THAI = "thai"
        val ENGLISH = "english"
        val PART_OF_SPEECH = "partOfSpeech"
        val ENG_SYN =  "englishSynonym"
        val THAI_SYN = "thaiSynonym"
        val THAI_ANT = "thaiAntonym"
        val EXAMPLE = "thaiExampleSentence"
        val THAI_DEF =  "thaiDefinition"
        val COUNTER = "thaiCounter"
        val IPA = "ipa"
        val ROMANIZATION = "romanization"

        private var instance: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            if(this.instance == null) {
                getAndCopyAssetDatabase(context)
                instance = DatabaseHelper(context)
            }
            return instance as DatabaseHelper
        }

        fun doesDatabaseExist(context: Context): Boolean {
            val dbFile = context.getDatabasePath(DATABASE_NAME)
            if(dbFile.exists()) return true
            else if (!dbFile.parentFile.exists()) {
                dbFile.parentFile.mkdirs()
            }
            return false
        }

        fun getAndCopyAssetDatabase(context: Context) {
            if(doesDatabaseExist(context)) return
            context.assets.open(DATABASE_NAME).copyTo(
                FileOutputStream(context.getDatabasePath(DATABASE_NAME)),
                8 * 1024
            )
        }
    }

}
package com.jica.cleaningcuisine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;

//WordDBHelper는 Database 화일(data/data/com.jica.sqlite/databases/alarm.db)
//내부에 테이블(TABLE명 dic)을 생성하는 작업
//Database를 opne/close하는 작업을 손쉽게 할수 있도록 도와준다.
public class AlarmDBHelper extends SQLiteOpenHelper {
    public AlarmDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        //위의 super(context, name, factory, version); 가 실행될때
        //내부적으로 databases폴더내부에 name인자로 전달된 word.db가 없으면
        //폴더및 word.db를 생성해 준다.
        Log.d("TAG", "AlarmDBHelper::AlarmDBHelper(,,,) ...");
    }

    //데이타베이스를 사용하기 위해 최초로 아래의 open메서드들 중 하나를 호출했을때 onCreate()메서드가 단 1번만 동작한다.
    //                             ------------
    //SQLiteDatabase	 getReadableDatabase()  --- SELECT명령만 사용
    //SQLiteDatabase	 getWritableDatabase()  --- INSERT INTO, UPDATE SET, DELETE
    @Override
    public void onCreate(SQLiteDatabase db) {
        //word.db 내부에 테이블을 생성하는 명령어를 실행시킨다.
        //SQLite에서 사용하는 테이블은 일반적인 _id를 관례적으로 가지고 PRIMARY KEY역활을
        //수행하도록 작성한다.
        //테이블을 생성하는 SQL명령 작성
        String sql = "CREATE TABLE alarm(";
        sql +=       "  _id INTEGER PRIMARY KEY AUTOINCREMENT,";
        sql +=       "  name TEXT,";
        sql +=       "  image TEXT,";
        sql +=       "  alarm_info TEXT);";  //"202408061100"

        //위의 표현을 간결하게 코딩하면 아래와 같이 작성할 수 있다.
        //String sql = "CREATE TABLE dic(_id INTEGER PRIMARY KEY AUTOINCREMENT, kor TEXT, eng TEXT);";

        //SQL 명령 실행
        db.execSQL(sql);  //word.db내부에 word테이블이 생성된다.

        Log.d("TAG", "AlarmDBHelper::onCreate(SQLiteDatabse)...:alarmdic테이블이 생성됨");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //테이블의 구조에 변화가 생겼을때 세번째 인자값인 버전번호를 증가시키면 호출된다.
        //여기서 주로 테이블의 구조를 변경하거나 삭제한다.

        Log.d("TAG", "AlarmDBHelper::onUpgrade(SQLiteDatabse,int,int)...:alarm테이블을 삭제하고 새로 생성함");

        //현재 존재하는 테이블을 삭제하자
        String sql ="DROP TABLE IF EXISTS alarm";
        db.execSQL(sql);

        //테이블을 다시 만들자
        onCreate(db);
    }

    //다양한 메서드를 추가하여 DAO처럼 사용할 수도 있다.
    ArrayList<AlarmInfo> queryAll(){
        SQLiteDatabase database = getReadableDatabase();

        //SELECT명령 실행
        Cursor cursor = database.query("alarm",new String[]{"_id","name","image","alarm_info"},null, null, null, null,null, null);

        //SELECT명령의 결과를 저장
        ArrayList<AlarmInfo> items = dicsFromCursor(cursor);

        cursor.close();
        database.close();
        return items;
    }

    ArrayList<AlarmInfo> queryname(String name){
        SQLiteDatabase database = getReadableDatabase();

        //SELECT명령 실행
        Cursor cursor = database.query("alarm",new String[]{"_id","name","image","alarm_info"},"name = ?", new String[]{name}, null, null,null, null);
        //SELECT명령의 결과를 저장
        ArrayList<AlarmInfo> items = dicsFromCursor(cursor);

        cursor.close();
        database.close();
        return items;
    }

    ArrayList<AlarmInfo> queryimage(String image){
        SQLiteDatabase database = getReadableDatabase();

        //SELECT명령 실행
        Cursor cursor = database.query("alarm",new String[]{"_id","name","image","alarm_info"},"image = ?", new String[]{image}, null, null,null, null);
        //SELECT명령의 결과를 저장
        ArrayList<AlarmInfo> items = dicsFromCursor(cursor);

        cursor.close();
        database.close();
        return items;
    }

    ArrayList<AlarmInfo> dicsFromCursor(Cursor cursor){
        ArrayList<AlarmInfo> items  = new ArrayList<AlarmInfo>();

        while( cursor.moveToNext()){ //다음 row위치로 이동
            //1건의 row에서 컬럼값을 추출하여 읽어온다.
            int _id = cursor.getInt(0); //첫번째 컬럼 _id 값을 읽어온다.
            String name = cursor.getString(1);//두번째 컬럼 name 값을 읽어온다.
            String image = cursor.getString(2);//세번째 컬럼 eng 값을 읽어온다.
            String alarm_info = cursor.getString(3); //네번째 컬럼 alarm_info

            AlarmInfo info = new AlarmInfo(name,image, alarm_info);
            items.add(info);
        }
        return items;
    }

    int insert(String name, String image, String alarm_info){
        //데이타베이스 open
        SQLiteDatabase database = getWritableDatabase();

        ContentValues row = new ContentValues();
        row.put("name", name );
        row.put("image", image );
        row.put("alarm_info", alarm_info);

        Log.d("TAG", "insert()메서드로 row를 추가합니다.");
        long rowId = database.insert("alarm", null, row) ;

        database.close();

        //작업결과를 보여주자
        if( rowId != -1) {
            return 1;  //추가성공
        }else{
            return 0;  //추가 실패
        }
    }


    int update(String name, String image){
        //Database open
        SQLiteDatabase database = getWritableDatabase();

        //ContentValues 객체에 변경하고 싶은 컬럼명과 변경값을 저장
        ContentValues row = new ContentValues();
        row.put("image", image);

        Log.d("TAG", "update() 메서드로 row의 컬럼값을 변경 합니다.");

        int n = database.update("alarm", row, "name = ?", new String[]{name} );

        database.close();
        return n ;
    }

    int delete(String name){
        //데이타베이스 open
        SQLiteDatabase database = getWritableDatabase();

        Log.d("TAG", "delete() 메서드로 row를 삭제 합니다.");
        int n = database.delete("alarm", "name = ?", new String[]{name});

        database.close();
        return n;
    }

}

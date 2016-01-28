package com.smarthome.client2.widget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

//import com.smarthome.client2.bean.FamilyBean;

import com.smarthome.client2.bean.MemBean;

import java.util.ArrayList;

public class FamilyMemberDao
{
    DBHelper dbHelper = null;

    SQLiteDatabase db = null;

    /**
     * 数据库创建,通过构造方法
     */
    public FamilyMemberDao(Context context)
    {
        dbHelper = new DBHelper(context);
    }

    /**
     * 添加数据到数据库
     */
    public void insert(MemBean bean)
    {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", bean.memID);
        values.put("headPath", bean.memHeadImgUrl);
        values.put("headName", "null");
        values.put("groupId", bean.memGroupID);

        if (bean.memName.isEmpty())
        {
            values.put("name", bean.memName);
        }
        values.put("address",bean.location);

        db.insert("memberinfo", null, values);
    }

    public void insert(ArrayList<MemBean> list)
    {
        db = dbHelper.getWritableDatabase();
        for (int i = 0; i < list.size(); i++)
        {
            MemBean bean = list.get(i);
            ContentValues values = new ContentValues();
            values.put("id", bean.memID);
            values.put("headPath", bean.memHeadImgUrl);
            values.put("headName", "null");
            values.put("groupId", bean.memGroupID);

            if (bean.memName.isEmpty())
            {
                values.put("name", "null");
            }
            else
            {
                values.put("name",bean.memName);
            }
            values.put("address",bean.location);
            db.insert("memberinfo", null, values);
        }
    }

    /**
     * 清空数据库数据
     */
    public void deleteAll()
    {
        db = dbHelper.getWritableDatabase();
        String sql = "delete from memberinfo";
        db.execSQL(sql);
    }

    public void deleteExceptAccout(String id)
    {
        db = dbHelper.getWritableDatabase();
        String sql = "delete from memberinfo where id!=?";
        db.execSQL(sql, new Object[] { id });
    }

    public ArrayList<MemberInfo> queryAll()
    {
        ArrayList<MemberInfo> list = new ArrayList<MemberInfo>();
        db = dbHelper.getReadableDatabase();
        String sql = "select id,headPath,headName,groupId,name,address from memberinfo";
        Cursor cursor = db.rawQuery(sql, null);
        // 移动头节点查找所有数据
        while (cursor.moveToNext())
        {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String groupId = cursor.getString(cursor.getColumnIndex("groupId"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String headPath = cursor.getString(cursor.getColumnIndex("headPath"));
            String headName = cursor.getString(cursor.getColumnIndex("headName"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            list.add(new MemberInfo(id,groupId,name, headPath,headName,address));
        }
        return list;
    }

    public void close()
    {
        if (db != null)
        {
            db.close();
        }
        if (dbHelper != null)
        {
            dbHelper.close();
        }
    }

    /**
     * 根据id更新头像地址
     */
    public void update(String id, String headPath, String headName)
    {
        db = dbHelper.getWritableDatabase();
        String sql = "update memberinfo set headPath=?,headName=? where id=?";
        db.execSQL(sql, new Object[] { headPath, headName, id });
    }

    public MemberInfo queryById(String id)
    {
        db = dbHelper.getReadableDatabase();
        String sql = "select id,headPath,headName,groupId,name,address from memberinfo where id=?";
        Cursor cursor = db.rawQuery(sql, new String[] { id });
        if (cursor.moveToNext())
        {
            String tmpId = cursor.getString(cursor.getColumnIndex("id"));
            String headPath = cursor.getString(cursor.getColumnIndex("headPath"));
            String headName = cursor.getString(cursor.getColumnIndex("headName"));
            String groupId = cursor.getString(cursor.getColumnIndex("groupId"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            return new MemberInfo(tmpId, groupId, name, headName, headPath,address);
        }
        else
        {
            return null;
        }
    }

    public void deleteById(String id)
    {
        db = dbHelper.getWritableDatabase();
        String sql = "delete from memberinfo where id=?";
        db.execSQL(sql, new Object[] { id });
    }

}

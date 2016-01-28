package com.smarthome.client2.widget;

import java.util.ArrayList;

import com.smarthome.client2.bean.FamilyBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MemberDao
{
    DBHelper dbHelper = null;

    SQLiteDatabase db = null;

    /**
     * 数据库创建,通过构造方法
     */
    public MemberDao(Context context)
    {
        dbHelper = new DBHelper(context);
    }

    /**
     * 添加数据到数据库
     */
    public void insert(FamilyBean bean)
    {
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", bean.mMember_userId);
        values.put("headPath", bean.headpicpath);
        values.put("headName", bean.headpicname);
        values.put("groupId", bean.mGroupId);
        if (bean.alias.isEmpty())
        {
            values.put("name", bean.realname);
        }
        else
        {
            values.put("name", bean.alias);
        }
        db.insert("memberinfo", null, values);
    }

    public void insert(ArrayList<FamilyBean> list)
    {
        db = dbHelper.getWritableDatabase();
        for (int i = 0; i < list.size(); i++)
        {
            FamilyBean bean = list.get(i);
            ContentValues values = new ContentValues();
            values.put("id", bean.mMember_userId);
            values.put("headPath", bean.headpicpath);
            values.put("headName", bean.headpicname);
            values.put("groupId", bean.mGroupId);
            if (bean.alias.isEmpty())
            {
                values.put("name", bean.realname);
            }
            else
            {
                values.put("name", bean.alias);
            }
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
        String sql = "select id,headPath,headName from memberinfo";
        Cursor cursor = db.rawQuery(sql, null);
        // 移动头节点查找所有数据
        while (cursor.moveToNext())
        {
            String id = cursor.getString(cursor.getColumnIndex("id"));
            String headPath = cursor.getString(cursor.getColumnIndex("headPath"));
            String headName = cursor.getString(cursor.getColumnIndex("headName"));
            list.add(new MemberInfo(id, headPath, headName));
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
        String sql = "select id,headPath,headName,groupId,name from memberinfo where id=?";
        Cursor cursor = db.rawQuery(sql, new String[] { id });
        if (cursor.moveToNext())
        {
            String tmpId = cursor.getString(cursor.getColumnIndex("id"));
            String headPath = cursor.getString(cursor.getColumnIndex("headPath"));
            String headName = cursor.getString(cursor.getColumnIndex("headName"));
            String groupId = cursor.getString(cursor.getColumnIndex("groupId"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            return new MemberInfo(tmpId, groupId, name, headName, headPath);
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

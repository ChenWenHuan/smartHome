package com.smarthome.client2.familySchool.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.sourceforge.pinyin4j.PinyinHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.smarthome.client2.familySchool.model.CardAnalysis;
import com.smarthome.client2.familySchool.model.CardLog;
import com.smarthome.client2.familySchool.model.CardOne;
import com.smarthome.client2.familySchool.model.ClassContacts;
import com.smarthome.client2.familySchool.model.Exam;
import com.smarthome.client2.familySchool.model.Homework;
import com.smarthome.client2.familySchool.model.LeaveMessage;
import com.smarthome.client2.familySchool.model.MessageReply;
import com.smarthome.client2.familySchool.model.MsgTarget;
import com.smarthome.client2.familySchool.model.Notice;
import com.smarthome.client2.familySchool.model.ScoreFamily;
import com.smarthome.client2.familySchool.model.ScoreTeacher;
import com.smarthome.client2.familySchool.model.Subject;
import com.smarthome.client2.familySchool.model.Syllabus;
import com.smarthome.client2.familySchool.model.SyllabusItem;

/**
 * @author n003913 网络返回结果处理类
 */
public class ResultParsers
{

    /**
     * 获取返回码
     * @param jsonStr
     * @return
     */
    public static String getCode(String jsonStr)
    {
        JSONObject jObject;
        try
        {
            jObject = new JSONObject(jsonStr);
            return jObject.getString("retcode");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            LogUtil.e("JSONException", "JSONException");
            return null;
        }
    }

    /**
     * 留言返回数据解析
     * @return
     */
    public static ArrayList<LeaveMessage> parserMessage(String jsonStr,
            String childUserId)
    {
        try
        {
            ArrayList<LeaveMessage> list = new ArrayList<LeaveMessage>();
            JSONObject jObject = new JSONObject(jsonStr);
            JSONArray jArray = jObject.getJSONArray("data");
            int length = jArray.length();
            for (int i = 0; i < length; i++)
            {
                jObject = jArray.getJSONObject(i);
                String id = jObject.getString("id");
                String publisher = jObject.getString("teachername");
                String publisherId = jObject.getString("userid");
                String content = jObject.getString("content");
                String time = jObject.getString("date_time");
                if (time != null && time.length() >= 19)
                {
                    time = time.substring(0, 19);
                }
                String headurl = jObject.getString("headpicpath")
                        + jObject.getString("headpicname");
                JSONArray childArray = jObject.getJSONArray("reply");
                ArrayList<MessageReply> replyList = new ArrayList<MessageReply>();
                for (int j = 0; j < childArray.length(); j++)
                {
                    jObject = childArray.getJSONObject(j);
                    String msgId = jObject.getString("message_id");
                    String reply_user_id = jObject.getString("reply_user_id");
                    String itemContent = jObject.getString("content");
                    String replyUsername = jObject.getString("reply_username");
                    String revUserName = jObject.getString("rev_username");
                    String rev_user_id = jObject.getString("rev_user_id");
                    // 家长身份查看留言，把其他家庭的留言回复过滤掉
                    if (childUserId != null)
                    {
                        if (reply_user_id.equals(childUserId)
                                || rev_user_id.equals(childUserId))
                        {
                            replyList.add(new MessageReply(null, msgId,
                                    reply_user_id, rev_user_id, itemContent,
                                    null, replyUsername, revUserName));
                        }
                    }
                    // 老师身份查看留言，留言回复全部保留
                    else
                    {
                        replyList.add(new MessageReply(null, msgId,
                                reply_user_id, rev_user_id, itemContent, null,
                                replyUsername, revUserName));
                    }
                }
                list.add(new LeaveMessage(id, headurl, publisher, publisherId,
                        time, content, replyList));
            }
            return list;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            LogUtil.e("JSONException", "JSONException");
            return null;
        }
    }

    /**
     * 获取课程表数据解析（教师、家长）
     * @param jsonStr
     * @return
     */
    public static Syllabus parserSyllabus(String jsonStr)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            JSONArray array = object.getJSONArray("data");
            if (array.length() == 0)
            {
                return new Syllabus(-1, 0, 0, 0, 0, "0", null);
            }
            object = array.getJSONObject(0);// array长度就是1，因为请求参数loadsize=1.
            String date = object.getString("create_time");
            date = date.substring(0, 10);
            long id = object.getLong("id");
            JSONArray jArray = object.getJSONArray("weeks");
            int length = jArray.length();
            int[] periodMax = new int[] { 0, 0, 0, 0 };
            ArrayList<SyllabusItem> list = new ArrayList<SyllabusItem>();
            for (int i = 0; i < length; i++)
            {
                object = jArray.getJSONObject(i);
                int weekday = Integer.parseInt(object.getString("weekday")) + 1;
                int sort = Integer.parseInt(object.getString("sortnum")) + 1;
                String subjectname = object.getString("subject_name");
                int type = Integer.parseInt(object.getString("type"));
                if (sort > periodMax[type])
                {
                    periodMax[type] = sort;
                }
                list.add(new SyllabusItem(weekday, sort, subjectname, type));
            }
            int beforeAm = periodMax[0];
            int am = Math.max(periodMax[1] - beforeAm, 0);
            int pm = Math.max(periodMax[2] - beforeAm - am, 0);
            int afterPm = Math.max(periodMax[3] - beforeAm - am - pm, 0);
            return new Syllabus(id, am, pm, beforeAm, afterPm, date, list);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            LogUtil.e("JSONException", "JSONException");
            return null;
        }
    }

    /**
     * 考勤数据解析（家长）
     * @param jsonStr
     * @return
     */
    public static ArrayList<CardLog> parserAttendanceFamily(String jsonStr)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            JSONArray array = object.getJSONArray("data");
            int length = array.length();
            ArrayList<CardLog> list = new ArrayList<CardLog>();
            for (int i = 0; i < length; i++)
            {
                object = array.getJSONObject(i);
                String time = object.getString("start_time").substring(10, 19);
                String opt_type = object.getString("opt_type");
                list.add(new CardLog(time, opt_type));
            }
            return list;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 考勤数据解析（教师）
     * @param jsonStr
     * @return
     */
    public static ArrayList<CardAnalysis> parserAttendanceTeacher(String jsonStr)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            JSONArray array = object.getJSONArray("data");
            int length = array.length();
            ArrayList<CardAnalysis> list = new ArrayList<CardAnalysis>();
            for (int i = 0; i < length; i++)
            {
                object = array.getJSONObject(i);
                String name = object.getString("student_name");
                long id = object.getLong("id");
                JSONArray childArray;
                ArrayList<CardOne> childList = new ArrayList<CardOne>();
                CardOne cardOne;
                if (object.has("times"))
                {
                    childArray = object.getJSONArray("times");
                    int childLength = childArray.length();
                    for (int j = 0; j < childLength; j++)
                    {
                        object = childArray.getJSONObject(j);
                        String time = object.getString("card_time")
                                .substring(0, 5);
                        String state = object.getString("status");
                        cardOne = new CardOne(time, state, null);
                        childList.add(cardOne);
                    }
                }
                list.add(new CardAnalysis(id, name, childList));
            }
            return list;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 考勤概况解析
     * @param jsonStr
     * @return
     */
    public static int[] parserAttendanceAnalyse(String jsonStr)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            object = object.getJSONObject("data");
            int[] array = new int[] { 0, 0, 0 };
            array[0] = object.getInt("late");
            array[1] = object.getInt("absent");
            array[2] = object.getInt("early");
            return array;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 班级圈通讯录解析,提取首字母分组排序（#、A、B、……Z）。 构造一个首字母initial实体类（无意义的联系人实体类）new
     * ClassContacts(null, inital)，添加到map和list中。
     * 通过map中的key值（initial）获取实体类，在list中indexOf获取插入索引。
     * @param jsonStr
     * @return
     */
    public static ArrayList<ClassContacts> parserContacts(String jsonStr)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            JSONArray array = object.getJSONArray("data");
            int length = array.length();
            ArrayList<ClassContacts> list = new ArrayList<ClassContacts>();
            HashMap<String, ClassContacts> map = new HashMap<String, ClassContacts>();// 存储list中首字母对应的实体类
            for (int i = 0; i < length; i++)
            {
                object = array.getJSONObject(i);
                String name = "";
                String note = "";
                if (!object.getString("child_name").isEmpty())
                {
                    name = object.getString("child_name") + "的家长";
                    note = "(" + object.getString("userrealname") + ")";
                }
                else
                {
                    name = object.getString("userrealname");
                    note = "(" + object.getString("subject_name") + "老师)";
                }
                String phone = object.getString("telnum");
                char ch = name.charAt(0);
                String inital = "";
                ClassContacts model = null;
                int index = -1;
                // 获取首字母（大写字母和#）
                if (ch >= 128)
                {
                    ch = PinyinHelper.toHanyuPinyinStringArray(ch)[0].charAt(0);
                    inital = String.valueOf((char) (ch - 32));
                }
                else if (ch >= 65 && ch <= 90)
                {
                    inital = String.valueOf(ch);
                }
                else if (ch >= 97 && ch <= 122)
                {
                    inital = String.valueOf((char) (ch - 32));
                }
                else
                {
                    inital = "#";
                }
                // map中已经有了，则获取在list中的索引，加1插入
                if (map.containsKey(inital))
                {
                    index = list.indexOf(map.get(inital));
                    list.add(index + 1, new ClassContacts(name, phone, note));
                }
                // map中不存在,map中要添加，list中要在合适位置插入首字母实体类和当前的联系人实体类
                else
                {
                    model = new ClassContacts(null, inital, null);
                    // #放在最前
                    if (inital.equals("#"))
                    {
                        map.put(inital, model);
                        list.add(0, model);
                        list.add(1, new ClassContacts(name, phone, note));
                    }
                    // 遍历map的key值，获取比initial大的最小字母
                    else
                    {
                        String more = "a";
                        String temp = null;
                        for (Entry<String, ClassContacts> entry : map.entrySet())
                        {
                            temp = entry.getKey();
                            if (temp.compareTo(inital) > 0
                                    && temp.compareTo(more) < 0)
                            {
                                more = temp;
                            }
                        }
                        map.put(inital, model);
                        // initial比map的所有key值都大，那么在list最后插入
                        if (more.equals("a"))
                        {
                            list.add(model);
                            list.add(new ClassContacts(name, phone, note));
                        }
                        // 在比initial大的最小字母对应的list中的位置插入
                        else
                        {
                            index = list.indexOf(map.get(more));
                            list.add(index, model);
                            list.add(index + 1, new ClassContacts(name, phone,
                                    note));
                        }
                    }
                }
            }
            return list;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 留言对象解析,提取首字母分组排序（#、A、B、……Z）。 构造一个首字母initial实体类（无意义的联系人实体类）new
     * MsgTarget("-1", inital)，添加到map和newList中。
     * 通过map中的key值（initial）获取实体类，在newList中indexOf获取插入索引。
     * 第二层排序，id升序
     * @param jsonStr
     * @return
     */
    public static ArrayList<MsgTarget> parserTargets(String jsonStr)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            JSONArray array = object.getJSONArray("data");
            int length = array.length();
            ArrayList<MsgTarget> list = new ArrayList<MsgTarget>();
            if (length == 0)
            {
                return list;
            }
            // 得到未分组按照id降序列表
            for (int i = 0; i < length; i++)
            {
                object = array.getJSONObject(i);
                String name = object.getString("student_name");
                String id = object.getString("id");
                String userId = object.getString("user_id");
                list.add(0, new MsgTarget(id, name, userId));
            }
            ArrayList<MsgTarget> newList = new ArrayList<MsgTarget>();
            HashMap<String, MsgTarget> map = new HashMap<String, MsgTarget>();// 存储newList中首字母对应的实体类
            MsgTarget modelTrue = null;
            MsgTarget modelFalse = null;
            for (int i = 0; i < length; i++)
            {
                modelTrue = list.get(i);
                char ch = modelTrue.getName().charAt(0);
                String inital = "";
                int index = -1;
                // 获取首字母（大写字母和#）
                if (ch >= 128)
                {
                    ch = PinyinHelper.toHanyuPinyinStringArray(ch)[0].charAt(0);
                    inital = String.valueOf((char) (ch - 32));
                }
                else if (ch >= 65 && ch <= 90)
                {
                    inital = String.valueOf(ch);
                }
                else if (ch >= 97 && ch <= 122)
                {
                    inital = String.valueOf((char) (ch - 32));
                }
                else
                {
                    inital = "#";
                }
                // map中已经有了，则获取在newList中的索引，加1插入
                if (map.containsKey(inital))
                {
                    index = newList.indexOf(map.get(inital));
                    newList.add(index + 1, modelTrue);
                }
                // map中不存在,map中要添加，list中要在合适位置插入首字母实体类和当前的联系人实体类
                else
                {
                    modelFalse = new MsgTarget("-1", inital);
                    // #放在最前
                    if (inital.equals("#"))
                    {
                        map.put(inital, modelFalse);
                        newList.add(0, modelFalse);
                        newList.add(1, modelTrue);
                    }
                    // 遍历map的key值，获取比initial大的最小字母
                    else
                    {
                        String more = "a";
                        String temp = null;
                        for (Entry<String, MsgTarget> entry : map.entrySet())
                        {
                            temp = entry.getKey();
                            if (temp.compareTo(inital) > 0
                                    && temp.compareTo(more) < 0)
                            {
                                more = temp;
                            }
                        }
                        map.put(inital, modelFalse);
                        // initial比map的所有key值都大，那么在list最后插入
                        if (more.equals("a"))
                        {
                            newList.add(modelFalse);
                            newList.add(modelTrue);
                        }
                        // 在比initial大的最小字母对应的list中的位置插入
                        else
                        {
                            index = newList.indexOf(map.get(more));
                            newList.add(index, modelFalse);
                            newList.add(index + 1, modelTrue);
                        }
                    }
                }
            }
            return newList;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析家庭作业（教师、家长）
     * @param jsonStr
     * @return
     */
    public static ArrayList<Homework> parserHomework(String jsonStr)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            JSONArray array = object.getJSONArray("data");
            int length = array.length();
            ArrayList<Homework> list = new ArrayList<Homework>();
            for (int i = 0; i < length; i++)
            {
                object = array.getJSONObject(i);
                long id = object.getLong("id");
                String publisher = object.getString("teacher_name");
                String headUrl = object.getString("headpicpath")
                        + object.getString("headpicname");
                String date = object.getString("date_time");
                if (date.length() >= 10)
                {
                    date = date.substring(0, 10);
                }
                String subject = object.getString("subject_name");
                String content = object.getString("content");
                list.add(new Homework(id, publisher, headUrl, date, subject,
                        content));
            }
            return list;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析课程
     * @param jsonStr
     * @return
     */
    public static ArrayList<Subject> parserSubjects(String jsonStr)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            JSONArray array = object.getJSONArray("data");
            int length = array.length();
            ArrayList<Subject> list = new ArrayList<Subject>();
            for (int i = 0; i < length; i++)
            {
                object = array.getJSONObject(i);
                String id = object.getString("subject_id");
                String name = object.getString("subject_name");
                list.add(new Subject(id, name));
            }
            return list;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析成绩信息（家长）
     * @param jsonStr
     * @return
     */
    public static ArrayList<ScoreFamily> parserScoreFamily(String jsonStr)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            JSONArray array = object.getJSONArray("data");
            int length = array.length();
            ArrayList<ScoreFamily> list = new ArrayList<ScoreFamily>();
            for (int i = 0; i < length; i++)
            {
                object = array.getJSONObject(i);
                long id = object.getLong("exam_id");
                String publisher = object.getString("teacher_name");
                String headUrl = object.getString("headpicpath")
                        + object.getString("headpicname");
                String date = object.getString("exam_time");
                if (date.length() >= 10)
                {
                    date = date.substring(0, 10);
                }
                String examName = object.getString("exam_name");
                String subject = object.getString("subject_name");
                String score = object.getString("score");
                String rank = object.getString("sort");
                boolean isScore = object.getString("type").equals("0");
                list.add(new ScoreFamily(id, publisher, headUrl, date,
                        examName, subject, score, rank, isScore));
            }
            return list;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析考试列表（老师）
     * @param jsonStr
     * @return
     */
    public static ArrayList<Exam> parserExamTeacher(String jsonStr)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            JSONArray array = object.getJSONArray("data");
            int length = array.length();
            ArrayList<Exam> list = new ArrayList<Exam>();
            for (int i = 0; i < length; i++)
            {
                object = array.getJSONObject(i);
                long id = object.getLong("id");
                String examName = object.getString("name");
                String subject = object.getString("subjectname");
                int type = object.getInt("type");
                String date = object.getString("exam_time");
                if (date.length() >= 10)
                {
                    date = date.substring(0, 10);
                }
                list.add(new Exam(id, examName, subject, date, type));
            }
            return list;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析成绩信息（老师）
     */
    public static ArrayList<ScoreTeacher> parserScoreTeacher(String jsonStr,
            boolean isScore)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            JSONArray array = object.getJSONArray("data");
            int length = array.length();
            ArrayList<ScoreTeacher> list = new ArrayList<ScoreTeacher>();
            for (int i = 0; i < length; i++)
            {
                object = array.getJSONObject(i);
                String name = object.getString("name");
                String score = object.getString("score");
                String rank = "";
                if (isScore)
                {
                    rank = object.getString("sort");
                }
                list.add(new ScoreTeacher(name, score, rank));
            }
            return list;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析通知公告（老师、家长）
     */
    public static ArrayList<Notice> parserNotice(String jsonStr)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            JSONArray array = object.getJSONArray("data");
            int length = array.length();
            ArrayList<Notice> list = new ArrayList<Notice>();
            for (int i = 0; i < length; i++)
            {
                object = array.getJSONObject(i);
                long id = object.getLong("id");
                String headUrl = object.getString("headpicpath")
                        + object.getString("headpicname");
                String publisher = object.getString("userrealname");
                String target = "通知" + object.getString("incept_desc");
                String date = object.getString("dateStr");
                String text = object.getString("content");
                String picUrl = object.getString("file_path")
                        + object.getString("file_name");
                list.add(new Notice(id, headUrl, publisher, target, date, text,
                        picUrl));
            }
            return list;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析学生列表，用于发布成绩（教师）
     * @param jsonStr
     * @return
     */
    public static ArrayList<MsgTarget> parserStudents(String jsonStr)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            JSONArray array = object.getJSONArray("data");
            int length = array.length();
            ArrayList<MsgTarget> list = new ArrayList<MsgTarget>();
            for (int i = 0; i < length; i++)
            {
                object = array.getJSONObject(i);
                String id = object.getString("id");
                String name = object.getString("student_name");
                String userId = object.getString("user_id");
                list.add(new MsgTarget(id, name, userId));
            }
            return list;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解析学生列表，用于教师查看一条留言的对象
     * @param jsonStr
     * @return
     */
    public static ArrayList<MsgTarget> parserLookTargets(String jsonStr)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            JSONArray array = object.getJSONArray("data");
            int length = array.length();
            ArrayList<MsgTarget> list = new ArrayList<MsgTarget>();
            for (int i = 0; i < length; i++)
            {
                object = array.getJSONObject(i);
                String name = object.getString("userrealname");
                String headUrl = object.getString("headpicpath")
                        + object.getString("headpicname");
                MsgTarget target = new MsgTarget();
                target.setNameHead(name, headUrl);
                list.add(target);
            }
            return list;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String getData(String jsonStr)
    {
        try
        {
            JSONObject object = new JSONObject(jsonStr);
            return object.getString("data");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return null;
        }
    }

}

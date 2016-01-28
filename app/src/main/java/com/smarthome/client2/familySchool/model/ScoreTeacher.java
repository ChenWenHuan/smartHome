package com.smarthome.client2.familySchool.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author n003913
 * 一条成绩信息（教师）
 */
public class ScoreTeacher
{

    private String name;

    private String score;

    /**
     * 名次
     */
    private String rank;

    public ScoreTeacher(String name, String score, String rank)
    {
        this.name = name;
        this.score = score;
        this.rank = rank;
    }

    public String getName()
    {
        return name;
    }

    public String getScore()
    {
        return score;
    }

    public String getRank()
    {
        return rank;
    }

    /**
     * 升序排列
     */
    public static void orderAsc(ArrayList<ScoreTeacher> list)
    {
        Comparator<ScoreTeacher> comparator = new Comparator<ScoreTeacher>()
        {
            @Override
            public int compare(ScoreTeacher lhs, ScoreTeacher rhs)
            {
                int result = Integer.parseInt(lhs.getRank())
                        - Integer.parseInt(rhs.getRank());
                return result;
            }
        };
        Collections.sort(list, comparator);
    }

}

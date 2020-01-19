package com.smanager.utils;

public class WorkControllerPaths {

    public static final String INDEX = "/Index";
    public static final String MENU = "/Menu";
    public static final String TEACHER_WORK = "/TeacherWork";
    public static final String NO_GRADES_REPORT = "/NoGradeReport";
    public static final String NO_COMMENT_REPORT = "/NoCommentReport";
    public static final String GRADES_REPORT = "/GradesReport";
    public static final String TEACHER_GRADES_REPORT = "/TeacherGradesReport";

    private static final String CONTROLLER_PREFIX = "/Work";
    private static final String REDIRECT_PREFIX = "redirect:";

    public static String getRedirectPath(String workControllerPath) {
        return REDIRECT_PREFIX + CONTROLLER_PREFIX + workControllerPath;
    }

    public static String getUrl(String workControllerPath) {
        return CONTROLLER_PREFIX + workControllerPath;
    }
}

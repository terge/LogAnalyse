package me.eg.loganalyse;

import java.util.HashMap;

public class Log {
    public String t;
    public String game_id;
    public String game_name;
    public String ucid;
    public String si;
    public String ve;
    public String fr;
    public String model;
    public String imei;
    public String level;
    public String content;
    public String claz;
    public String method;
    public String ctime;
    public String log_from;
    public String sys_ver;
    public int count = 1;

    private final static String T = "t";
    private final static String GAME_ID = "game_id";
    private final static String GAME_NAME = "game_name";
    private final static String UCID = "ucid";
    private final static String SI = "si";
    private final static String VE = "ve";
    private final static String FR = "fr";
    private final static String MODEL = "model";
    private final static String IMEI = "imei";
    private final static String NET = "net";
    private final static String LEVEL = "level";
    private final static String CONTENT = "content";
    private final static String CLAZ = "claz";
    private final static String METHOD = "method";
    private final static String CTIME = "ctime";
    private final static String LOG_FROM = "log_from";
    private final static String SYS_VER = "sys_ver";


    public static Log gen(String str) {
        String[] ele = str.split(",");
        Log log = new Log();
        log.t = ele[getIndex(T)];
        log.game_id = ele[getIndex(GAME_ID)];
        log.game_name = ele[getIndex(GAME_NAME)];
        log.ucid = ele[getIndex(UCID)];
        log.si = ele[getIndex(SI)];
        log.ve = ele[getIndex(VE)];
        log.fr = ele[getIndex(FR)];
        log.model = ele[getIndex(MODEL)];
        log.imei = ele[getIndex(IMEI)];
        log.level = ele[getIndex(LEVEL)];
        log.content = ele[getIndex(CONTENT)];
        log.claz = ele[getIndex(CLAZ)];
        log.method = ele[getIndex(METHOD)];
        log.ctime = ele[getIndex(CTIME)];
        log.log_from = ele[getIndex(LOG_FROM)];
        log.sys_ver = ele[getIndex(SYS_VER)];
        return log;
    }

    private final static HashMap<String, Integer> indexMap = new HashMap<String, Integer>();

    public static void initIndex(String line) {
        indexMap.clear();
        String[] strs = line.split(",");
        for (int i = 0; i < strs.length; i++) {
            indexMap.put(strs[i], i);
        }
    }

    private static int getIndex(String key) {
        Integer index = indexMap.get(key);
        return index == null ? 0 : index;
    }


}

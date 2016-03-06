package me.eg.loganalyse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Analyse {

    public static final String DIR = "E:\\ErrorLog";
    private final static List<String> keywordList = new ArrayList<String>();
    private static int totalSi;
    static File curFile;
    public static final String LIB_VERSION = "lib_4.0";

    public static void main(String[] args) {

        File[] files = new File(DIR).listFiles();
        for (File file : files) {
            if (file.isFile()) {
                System.out.println("====================");
                System.out.println("" + file.getName());
                readLogFile(file);
                initKeyword();
                split();
                calcSiPercent();
                //                out("all", logList, file);
            }
        }
        //        initKeyword();

        //        split();
        
        //        contentSiCount();
    }

    private static void initKeyword() {
        /**
         * keywrod 特殊排在前面，避免将错误先放放通用list里面去了
         */
        keywordList.clear();
        keywordList.add("View not attached to window manager");
        keywordList.add("cn.uc.gamesdk.lib.a.i.g");//前后台切换空指针
        keywordList.add(" TimerTask is canceled");
        keywordList.add("Receiver not registered");
        keywordList.add("uc.bubble.update.action");
        keywordList.add("android.intent.action.DOWNLOAD_COMPLETE");
        keywordList.add("Can't create handler inside thread");
        keywordList.add("Toast");
        keywordList.add("pthread_create");
        keywordList.add("Unable to create directory");
        keywordList.add("is your activity running");
        keywordList.add("Unable to start activity ComponentInfo");
        keywordList.add("has already been added");

        keywordList.add("cn.uc.gamesdk.core.i.b.b");//资源更新空指针
        keywordList.add("core.view.widget.config.ThirdPartyInfo");
        keywordList.add("android.database.sqlite");
        keywordList.add("Destination must be on external storage");
        keywordList.add("cn.uc.gamesdk.core.d.b$1.run");//获取验证码空指针
        keywordList.add("cn.uc.gamesdk.core.view.widget.loginwidget");//登录空指针异常


    }
    private static List<Log> logList = new ArrayList<Log>();

    public static void readLogFile(File file) {
        totalSi = 0;
        curFile = file;
        //        String filePath = "C:\\Users\\Administrator\\Desktop\\bhxy_4.0.0.2.csv";
        //        File file = new File(filePath);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.length() < 200) {
                    Log.initIndex(line);
                    continue;
                }
                Log log = Log.gen(line);
                //过滤掉不属于此版本的日志
                if (log != null && log.log_from != null && log.log_from.startsWith(LIB_VERSION)) {
                    logList.add(log);
                } else {
                    //                    System.out.println("line:" + line);
                }
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void out(String keyword, List<Log> logList) {
        HashMap<String, Log> siMap = new HashMap<String, Log>();
        System.out.println("keyword:" + keyword);

        System.out.println("logList.size()" + logList.size() + " siMap.size():" + siMap.size());
        for(Log log:logList){
            if (siMap.containsKey(log.si)) {
                siMap.get(log.si).count++;
            } else {
                siMap.put(log.si, log);
            }
        }
        
        totalSi += siMap.size();
        FileWriter fw = getOutFileWriter();
        try {
            fw.append("keyword:" + keyword);
            fw.append("影响si:" + siMap.keySet().size());
            fw.append("\r\n");
            List<Log> valueList = Map2List(siMap);
            sort(valueList);
            for (Log log : valueList) {
                fw.append("" + log.count);
                fw.append(" : ");
                fw.append(log.si);
                fw.append(" : ");
                fw.append(log.content);
                fw.append("\r\n");
            }

            fw.append("\r\n");
            fw.append("\r\n");
            fw.append("\r\n");
            
            fw.flush();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static FileWriter getOutFileWriter() {
        FileWriter fw = null;
        File outFile = new File(curFile.getAbsolutePath() + "_out");
        try {
            if (!outFile.exists()) outFile.createNewFile();
            fw = new FileWriter(outFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fw;
    }

    public static void contentSiCount(String keyWord) {
        Set<String> siSet = new HashSet<String>();
        for (Log log : logList) {
            if (log.content.contains(keyWord)) siSet.add(log.si);
        }
        System.out.println("key_word:" + keyWord + " si count:" + siSet.size());
    }

    public static void split() {
        HashMap<String, List<Log>> keywordLogMap = new HashMap<String, List<Log>>();
        for (String keyword : keywordList) {
            keywordLogMap.put(keyword, new ArrayList<Log>());
        }
        ArrayList<Log> otherList = new ArrayList<Log>();
        boolean isKeyword = false;
        Log log = null;
        while(logList.size() > 0){
            isKeyword = false;
            log = logList.get(0);
            for (String keyword : keywordList) {
                if (log.content.contains(keyword)) {
                    keywordLogMap.get(keyword).add(log);
                    isKeyword = true;
                    break;
                }
            }
            if (!isKeyword) otherList.add(log);
            logList.remove(log);
        }

        for (String keyword : keywordList) {
            out(keyword, keywordLogMap.get(keyword));
        }
        out("Rest", otherList);
    }

    private static void calcSiPercent() {
        try {
            File siFile = new File(DIR, "si_count");
            if (!siFile.exists()) siFile.createNewFile();

            FileWriter fw = new FileWriter(siFile, true);
            fw.append(curFile.getName());
            fw.append("\r\n");
            fw.append("totalSi:" + totalSi);
            fw.append("\r\n");

            File outFile = new File(DIR, curFile.getName() + "_out");
            FileReader fr = new FileReader(outFile);
            BufferedReader bf = new BufferedReader(fr);
            String line;
            String siCount;
            while ((line = bf.readLine()) != null) {
                if (line.contains("si:")) {
                    siCount = line.substring(line.indexOf("si:") + "si:".length());
                    fw.append("" + (Integer.parseInt(siCount) * 10000 / totalSi));
                    fw.append("\r\n");
                }
            }
            fw.append("\r\n");
            fw.append("\r\n");
            fw.append("\r\n");
            bf.close();
            fr.close();
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sort(List<Log> list) {
        Collections.sort(list, new Comparator<Log>() {

            @Override
            public int compare(Log o1, Log o2) {
                return o1.content.length() - o2.content.length();
            }
        });

    }
    
    private static List<Log> Map2List(HashMap<String, Log> logMap) {
        List<Log> list = new ArrayList<Log>();
        Collection<Log> value = logMap.values();
        for (Log log : value) {
            list.add(log);
        }
        return list;
    }
}

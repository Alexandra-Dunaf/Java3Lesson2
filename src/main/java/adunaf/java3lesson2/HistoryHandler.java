package adunaf.java3lesson2;

import java.io.*;
import java.util.LinkedList;


public class HistoryHandler {
    private static BufferedWriter bufferedWriter = null;
    private static boolean isOpened  = false;
    private static String login;
    private static int maxSize = 5;
    public static void setLogin(String argLogin){
        login = argLogin;
    };

    public static void closeWriting(){
        if (isOpened){
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                isOpened = false;
            }
        }
    }

    public static void appendText(String s) {
        if (!isOpened) {
            try {
                bufferedWriter = new BufferedWriter(new FileWriter("history_" + login + ".txt",true));
                isOpened = true;
            } catch (IOException e) {
                isOpened = false;
            }
        }
        if (isOpened) {
            try {
                bufferedWriter.write(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readLastMessages() {
        String result = "";
        File file = new File("history_" + login + ".txt");
        if (file.exists()){
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                LinkedList<String> list = new LinkedList<>();
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    list.addLast(str+"\n");
                    if (list.size()>maxSize){
                        list.removeFirst();
                    }
                }
                for (String s:list
                ) {
                    result = result+s;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}

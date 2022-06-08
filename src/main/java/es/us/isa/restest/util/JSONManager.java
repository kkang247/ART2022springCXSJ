package es.us.isa.restest.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import es.us.isa.restest.testcases.TestCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.JsonArray;
import javax.json.JsonObject;

import static es.us.isa.restest.util.FileManager.readFile;

public class JSONManager {

    private static final Logger logger = LogManager.getLogger(JSONManager.class.getName());

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static List<Object> readMultipleJSONs(List<String> jsonPaths) {
        List<Object> values = new ArrayList<Object>();
        Object jsonData;

        // For each path, read JSON file
        for (String jsonPath: jsonPaths) {
            jsonData = readJSON(jsonPath);
            if (jsonData != null)
                values.add(jsonData);
        }

        return values;
    }

    public static Object readJSON(String jsonPath) {
        JsonNode jsonData = null;
        try {
            jsonData = objectMapper.readTree(new File(jsonPath));
        } catch (IOException ex) {
            logger.error("Error parsing JSON file: {}", jsonPath);
            logger.error("Exception: ", ex);
        }
        return jsonData;
    }

    public static Object readJSONFromString(String json) {
        JsonNode jsonData = null;
        try {
            jsonData = objectMapper.readTree(json);
        } catch (IOException ex) {
            logger.error("Error parsing JSON String: \n {}", json);
            logger.error("Exception: ", ex);
        }
        return jsonData;
    }

    public static String getStringFromJSON(JsonNode node) {
        String json = null;
        try {
            json = objectMapper.writeValueAsString(node);
        } catch (JsonProcessingException ex) {
            logger.error("Error parsing JSON: {}", node);
            logger.error("Exception: ", ex);
        }
        return json;
    }

    public static String saveAsJson(Collection<TestCase> testCases){
        String jsonString = JSON.toJSONString(testCases);
        System.out.println(jsonString);
        String filePath = "";

//        try {
//            // 保证创建一个新文件
//            File file = new File(filePath);
//            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
//                file.getParentFile().mkdirs();
//            }
//            if (file.exists()) { // 如果已存在,删除旧文件
//                file.delete();
//            }
//            file.createNewFile();
//            // 将格式化后的字符串写入文件
//            Writer write = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
//            write.write(jsonString);
//            write.flush();
//            write.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return jsonString;
    }

    public static List<TestCase> readTestCase(String json){
        List<TestCase> cases = JSON.parseArray(json, TestCase.class);
//        for (TestCase tc: cases) {
//            System.out.println(tc.getId());
//        }
        return cases;
    }

    public static HashMap<String, List<String>> readCoverage() throws IOException {
        HashMap<String, List<String>> result = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader("F:\\创新实践\\ART2022springCXSJ_final\\src\\main\\covered_info.txt"));
        String s;
//        List<String> covList = new ArrayList<>();
        String key;
        while((s = br.readLine())!=null){
            String[] text;
            text = s.split(":");
            String id = text[0];
            key = id;
            String[] cases = text[1].split("\\.");
            List<String> covList = new ArrayList<>();
            for (int i = 0; i <cases.length ; i++) {
                covList.add(cases[i]);
            }
            result.put(key, covList); // put id and coverage
//            int class_number = Integer.parseInt(text[0]);
//            int method_num = Integer.parseInt(text[1]);
//            int method_line = Integer.parseInt(text[2]);
//            int line_covered = Integer.parseInt(text[3]);
        }
//        appendToFile(content);
        return result;
    }
}
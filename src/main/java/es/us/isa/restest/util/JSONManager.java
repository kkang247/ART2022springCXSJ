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
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
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

    public static void saveAsJson(Collection<TestCase> testCases){
        String jsonString = JSON.toJSONString(testCases);
        System.out.println(jsonString);
        String filePath = "";

        try {
            // 保证创建一个新文件
            File file = new File(filePath);
            if (!file.getParentFile().exists()) { // 如果父目录不存在，创建父目录
                file.getParentFile().mkdirs();
            }
            if (file.exists()) { // 如果已存在,删除旧文件
                file.delete();
            }
            file.createNewFile();
            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            write.write(jsonString);
            write.flush();
            write.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<TestCase> readTestCase() {
        String jsonPath = "";
        BufferedReader bufferedReader;
        StringBuilder result = new StringBuilder();
        try {
            bufferedReader = new BufferedReader(new FileReader(new File(jsonPath)));
            String temp;
            while ((temp = bufferedReader.readLine()) != null){
                result.append(temp);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return JSON.parseArray(result.toString(), TestCase.class);
    }

    public static HashMap<String, List<String>> readCoverage(){
        HashMap<String, List<String>> result = new HashMap<>();
        List<String> covList = new ArrayList<>();

        covList.add("1-1-1000");
        covList.add("2-2-0001");
        result.put("TEST111", covList); // put id and coverage
        return result;
    }
}

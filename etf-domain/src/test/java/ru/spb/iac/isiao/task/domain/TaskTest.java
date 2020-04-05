package ru.spb.iac.isiao.task.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericContainer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

@RunWith(BlockJUnit4ClassRunner.class)
public class TaskTest {
    private static final String JSON_FOLDER = "json/";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void createSchemaObject() throws Exception {
        FileUtils.forceMkdir(new File(JSON_FOLDER));

        createSchemaFile(new File(getClass().getClassLoader().getResource("avro").getFile()));
    }

    private void createSchemaFile(File file) throws Exception {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f : files) {
                    createSchemaFile(f);
                }
            }
        } else {
            JsonNode jsonNode = objectMapper.readTree(file);
            String packageName = jsonNode.get("namespace").asText();
            String className = jsonNode.get("name").asText();

            Class<?> clazz = Class.forName(packageName + "." + className);
            Field field = clazz.getField("SCHEMA$");
            Schema schema = (Schema) field.get(null);
            String schemaStr = schema.toString();

            SchemaObject schemaObject = new SchemaObject(schemaStr);
            String json = objectMapper.writeValueAsString(schemaObject);

            IOUtils.write(json, new FileOutputStream(JSON_FOLDER + className + ".json"), StandardCharsets.UTF_8);
        }
    }
}

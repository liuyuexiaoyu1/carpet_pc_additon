package com.pigconnon.carpet_pc_addition;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NicknameStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger("CPCA");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final File configDir;
    private final File dataFile;

    public NicknameStorage() {
        // 直接指定正确的路径
        File runDir = new File(".").getAbsoluteFile(); // 获取当前工作目录的绝对路径
        this.configDir = new File(runDir, "config/carpet_pig_connon_addition");
        this.dataFile = new File(configDir, "nicknames.json");

        // 确保目录存在
        ensureDirectoryExists();
    }

    /**
     * 确保配置目录存在
     */
    private void ensureDirectoryExists() {
        if (!configDir.exists()) {
            if (configDir.mkdirs()) {
                LOGGER.info("创建配置目录: {}", configDir.getAbsolutePath());
            } else {
                LOGGER.error("无法创建配置目录: {}", configDir.getAbsolutePath());
            }
        }
    }

    /**
     * 保存所有昵称数据
     */
    public void saveData(Map<UUID, PlayerNickname> nicknames) {
        try {
            // 确保目录存在
            ensureDirectoryExists();

            // 创建可序列化的数据格式
            Map<String, NicknameData> saveData = new HashMap<>();
            for (Map.Entry<UUID, PlayerNickname> entry : nicknames.entrySet()) {
                PlayerNickname nickname = entry.getValue();
                saveData.put(entry.getKey().toString(),
                        new NicknameData(nickname.getPrefix(), nickname.getSuffix()));
            }

            // 写入文件
            try (FileWriter writer = new FileWriter(dataFile)) {
                GSON.toJson(saveData, writer);

            }

        } catch (IOException e) {
            LOGGER.error("保存昵称数据失败: " + e.getMessage());
        }
    }

    /**
     * 加载所有昵称数据
     */
    public Map<UUID, PlayerNickname> loadData() {
        // 确保目录存在
        ensureDirectoryExists();

        if (!dataFile.exists()) {
            LOGGER.info("昵称数据文件不存在，创建新文件: {}", dataFile.getAbsolutePath());
            // 创建空的 JSON 文件
            try (FileWriter writer = new FileWriter(dataFile)) {
                writer.write("{}"); // 写入空的 JSON 对象
                LOGGER.info("成功创建空的昵称数据文件");
            } catch (IOException e) {
                LOGGER.error("创建昵称数据文件失败: " + e.getMessage());
                LOGGER.error("完整路径: {}", dataFile.getAbsolutePath());
            }
            return new HashMap<>();
        }

        try (FileReader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<String, NicknameData>>(){}.getType();
            Map<String, NicknameData> loadData = GSON.fromJson(reader, type);

            if (loadData != null) {
                Map<UUID, PlayerNickname> nicknames = new HashMap<>();
                int successCount = 0;

                for (Map.Entry<String, NicknameData> entry : loadData.entrySet()) {
                    try {
                        UUID uuid = UUID.fromString(entry.getKey());
                        NicknameData data = entry.getValue();
                        nicknames.put(uuid, new PlayerNickname(data.prefix, data.suffix));
                        successCount++;
                    } catch (IllegalArgumentException e) {
                        LOGGER.warn("无效的UUID格式: {}", entry.getKey());
                    }
                }

                return nicknames;
            }
        } catch (IOException e) {
            LOGGER.error("加载昵称数据失败: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error("解析昵称数据文件时出错: " + e.getMessage());
        }

        return new HashMap<>();
    }

    /**
     * 获取配置目录路径（用于调试）
     */
    public String getConfigDirPath() {
        return configDir.getAbsolutePath();
    }

    /**
     * 获取数据文件路径（用于调试）
     */
    public String getDataFilePath() {
        return dataFile.getAbsolutePath();
    }

    /**
     * 用于 JSON 序列化的数据类
     */
    private static class NicknameData {
        String prefix;
        String suffix;

        NicknameData(String prefix, String suffix) {
            this.prefix = prefix;
            this.suffix = suffix;
        }
    }
}
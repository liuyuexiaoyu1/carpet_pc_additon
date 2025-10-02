package com.pigconnon.carpet_pc_addition;


import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mojang.text2speech.Narrator.LOGGER;

public class NicknameManager {
    private static NicknameManager instance;
    private final Map<UUID, PlayerNickname> playerNicknames = new HashMap<>();
    private static NicknameStorage storage;

    public static NicknameManager getInstance() {
        if (instance == null) {
            instance = new NicknameManager();
        }
        return instance;
    }

    /**
     * 获取 Tab 列表格式化的显示名称
     */
    public Text getFormattedTabName(UUID playerUUID, String originalName) {
        PlayerNickname nickname = playerNicknames.get(playerUUID);
        if (nickname == null || (!nickname.hasPrefix() && !nickname.hasSuffix())) {
            return null;
        }

        MutableText formatted = Text.empty();

        // 添加前缀
        if (nickname.hasPrefix()) {
            formatted.append(parseFormattingText(nickname.getPrefix()))
                    .append(Text.literal(" "));
        }

        // 添加原名称
        formatted.append(Text.literal(originalName));

        // 添加后缀
        if (nickname.hasSuffix()) {
            formatted.append(Text.literal(" "))
                    .append(parseFormattingText(nickname.getSuffix()));
        }

        return formatted;
    }




    /**
     * 解析格式代码并创建格式化文本
     */
    private Text parseFormattingText(String text) {

        return Text.literal(text.replace('&', '§'));
    }
    /**
     * 获取聊天消息格式化的显示名称
     */
    public Text getFormattedChatName(UUID playerUUID, String originalName) {
        PlayerNickname nickname = playerNicknames.get(playerUUID);
        if (nickname == null || (!nickname.hasPrefix() && !nickname.hasSuffix())) {
            return Text.literal(originalName);
        }

        MutableText formatted = Text.empty();

        // 添加前缀
        if (nickname.hasPrefix()) {
            formatted.append(parseFormattingText(nickname.getPrefix()))
                    .append(Text.literal(" "));
        }

        // 添加原名称
        formatted.append(Text.literal(originalName));

        // 添加后缀
        if (nickname.hasSuffix()) {
            formatted.append(Text.literal(" "))
                    .append(parseFormattingText(nickname.getSuffix()));
        }

        return formatted;
    }
    /**
     * 设置玩家昵称并刷新显示
     */
    public void setNickname(UUID playerUUID, PlayerNickname nickname) {
        playerNicknames.put(playerUUID, nickname);
        refreshPlayerDisplay();
    }

    /**
     * 移除玩家昵称并刷新显示
     */
    public void removeNickname(UUID playerUUID) {
        playerNicknames.remove(playerUUID);
        refreshPlayerDisplay();
    }

    /**
     * 获取玩家昵称数据
     */
    public PlayerNickname getNickname(UUID playerUUID) {
        return playerNicknames.get(playerUUID);
    }

    /**
     * 刷新玩家显示
     */
    private void refreshPlayerDisplay() {
        // 在实际实现中，通过重新发送玩家列表数据包来刷新显示
        // 这里需要获取服务器实例并调用相应的方法
    }

    public void initializeStorage() {
        storage = new NicknameStorage();
        NicknameManager.getInstance().setStorage(storage);
        NicknameManager.getInstance().loadData();
    }
    public void setStorage(NicknameStorage storage) {
        this.storage = storage;
    }
    /**
     * 加载数据（在服务器启动时调用）
     */
    public void loadData() {
        if (storage != null) {
            Map<UUID, PlayerNickname> loadedData = storage.loadData();
            playerNicknames.clear();
            playerNicknames.putAll(loadedData);
        } else {
            LOGGER.warn("存储实例未设置，无法加载数据");
        }
    }

    /**
     * 保存数据
     */
    public void saveData() {
        if (storage != null) {
            storage.saveData(playerNicknames);
        } else {
            LOGGER.warn("存储实例未设置，无法保存数据");
        }
    }
}
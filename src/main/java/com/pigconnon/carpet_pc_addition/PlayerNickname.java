package com.pigconnon.carpet_pc_addition;

public class PlayerNickname {
    private String prefix;
    private String suffix;

    public PlayerNickname() {
        this.prefix = "";
        this.suffix = "";
    }

    public PlayerNickname(String prefix, String suffix) {
        this.prefix = prefix != null ? prefix : "";
        this.suffix = suffix != null ? suffix : "";
    }

    // Getter 和 Setter 方法
    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix != null ? prefix : "";
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix != null ? suffix : "";
    }

    public boolean hasPrefix() {
        return prefix != null && !prefix.isEmpty();
    }

    public boolean hasSuffix() {
        return suffix != null && !suffix.isEmpty();
    }
}
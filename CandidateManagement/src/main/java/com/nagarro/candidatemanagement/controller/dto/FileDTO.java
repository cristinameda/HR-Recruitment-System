package com.nagarro.candidatemanagement.controller.dto;

import com.nagarro.candidatemanagement.model.FileType;

public class FileDTO {
    private long id;
    private String name;
    private FileType type;
    private byte[] data;

    public FileDTO() {
    }

    public FileDTO(String name, FileType type, byte[] data) {
        this.name = name;
        this.type = type;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileType getType() {
        return type;
    }

    public void setType(FileType type) {
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}

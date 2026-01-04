package com.jumio.roms.api.dto.branch;

import java.util.UUID;

public class BranchResponse {
    private UUID id;
    private String name;
    private String address;

    public BranchResponse() { }

    public BranchResponse(UUID id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}

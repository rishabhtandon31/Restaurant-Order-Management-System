package com.jumio.roms.api.dto.branch;

import jakarta.validation.constraints.NotBlank;

public class BranchCreateRequest {
    @NotBlank(message = "name is required")
    private String name;
    @NotBlank(message = "address is required")
    private String address;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}

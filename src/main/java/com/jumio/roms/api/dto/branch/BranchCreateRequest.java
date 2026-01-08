package com.jumio.roms.api.dto.branch;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BranchCreateRequest {
    @NotBlank(message = "name is required")
    private String name;
    
    @NotBlank(message = "address is required")
    private String address;
}

package com.jumio.roms.api.dto.branch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BranchResponse {
    private UUID id;
    private String name;
    private String address;
}

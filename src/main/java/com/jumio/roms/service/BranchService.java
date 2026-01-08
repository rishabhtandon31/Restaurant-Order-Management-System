package com.jumio.roms.service;

import com.jumio.roms.api.dto.branch.BranchCreateRequest;
import com.jumio.roms.api.dto.branch.BranchResponse;
import com.jumio.roms.domain.entity.Branch;

import java.util.List;
import java.util.UUID;

public interface BranchService {
    BranchResponse createBranch(BranchCreateRequest request);
    List<BranchResponse> getAllBranches();
    BranchResponse getBranchById(UUID branchId);
    Branch getBranchOrThrow(UUID branchId);
}

package com.jumio.roms.api.controller;

import com.jumio.roms.api.dto.branch.BranchCreateRequest;
import com.jumio.roms.api.dto.branch.BranchResponse;
import com.jumio.roms.service.BranchService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PostMapping
    public BranchResponse create(@Valid @RequestBody BranchCreateRequest req) {
        return branchService.createBranch(req);
    }

    @GetMapping
    public List<BranchResponse> list() {
        return branchService.getAllBranches();
    }

    @GetMapping("/{branchId}")
    public BranchResponse get(@PathVariable UUID branchId) {
        return branchService.getBranchById(branchId);
    }
}

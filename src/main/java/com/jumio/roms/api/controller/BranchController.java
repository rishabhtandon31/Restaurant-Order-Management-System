package com.jumio.roms.api.controller;

import com.jumio.roms.api.dto.branch.BranchCreateRequest;
import com.jumio.roms.api.dto.branch.BranchResponse;
import com.jumio.roms.domain.entity.Branch;
import com.jumio.roms.exception.NotFoundException;
import com.jumio.roms.repo.BranchRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/branches")
public class BranchController {

    private final BranchRepository branchRepo;

    public BranchController(BranchRepository branchRepo) {
        this.branchRepo = branchRepo;
    }

    @PostMapping
    public BranchResponse create(@Valid @RequestBody BranchCreateRequest req) {
        Branch b = new Branch();
        b.setName(req.getName());
        b.setAddress(req.getAddress());
        Branch saved = branchRepo.save(b);
        return new BranchResponse(saved.getId(), saved.getName(), saved.getAddress());
    }

    @GetMapping
    public List<BranchResponse> list() {
        return branchRepo.findAll().stream()
                .map(b -> new BranchResponse(b.getId(), b.getName(), b.getAddress()))
                .toList();
    }

    @GetMapping("/{branchId}")
    public BranchResponse get(@PathVariable UUID branchId) {
        Branch b = branchRepo.findById(branchId).orElseThrow(() -> new NotFoundException("Branch not found: " + branchId));
        return new BranchResponse(b.getId(), b.getName(), b.getAddress());
    }
}

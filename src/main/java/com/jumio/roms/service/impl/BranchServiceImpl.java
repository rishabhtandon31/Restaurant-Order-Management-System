package com.jumio.roms.service.impl;

import com.jumio.roms.api.dto.branch.BranchCreateRequest;
import com.jumio.roms.api.dto.branch.BranchResponse;
import com.jumio.roms.domain.entity.Branch;
import com.jumio.roms.exception.NotFoundException;
import com.jumio.roms.repo.BranchRepository;
import com.jumio.roms.service.BranchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BranchServiceImpl implements BranchService {

    private static final Logger log = LoggerFactory.getLogger(BranchServiceImpl.class);

    private final BranchRepository branchRepository;

    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional
    public BranchResponse createBranch(BranchCreateRequest request) {
        Branch newBranch = new Branch();
        newBranch.setName(request.getName());
        newBranch.setAddress(request.getAddress());
        Branch saved = branchRepository.save(newBranch);
        log.info("Created branch {} with name: {}", saved.getId(), saved.getName());
        return new BranchResponse(saved.getId(), saved.getName(), saved.getAddress());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchResponse> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(branch -> new BranchResponse(branch.getId(), branch.getName(), branch.getAddress()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BranchResponse getBranchById(UUID branchId) {
        Branch branch = getBranchOrThrow(branchId);
        return new BranchResponse(branch.getId(), branch.getName(), branch.getAddress());
    }

    @Override
    @Transactional(readOnly = true)
    public Branch getBranchOrThrow(UUID branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> new NotFoundException("Branch not found: " + branchId));
    }
}


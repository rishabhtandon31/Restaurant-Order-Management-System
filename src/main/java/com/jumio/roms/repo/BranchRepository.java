package com.jumio.roms.repo;

import com.jumio.roms.domain.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> { }

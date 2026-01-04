package com.jumio.roms.repo;

import com.jumio.roms.domain.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RefundRepository extends JpaRepository<Refund, UUID> {
    List<Refund> findByPayment_Id(UUID paymentId);
}

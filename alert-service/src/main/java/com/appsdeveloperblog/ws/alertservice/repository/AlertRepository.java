package com.appsdeveloperblog.ws.alertservice.repository;

import com.appsdeveloperblog.ws.alertservice.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
}

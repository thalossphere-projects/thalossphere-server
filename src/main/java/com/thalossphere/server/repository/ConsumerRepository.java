package com.thalossphere.server.repository;

import com.thalossphere.server.entity.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ConsumerRepository extends JpaRepository<Consumer, Integer>, JpaSpecificationExecutor<Consumer> {

    Consumer findByNameAndProviderName(String name, String providerName);

    List<Consumer> findByProviderName(String providerName);

}

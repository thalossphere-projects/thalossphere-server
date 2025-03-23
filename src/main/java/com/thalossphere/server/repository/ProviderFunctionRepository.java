package com.thalossphere.server.repository;

import com.thalossphere.server.entity.ProviderFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ProviderFunctionRepository extends JpaRepository<ProviderFunction, Integer>, JpaSpecificationExecutor<ProviderFunction> {

    List<ProviderFunction> findByProviderNameAndUrlIn(String providerName, List<String> urlList);

}

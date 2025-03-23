package com.thalossphere.server.repository;

import com.thalossphere.server.entity.Cmd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface CmdRepository extends JpaRepository<Cmd, Integer>, JpaSpecificationExecutor<Cmd> {

    List<Cmd> findFirst500ByCreateTimeGreaterThanEqualAndStatusOrderByCreateTimeAsc(
            LocalDateTime createTime, int status);

}

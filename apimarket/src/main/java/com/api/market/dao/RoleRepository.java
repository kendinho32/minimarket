package com.api.market.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.market.entity.Role;
import com.api.market.entity.RoleName;

import java.util.Optional;

/**
 * Created by Kendall Navarro on 02/08/17.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}

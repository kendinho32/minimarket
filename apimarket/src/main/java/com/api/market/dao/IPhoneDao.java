package com.api.market.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.api.market.entity.Phone;

@Repository
public interface IPhoneDao extends CrudRepository<Phone, Long> {

}

package com.customeranalytics.repository;

import com.customeranalytics.domain.Stuff;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Stuff entity.
 */
@SuppressWarnings("unused")
@Repository
public interface StuffRepository extends JpaRepository<Stuff, Long> {

}

package com.app.greenFuxes.repository.officeRepository;

import com.app.greenFuxes.entity.office.Office;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfficeRepository extends CrudRepository<Office, Long> {

}

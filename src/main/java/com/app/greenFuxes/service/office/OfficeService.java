package com.app.greenFuxes.service.office;

import com.app.greenFuxes.entity.office.Office;

public interface OfficeService {

  Office create() throws Exception;

  void setHeadCount(int headcount) throws Exception;

  Office findById(Long id);
}

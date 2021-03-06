package com.app.greenFuxes.service.office;

import com.app.greenFuxes.entity.office.Office;
import com.app.greenFuxes.repository.officeRepository.OfficeRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class OfficeServiceImpl implements OfficeService {

  private final OfficeRepository officeRepository;

  @Autowired
  public OfficeServiceImpl(OfficeRepository officeRepository) {
    this.officeRepository = officeRepository;
  }

  @Override
  @EventListener(ApplicationReadyEvent.class)
  public Office create() throws Exception {
    List<Office> list = (List<Office>) officeRepository.findAll();
    if (list.size() == 0) {
      Office office = new Office();
      return officeRepository.save(office);
    } else {
      return null;
    }
  }

  @Override
  public void setHeadCount(int headcount) throws Exception {
    if (headcount != 0 && headcount >= 0) {
      Optional<Office> temp = officeRepository.findById(1L);
      if (temp.isPresent()) {
        Office office = temp.get();
        office.setCapacity(headcount);
        officeRepository.save(office);
      }
    }
  }

  @Override
  public Office findById(Long id) {
    return officeRepository.findById(id).orElse(null);
  }
}

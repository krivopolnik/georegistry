package com.borschevski.georegistry.service;

import com.borschevski.georegistry.entity.Obec;
import com.borschevski.georegistry.entity.CastObce;
import com.borschevski.georegistry.repository.ObecRepository;
import com.borschevski.georegistry.repository.CastObceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseService {

    private final ObecRepository obecRepository;
    private final CastObceRepository castObceRepository;

    @Transactional
    public void saveObec(@NotNull Obec currentObec) {
        if (currentObec.getKod() != null) {
            if (obecRepository.existsById(currentObec.getKod())) {
                log.info("Updating existing entity: {}", currentObec);
            } else {
                log.info("Creating new entity: {}", currentObec);
            }
        } else {
            log.info("Saving new entity without kod: {}", currentObec);
        }
        obecRepository.save(currentObec);
    }

    @Transactional
    public void saveCastObce(CastObce castObce) {
        try {
            castObceRepository.save(castObce);
            log.info("Successfully saved 'CastObce' with Kod: {}, Nazev: {}, Obec Kod: {}", castObce.getKod(), castObce.getNazev(), castObce.getKod());
        } catch (DataAccessException e) {
            log.error("Failed to save 'CastObce' with Kod: {}, Nazev: {}, Obec Kod: {}. Error: {}", castObce.getKod(), castObce.getNazev(), castObce.getKod(), e.getMessage(), e);
            throw e; // Rethrow to trigger transaction rollback if needed
        }
    }
}

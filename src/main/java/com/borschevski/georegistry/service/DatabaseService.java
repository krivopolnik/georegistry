package com.borschevski.georegistry.service;

import com.borschevski.georegistry.entity.Obec;
import com.borschevski.georegistry.entity.CastObce;
import com.borschevski.georegistry.repository.ObecRepository;
import com.borschevski.georegistry.repository.CastObceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public void saveObec(Obec obec) {
        try {
            obecRepository.save(obec);
            log.info("Successfully saved 'Obec' with Kod: {} and Nazev: {}", obec.getKod(), obec.getNazev());
        } catch (DataAccessException e) {
            log.error("Failed to save 'Obec' with Kod: {} and Nazev: {}. Error: {}", obec.getKod(), obec.getNazev(), e.getMessage(), e);
            throw e; // Rethrow to trigger transaction rollback if needed
        }
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

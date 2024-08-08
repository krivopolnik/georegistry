package com.borschevski.georegistry.service;

import com.borschevski.georegistry.entity.Obec;
import com.borschevski.georegistry.entity.CastObce;
import com.borschevski.georegistry.repository.ObecRepository;
import com.borschevski.georegistry.repository.CastObceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseService {

    @Autowired
    private ObecRepository obecRepository;

    @Autowired
    private CastObceRepository castObceRepository;

    public void saveObec(Obec obec) {
        obecRepository.save(obec);
    }

    public void saveCastObce(CastObce castObce) {
        castObceRepository.save(castObce);
    }
}

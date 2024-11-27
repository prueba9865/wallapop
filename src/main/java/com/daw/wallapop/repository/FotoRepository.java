package com.daw.wallapop.repository;

import com.daw.wallapop.entity.FotoAnuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoRepository extends JpaRepository<FotoAnuncio, Long> {
}

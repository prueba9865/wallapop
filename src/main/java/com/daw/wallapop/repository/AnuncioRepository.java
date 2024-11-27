package com.daw.wallapop.repository;

import com.daw.wallapop.entity.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio,Long> {
    List<Anuncio> findAllByOrderByFechaDesc();
}

package com.lembrai.repository;

import com.lembrai.model.Lembrete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LembreteRepository extends JpaRepository<Lembrete, Long> {
    // Aqui você pode criar consultas personalizadas se quiser, por enquanto só o básico do JpaRepository já funciona.
}

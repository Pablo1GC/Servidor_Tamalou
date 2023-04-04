package com.tamalou.servidor.modelo.persistencia.JPAData;

import com.tamalou.servidor.modelo.entidad.entidadesUsuario.GamePlayer;
import com.tamalou.servidor.modelo.entidad.entidadesUsuario.GamePlayerId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, GamePlayerId> {
}

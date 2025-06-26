package com.boot.ktn.repository.mapview;

import com.boot.ktn.entity.mapview.MapViewFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MapViewFileRepository extends JpaRepository<MapViewFileEntity, String> {

    @Query(value = "EXEC UP_MAPVIEWFILES_SELECT :pEMPNO, :pIP, :pRPTCD, :pJOBGB, :pPARAMS, :pUSERCONGB, :pUSERAGENT;", nativeQuery = true)
    MapViewFileEntity findFileInfoByCriteria(
            String pEMPNO,
            String pIP,
            String pRPTCD,
            String pJOBGB,
            String pPARAMS,
            String pUSERCONGB,
            String pUSERAGENT
    );
}
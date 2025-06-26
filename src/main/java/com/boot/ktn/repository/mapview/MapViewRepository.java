package com.boot.ktn.repository.mapview;

import com.boot.ktn.entity.mapview.MapViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MapViewRepository extends JpaRepository<MapViewEntity, String> {

    @Query(value = "EXEC UP_MAPVIEW_SELECT :pEMPNO, :pIP, :pRPTCD, :pJOBGB, :pPARAMS, :pUSERCONGB, :pUSERAGENT;", nativeQuery = true)
    MapViewEntity findMapViewInfoByRptCd(
            String pEMPNO,
            String pIP,
            String pRPTCD,
            String pJOBGB,
            String pPARAMS,
            String pUSERCONGB,
            String pUSERAGENT
    );
}
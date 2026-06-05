package com.cropcare.Repository;

import com.cropcare.model.Disease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DiseaseRepository extends JpaRepository<Disease, Long> {
    
    List<Disease> findByCropType(String cropType);
    
    @Query("SELECT d FROM Disease d WHERE d.cropType = :cropType " +
           "ORDER BY ABS(d.avgRedValue - :red) + ABS(d.avgGreenValue - :green) + " +
           "ABS(d.avgBrownValue - :brown)")
    List<Disease> findByColorProfile(String cropType, double red, double green, double brown);
}


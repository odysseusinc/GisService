package org.ohdsi.gisservice.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import org.ohdsi.gisservice.model.Source;

public interface SourceRepository extends EntityGraphJpaRepository<Source, String> {
}

package org.ohdsi.gisservice.utils;

import java.sql.SQLException;
import org.springframework.jdbc.core.JdbcTemplate;

@FunctionalInterface
public interface JdbcTemplateConsumer<T> {

	T execute(JdbcTemplate jdbcTemplate);
}

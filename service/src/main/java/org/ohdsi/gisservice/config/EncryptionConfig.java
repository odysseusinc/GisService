package org.ohdsi.gisservice.config;

import com.odysseusinc.datasourcemanager.encryption.EncryptorUtils;
import com.odysseusinc.datasourcemanager.encryption.NotEncrypted;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class EncryptionConfig {

	private static final Logger logger = LoggerFactory.getLogger(EncryptionConfig.class);

	@Value("${jasypt.encryptor.enabled:false}")
	private boolean encryptorEnabled;

	@Bean
	public StringEncryptor defaultStringEncryptor(Environment env) {

		StringEncryptor stringEncryptor;
		if (encryptorEnabled) {
			stringEncryptor = EncryptorUtils.buildStringEncryptor(env);
		} else {
			stringEncryptor = new NotEncrypted();
		}
		return stringEncryptor;
	}
}

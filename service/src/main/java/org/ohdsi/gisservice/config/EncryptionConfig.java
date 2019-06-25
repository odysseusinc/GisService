package org.ohdsi.gisservice.config;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.ohdsi.gisservice.utils.NotEncrypted;
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

		PBEStringEncryptor stringEncryptor;
		if (encryptorEnabled) {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setProvider(new BouncyCastleProvider());
			encryptor.setProviderName("BC");
			encryptor.setAlgorithm(env.getRequiredProperty("jasypt.encryptor.algorithm"));
			if ("PBEWithMD5AndDES".equals(env.getRequiredProperty("jasypt.encryptor.algorithm"))) {
				logger.warn("Warning:  encryption algorithm set to PBEWithMD5AndDES, which is not considered a strong encryption algorithm.  You may use PBEWITHSHA256AND128BITAES-CBC-BC, but will require special JVM configuration to support these stronger methods.");
			}
			encryptor.setKeyObtentionIterations(1000);
			String password = env.getRequiredProperty("jasypt.encryptor.password");
			if (StringUtils.isNotEmpty(password)) {
				encryptor.setPassword(password);
			}
			stringEncryptor = encryptor;
		} else {
			stringEncryptor = new NotEncrypted();
		}
		return stringEncryptor;
	}
}

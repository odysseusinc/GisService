package org.ohdsi.gisservice.service;

import java.util.Objects;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

	private static final String PREFIX = "ENC(";
	private static final String SUFFIX = ")";

	private final StringEncryptor encryptor;

	public EncryptionService(StringEncryptor encryptor) {

		this.encryptor = encryptor;
	}

	public String decrypt(String value) {

		String result = value;
		if (Objects.nonNull(value) && Objects.nonNull(encryptor) && value.startsWith(PREFIX)) {
			String encryptedData = value.substring(PREFIX.length(), value.length() - SUFFIX.length());
			result = encryptor.decrypt(encryptedData);
		}
		return result;
	}
}

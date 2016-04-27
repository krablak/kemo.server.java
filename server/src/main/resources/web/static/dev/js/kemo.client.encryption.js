var kemo = function(kemo) {
	kemo.encryption = kemo.encryption || {};

	/**
	 * Encryption module configuration.
	 */
	kemo.encryption.config = {
		ALGORITHM : 'AES-CFB',
		ENCODING : 'utf8',
		IV_SIZE : 16
	};

	// Helper function for asserting function arguments.
	var assertString = function(name, value) {
		var name = name ? name : "undefined";
		if (value === undefined || value === null) {
			throw "Required argument '" + name + "' is missing.";
		}
		if (typeof (value) !== "string") {
			throw "Required argument '" + value + "' is not type of string.";
		}
	};

	// Unified way of key bytes creation
	var toKeyBytes = function(keyStr) {
		return forge.md.sha256.create().update(keyStr).digest().getBytes();
	}

	/**
	 * Encrypts given message using provided key.
	 * 
	 * @param key
	 *            string with encryption key.
	 * 
	 * @param message
	 *            message as string to be encrypted.
	 * 
	 * @returns encrypted message as base64 string with first 16 bytes
	 *          representing initialization vector.
	 */
	kemo.encryption.encrypt = function(key, message) {
		assertString('key', key);
		assertString('message', message);
		// Generate random IV
		var iv = forge.random.getBytesSync(kemo.encryption.config.IV_SIZE);
		// Prepare encryption component
		var cipher = forge.cipher.createCipher(kemo.encryption.config.ALGORITHM, toKeyBytes(key));
		cipher.start({
			iv : iv
		});
		cipher.update(forge.util.createBuffer(message, kemo.encryption.config.ENCODING));
		// Get encrypted result
		var encrMessageBytes = cipher.output.getBytes();
		// Cleanup encryption component
		cipher.finish();
		// Create single Base64 encryption result from IV and encrypted message
		return forge.util.encode64(iv + encrMessageBytes);
	};

	/**
	 * Decrypts given encrypted data.
	 * 
	 * @param key
	 *            string with encryption key.
	 * @param encryptedStr
	 *            encrypted data as base64 string.
	 * @returns decrypted message as string.
	 */
	kemo.encryption.decrypt = function(key, encryptedStr) {
		assertString('key', key);
		assertString('encryptedStr', encryptedStr);
		// Decode message from base64
		var encryptedBytes = forge.util.decode64(encryptedStr);
		// First 16 bytes are IV
		var iv = encryptedBytes.slice(0, kemo.encryption.config.IV_SIZE);
		// Rest of encrypted content represents message content
		var encryptedData = forge.util.createBuffer(encryptedBytes.slice(kemo.encryption.config.IV_SIZE));
		// Prepare decryption component
		var decipher = forge.cipher.createDecipher(kemo.encryption.config.ALGORITHM, toKeyBytes(key));
		decipher.start({
			iv : iv
		});
		decipher.update(encryptedData);
		// Get decryption result
		var decryptedBytes = forge.util.decodeUtf8(decipher.output.getBytes());
		// Cleanup decryption component
		decipher.finish();
		return decryptedBytes;
	};

	// Support function to create communication address from key
	kemo.encryption.keyToAddress = function(key) {
		var keyStr = key ? "littlebitof" + key + "salt" : "defaultkey";
		return encodeURIComponent(forge.util.encode64(forge.md.sha256.create().update(keyStr).digest().getBytes()));
	};

	return kemo;
}(kemo || {});
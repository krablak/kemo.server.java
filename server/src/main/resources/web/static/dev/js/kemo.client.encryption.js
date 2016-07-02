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
		// Encode to base64 given key as string using function correctly
		// handling special characters
		// WARN: forge.js base64 function cannot be used here
		var keyBase64 = kemo.core.base64_encode(key);
		// Prepare encryption component
		var cipher = forge.cipher.createCipher(kemo.encryption.config.ALGORITHM, toKeyBytes(keyBase64));
		cipher.start({
			iv : iv
		});
		// Convert to base64 before encryption
		var messageBase64 = kemo.core.base64_encode(message)
		// Encrypt
		cipher.update(forge.util.createBuffer(messageBase64, kemo.encryption.config.ENCODING));
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
		// Encode to base64 given key as string using function correctly
		// handling special characters
		// WARN: forge.js base64 function cannot be used here
		var keyBase64 = kemo.core.base64_encode(key);
		// Prepare decryption component
		var decipher = forge.cipher.createDecipher(kemo.encryption.config.ALGORITHM, toKeyBytes(keyBase64));
		decipher.start({
			iv : iv
		});
		decipher.update(encryptedData);
		// Get decryption result which is in base64
		var decryptedBytesBase64 = forge.util.decodeUtf8(decipher.output.getBytes());
		// Cleanup decryption component
		decipher.finish();
		// Decode base64 into readable message
		var decryptedBytes = kemo.core.base64_decode(decryptedBytesBase64);
		return decryptedBytes;
	};

	// Support function to create communication address from key
	kemo.encryption.keyToAddress = function(key) {
		var saltedKey = key ? "littlebitof" + key + "salt" : "defaultkey";
		// Encode to base64 given key as string using function correctly
		// handling special characters
		// WARN: forge.js base64 function cannot be used here
		var saltedKeyBase64 = kemo.core.base64_encode(saltedKey);
		// Create hash
		var hashStr = forge.md.sha256.create().update(saltedKeyBase64).digest().getBytes();
		// Use forge base64 implementation only on bytes string content
		var base64Hash = forge.util.encode64(hashStr);
		// Encode into URL acceptable form
		return encodeURIComponent(base64Hash);
	};

	return kemo;
}(kemo || {});
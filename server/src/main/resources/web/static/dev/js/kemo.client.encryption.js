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
		var saltedKey = kemo.encryption.saltAddressKey(key);
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

	// Salts given key for path address creation
	kemo.encryption.saltAddressKey = function(key) {
		if (key === null || key === undefined || key === "") {
			return "b8af31bea5a94d1582c53cf602bb19ab";
		} else if (key.length === 1) {
			var base64Key = kemo.core.base64_encode(key);
			return "37bc92fbe3a8425ba0a2902ceb4383aa" + base64Key + "b7fa709330484789af66bbbaadfddc51";
		} else {
			var base64Key = kemo.core.base64_encode(key);
			var sessPathKeys = "";
			for (var i = 0; i < base64Key.length; i++) {
				if (i % 2 === 1) {
					sessPathKeys = sessPathKeys.concat(base64Key[i]);
				}
			}
			return "1413ef72661a47c99724d9ec13a80fdf" + sessPathKeys + "734cc96c37244fd9a20336509dfd28b4";
		}
	}
	
	// Salts given key for encryption
	kemo.encryption.saltEncKey = function(key) {
		if (key === null || key === undefined || key === "") {
			return "e79a713eec5e4d89991c0428efd5704a";
		} else if (key.length === 1) {
			var base64Key = kemo.core.base64_encode(key);
			return "a36fd8ab8ae04a38b7c04c877c6f39e9" + base64Key + "b7a9bd24b0314235aeb912c501a829a5";
		} else {
			var base64Key = kemo.core.base64_encode(key);
			var sessPathKeys = "";
			for (var i = 0; i < base64Key.length; i++) {
				if (i % 2 === 0) {
					sessPathKeys = sessPathKeys.concat(base64Key[i]);
				}
			}
			return "caf8069bd06145e3b926fa23c2fc419e" + sessPathKeys + "efbde326ef20437389a65d8f776f32dc";
		}
	}

	return kemo;
}(kemo || {});
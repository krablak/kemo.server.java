QUnit.test("Simple encryption/decryption scenario.", function(assert) {
	// Prepare test data
	var keyStr = "someStrKey";
	var message = "Some test message text.";

	// Perform encryption
	var encryptedMessage = kemo.encryption.encrypt(keyStr, message);
	assert.ok(encryptedMessage !== null && encryptedMessage !== undefined, "Basic encrypted data checks.");

	// Decrypt data
	var decryptedMessage = kemo.encryption.decrypt(keyStr, encryptedMessage);

	assert.equal(decryptedMessage, message, "Source and decrypted message should be same.");
});

QUnit.test("Empty data encryption/decryption scenario.", function(assert) {
	// Prepare test data
	var keyStr = "someStrKey";
	var message = "";

	// Perform encryption
	var encryptedMessage = kemo.encryption.encrypt(keyStr, message);
	assert.ok(encryptedMessage !== null && encryptedMessage !== undefined, "Basic encrypted data checks.");

	// Decrypt data
	var decryptedMessage = kemo.encryption.decrypt(keyStr, encryptedMessage);

	assert.equal(decryptedMessage, message, "Source and decrypted message should be same.");
});

QUnit.test("Smoke test module initialization check.", function(assert) {
	assert.ok(kemo, "Kemo module exists.");
	assert.ok(kemo.encryption, "Kemo encryption module exists.");
});

QUnit.test("Run encryption function with wrong or missing arguments.", function(assert) {
	assert.throws(function() {
		kemo.encryption.encrypt();
	}, "Missing required arguments.");
	assert.throws(function() {
		kemo.encryption.encrypt(null, null);
	}, "Missing required arguments.");
	assert.throws(function() {
		kemo.encryption.encrypt("", null);
	}, "Missing required arguments.");
	assert.throws(function() {
		kemo.encryption.encrypt("", null);
	}, "Missing required arguments.");
	assert.ok(kemo.encryption.encrypt("", ""), "Encryption with null args should not fail.");
});

QUnit.test("Run decryption function with wrong or missing arguments.", function(assert) {
	assert.throws(function() {
		kemo.encryption.decrypt();
	}, "Missing required arguments.");
	assert.throws(function() {
		kemo.encryption.decrypt(null, null);
	}, "Missing required arguments.");
	assert.throws(function() {
		kemo.encryption.decrypt("", null);
	}, "Missing required arguments.");
	assert.throws(function() {
		kemo.encryption.encrypt("", null);
	}, "Missing required arguments.");
	assert.equal(kemo.encryption.decrypt("", ""), "", "Decryption with null empty string args should not fail.");
});

QUnit.test("Prepareation of messagin url from key.", function(assert) {
	assert.equal(kemo.encryption.keyToAddress("key"), "x6OhZhAybKkIqfjFBOEIiBccUiB0P9R7VIlgBOId%2BPY%3D", "Happy day address part preparation.");
});

QUnit.test("Prepareation of messagin url from key with wrong arguments.", function(assert) {
	var defaultAddress = "a32WqNXEpu76mdllChG6m9xtaPcjM9sUJkYl9JeAefg%3D";
	assert.equal(kemo.encryption.keyToAddress(), defaultAddress, "Should pass with default result.");
	assert.equal(kemo.encryption.keyToAddress(null), defaultAddress, "Should pass with default result.");
	assert.equal(kemo.encryption.keyToAddress(""), defaultAddress, "Should pass with default result.");
});

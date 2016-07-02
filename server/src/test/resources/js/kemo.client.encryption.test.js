QUnit.module("kemo.client.encryption.test.js_keyToAddress", function() {
	QUnit.test("kemo.encryption.keyToAddress empty key", function(assert) {
		var key = "";
		var expAddress = "ZIj69fen3Ef7i5hxT3TWQtXyi8B8mS7s7f6BysBy2rE%3D";
		assert.equal(kemo.encryption.keyToAddress(key), expAddress, "Expected address passed!");
	});
	QUnit.test("kemo.encryption.keyToAddress simple key", function(assert) {
		var key = "some simple key";
		var expAddress = "Ct%2F2xnDU%2BIgUa6oo6A2Nwg36Zd8liJpbplBdZkPqXME%3D";
		assert.equal(kemo.encryption.keyToAddress(key), expAddress, "Expected address passed!");
	});
	QUnit.test("kemo.encryption.keyToAddress CZ key", function(assert) {
		var key = "Nechť již hříšné saxofony ďáblů rozzvučí síň úděsnými tóny waltzu, tanga a quickstepu.";
		var expAddress = "IIchqOzYOmGO0NXbxWeQjs%2B9TO8OB2kZffm3zlRn7YU%3D";
		assert.equal(kemo.encryption.keyToAddress(key), expAddress, "Expected address passed!");
	});
	QUnit.test("kemo.encryption.keyToAddress emoji key", function(assert) {
		var key = "😀😬😁😂😃😄😅😆😇😉😊🙂🙃☺️😋😌😍😘🙊🐒🐔🐧🐦🐤🐣🐥🐺🐗🐴🦄🐝🐛🐌🐞🐜🕷";
		var expAddress = "ASEx4HxcRbSg1mL8xztUrQaVB3%2ByWC%2FcC%2Bu8RR9iFIw%3D";
		assert.equal(kemo.encryption.keyToAddress(key), expAddress, "Expected address passed!");
	});
});

QUnit.module("kemo.client.encryption.test.js_encrypt", function() {
	QUnit.test("kemo.encryption.encrypt empty key and message", function(assert) {
		var key = "";
		var message = "";
		var encData = kemo.encryption.encrypt(key, message);
		var decData = kemo.encryption.decrypt(key, encData)
		assert.equal(decData, message, "Expected exncryption result passed!");
	});	
	QUnit.test("kemo.encryption.encrypt ASCII key and message", function(assert) {
		var key = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		var message = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		var encData = kemo.encryption.encrypt(key, message);
		var decData = kemo.encryption.decrypt(key, encData)
		assert.equal(decData, message, "Expected exncryption result passed!");
	});
	QUnit.test("kemo.encryption.encrypt Decrypt encrypted CZ data", function(assert) {
		var key = "Nechť již hříšné saxofony ďáblů rozzvučí síň úděsnými tóny waltzu, tanga a quickstepu.";
		var message = "Nechť již hříšné saxofony ďáblů rozzvučí síň úděsnými tóny waltzu, tanga a quickstepu.";
		var encData = kemo.encryption.encrypt(key, message);
		var decData = kemo.encryption.decrypt(key, encData)
		assert.equal(decData, message, "Expected exncryption result passed!");
	});
	QUnit.test("kemo.encryption.encrypt emoji key and message", function(assert) {
		var key = "😀😬😁😂😃😄😅😆😇😉😊🙂🙃☺️😋😌😍😘🙊🐒🐔🐧🐦🐤🐣🐥🐺🐗🐴🦄🐝🐛🐌🐞🐜🕷";
		var message = "😀😬😁😂😃😄😅😆😇😉😊🙂🙃☺️😋😌😍😘🙊🐒🐔🐧🐦🐤🐣🐥🐺🐗🐴🦄🐝🐛🐌🐞🐜🕷";
		var encData = kemo.encryption.encrypt(key, message);
		var decData = kemo.encryption.decrypt(key, encData)
		assert.equal(decData, message, "Expected exncryption result passed!");
	});
	QUnit.test("kemo.encryption.encrypt decrypt swift complex message", function(assert) {
		var key = "😍😘🙊🐒🐔Nechť již hříšné saxofony ďáblů rozzvučí síň úděsnými tóny waltzu, tanga a quickstepu.😂😃😄😅😆😇😉";
		var message = "😍😘🙊🐒🐔Nechť již hříšné saxofony ďáblů rozzvučí síň úděsnými tóny waltzu, tanga a quickstepu.😂😃😄😅😆😇😉";
		var encMessage = "ZkRFqo8sym1gQvPP5y5tJnC0vEvvKG6q/9mi4Dz2fl+MnYumJ9r9ZoAfAbEVL8h8VQPVnZ28sK1skx6wKrjz/ZjceQIGV9xOiedQC7rAfHzqbfE6feaRP7foC16+IlS/cGhzP9L2/xuKSJXSixBmAzDRhiqeWPfTCcLjNk7aSRktyTS6GxbkE7t+sCkqsrm8mo+aDFy6xqxfkP5PDeVgGtH8oYgBLXTlJ9EvUtlrZ0WpfmEGK5LdDQPkGbEbiP5QIrAh8C1y2yB5iYuOtfSteKoK5aXEp7P8YJf4TJpMEWo=";
		var decData = kemo.encryption.decrypt(key, encMessage);
		assert.equal(decData, message, "Expected exncryption result passed!");
	});
	QUnit.test("kemo.encryption.encrypt decrypt swift single emoji char", function(assert) {
		var key = "😍";
		var message = "😍";
		var encMessage = "ONJT4pnCAzI6XwNrKgcZbq/2582wj2tpSUj9LhlQJU0=";
		var decData = kemo.encryption.decrypt(key, encMessage);
		assert.equal(decData, message, "Expected exncryption result passed!");
	});
});
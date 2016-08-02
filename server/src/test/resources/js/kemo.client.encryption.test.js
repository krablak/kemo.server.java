QUnit.module("kemo.client.encryption.test.js_keyToAddress", function() {
	QUnit.test("kemo.encryption.keyToAddress empty key", function(assert) {
		var key = "";
		var expAddress = "tO%2BK0BckFeNDo9FqP0o45T%2FOXezXXmctjTSwenoBObw%3D";
		assert.equal(kemo.encryption.keyToAddress(key), expAddress, "Expected address passed!");
	});
	QUnit.test("kemo.encryption.keyToAddress single char key", function(assert) {
		var key = "r";
		var expAddress = "iUx7Yrz9M%2BJYYM0nR3LyITlIxT1LcgzI8DBTS4WDpP8%3D";
		assert.equal(kemo.encryption.keyToAddress(key), expAddress, "Expected address passed!");
	});
	QUnit.test("kemo.encryption.keyToAddress simple key", function(assert) {
		var key = "some simple key";
		var expAddress = "SSw9K8FZxn%2BqFltVG3heTnAvetrKI%2FrSQBPAG%2FO%2BPNM%3D";
		assert.equal(kemo.encryption.keyToAddress(key), expAddress, "Expected address passed!");
	});
	QUnit.test("kemo.encryption.keyToAddress CZ key", function(assert) {
		var key = "Nechť již hříšné saxofony ďáblů rozzvučí síň úděsnými tóny waltzu, tanga a quickstepu.";
		var expAddress = "wVk8GBflnEYNVHzGAeODYgTCNnvTiHdi7dodnbZwTKw%3D";
		assert.equal(kemo.encryption.keyToAddress(key), expAddress, "Expected address passed!");
	});
	QUnit.test("kemo.encryption.keyToAddress emoji key", function(assert) {
		var key = "😀😬😁😂😃😄😅😆😇😉😊🙂🙃☺️😋😌😍😘🙊🐒🐔🐧🐦🐤🐣🐥🐺🐗🐴🦄🐝🐛🐌🐞🐜🕷";
		var expAddress = "%2Bej2ag4aZ2BdJ%2FSWqnTMtTR3lX2l7%2Fwz3K6JrE4sRNk%3D";
		assert.equal(kemo.encryption.keyToAddress(key), expAddress, "Expected address passed!");
	});
});

QUnit.module("kemo.client.encryption.test.js_saltEncKey", function() {
	QUnit.test("kemo.encryption.saltEncKey empty key", function(assert) {
		var key = "";
		var expKey = "e79a713eec5e4d89991c0428efd5704a";
		assert.equal(kemo.encryption.saltEncKey(key), expKey, "Expected salted key passed!");
	});
	QUnit.test("kemo.encryption.saltEncKey single char key", function(assert) {
		var key = "r";
		var expKey = "a36fd8ab8ae04a38b7c04c877c6f39e9cg==b7a9bd24b0314235aeb912c501a829a5";
		assert.equal(kemo.encryption.saltEncKey(key), expKey, "Expected salted key passed!");
	});
	QUnit.test("kemo.encryption.saltEncKey simple key", function(assert) {
		var key = "some simple key";
		var expKey = "caf8069bd06145e3b926fa23c2fc419ec9ZBa1bUaVefbde326ef20437389a65d8f776f32dc";
		assert.equal(kemo.encryption.saltEncKey(key), expKey, "Expected salted key passed!");
	});
	QUnit.test("kemo.encryption.saltEncKey CZ key", function(assert) {
		var key = "Nechť již hříšné saxofony ďáblů rozzvučí síň úděsnými tóny waltzu, tanga a quickstepu.";
		var expKey = "caf8069bd06145e3b926fa23c2fc419eTVaWIpx4aWw3o7qBYhZ9eDjOYzrBbpdXjOIPrWIOZSc7v1ITs5IdbRdwdFZEYBdlaNZBL=efbde326ef20437389a65d8f776f32dc";
		assert.equal(kemo.encryption.saltEncKey(key), expKey, "Expected salted key passed!");
	});
	QUnit.test("kemo.encryption.saltEncKey emoji key", function(assert) {
		var key = "😀😬😁😂😃😄😅😆😇😉😊🙂🙃☺️😋😌😍😘🙊🐒🐔🐧🐦🐤🐣🐥🐺🐗🐴🦄🐝🐛🐌🐞🐜🕷";
		var expKey = "caf8069bd06145e3b926fa23c2fc419e8+gCmzni8+gCmPni8+hCmbni8+iCmrnm8+gKu+jCmvni8+jCmjnm8+kCkTnC8+pCkTnC8+pCkrnC8+tCpTnC8+mCkznC8+nClcefbde326ef20437389a65d8f776f32dc";
		assert.equal(kemo.encryption.saltEncKey(key), expKey, "Expected address passed!");
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
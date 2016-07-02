QUnit.module("kemo.core.js_base64", function() {
	QUnit.test("ASCII chars to base64", function(assert) {
		var encoded = "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5ejAxMjM0NTY3ODkrLz0=";
		var decoded = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
		assert.equal(kemo.core.base64_encode(decoded), encoded, "ASCII encoding Passed!");
		assert.equal(kemo.core.base64_decode(encoded), decoded, "ASCII decoding Passed!");
	});
	QUnit.test("CZ chars to base64", function(assert) {
		var encoded = "TmVjaMWlIGppxb4gaMWZw63FoW7DqSBzYXhvZm9ueSDEj8OhYmzFryByb3p6dnXEjcOtIHPDrcWIIMO6ZMSbc27DvW1pIHTDs255IHdhbHR6dSwgdGFuZ2EgYSBxdWlja3N0ZXB1Lg==";
		var decoded = "Nechť již hříšné saxofony ďáblů rozzvučí síň úděsnými tóny waltzu, tanga a quickstepu.";
		assert.equal(kemo.core.base64_encode(decoded), encoded, "All CZ chars encode passed!");
		assert.equal(kemo.core.base64_decode(encoded), decoded, "All CZ chars decode passed!");
	});
	QUnit.test("Few emojis chars to base64", function(assert) {
		var encoded = "8J+YgA==";
		var decoded = "😀";
		assert.equal(kemo.core.base64_encode(decoded), encoded, "Single emoji char encode passed!");
		assert.equal(kemo.core.base64_decode(encoded), decoded, "Single emoji char decode passed!");
	});
	QUnit.test("😍 emoji char to base64", function(assert) {
		var encoded = "8J+YjQ==";
		var decoded = "😍";
		assert.equal(kemo.core.base64_encode(decoded), encoded, "Single emoji char encode passed!");
		assert.equal(kemo.core.base64_decode(encoded), decoded, "Single emoji char decode passed!");
	});
	QUnit.test("Many emojis chars to base64", function(assert) {
		var encoded = "8J+YgPCfmKzwn5iB8J+YgvCfmIPwn5iE8J+YhfCfmIbwn5iH8J+YifCfmIrwn5mC8J+Zg+KYuu+4j/CfmIvwn5iM8J+YjfCfmJjwn5mK8J+QkvCfkJTwn5Cn8J+QpvCfkKTwn5Cj8J+QpfCfkLrwn5CX8J+QtPCfpoTwn5Cd8J+Qm/CfkIzwn5Ce8J+QnPCflbc=";
		var decoded = "😀😬😁😂😃😄😅😆😇😉😊🙂🙃☺️😋😌😍😘🙊🐒🐔🐧🐦🐤🐣🐥🐺🐗🐴🦄🐝🐛🐌🐞🐜🕷";

		assert.equal(kemo.core.base64_encode(decoded), encoded, "Emoji chars encode passed!");
		assert.equal(kemo.core.base64_decode(encoded), decoded, "Emoji chars decode passed!");
	});
});
// K emo simple messaging client
var kemo = function(kemo) {
	kemo.client = kemo.client || {};
	// Kemo client communication components module
	kemo.client.comm = kemo.client.comm || {};

	// Support function to create communication address from key
	var keyToAddress = function(key) {
		return encodeURIComponent(sjcl.codec.base64.fromBits(sjcl.hash.sha256.hash("littlebitof" + key + "salt")));
	};

	var saltKey = function(key) {
		return "clientenc" + key + "salt";
	};

	// Simple wrapper over encryption part
	var encrypt = function(key, data) {
		return sjcl.encrypt(saltKey(key), data);
	};

	// Simple wrapper over decryption part
	var decrypt = function(key, data) {
		var decryptedData = "";
		try {
			decryptedData = sjcl.decrypt(saltKey(key), data);
		} catch (err) {
			// Encryption fails for whatever reason.. just log it
			console.error("Unexpected error when decrypting received message.", err)
		}
		return decryptedData;
	};

	// Resolves url
	var resolveUrl = function(key) {
		var host = "";
		if (location.protocol === "http:") {
			if (location.port === "8080") {
				// Probably local dev environment
				host = "ws://" + location.host;
			} else {
				// Openshift on http
				host = "ws://" + location.hostname + ":8000";
			}
		} else {
			// Openshift on https
			host = "wss://" + location.hostname + ":8443";
		}
		return host + "/messaging/" + keyToAddress(key);
	};

	// Object representing messaging API
	kemo.client.comm.Messaging = function(key) {
		var self = this;
		// Messaging key
		self.key = key;
		// Websocket reference
		self.ws = null;

		// Connect API to server websocket
		self.connect = function(key, afterOnOpen) {
			try {
				self.key = key;
				// Close existing web socket connection
				if (self.ws && self.ws.readyState === 1) {
					self.ws.close();
				}
				// Open new connection with given key
				self.ws = new WebSocket(resolveUrl(key));
				self.ws.onopen = function() {
					self.ws.onmessage = function(event) {
						self.onmessage(decrypt(self.key, event.data));
					};
					if (afterOnOpen) {
						afterOnOpen();
					}
				}
			} catch (err) {
				// Do not propagate up... silently fails.
			}
		};

		// Called when message is received and decrypted
		self.onmessage = function(message) {

		};

		// Sends message
		self.send = function(key, message) {
			if (self.ws.readyState !== 1 || self.key !== key) {
				self.connect(key, function() {
					self.ws.send(encrypt(key, message));
				});
			} else {
				self.ws.send(encrypt(key, message));
			}
		};

		// Checks connection and reconnects client when needed
		self.connectionCheck = function() {
			if (self.ws && self.ws.readyState !== 1) {
				self.connect(self.key);
			}
			// Plan next connection check round
			setTimeout(self.connectionCheck, 5000);
		};

		return self;
	}

	return kemo;
}(kemo || {});
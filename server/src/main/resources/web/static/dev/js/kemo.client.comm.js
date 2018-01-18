// K emo simple messaging client
var kemo = function(kemo) {
	kemo.client = kemo.client || {};
	// Kemo client communication components module
	kemo.client.comm = kemo.client.comm || {};

	// Communication module configuration
	kemo.client.comm.config = {
		// Flag says if audit logging on module level is enabled
		auditEnabled : false,
		// Timeout for regular connection check
		checkTimeout : 15000,
		// Time out for reconnect attempt after error
		onErrorReconnect : 10000
	};

	// Support function to create communication address from key
	var keyToAddress = function(key) {
		return kemo.encryption.keyToAddress(key);
	};

	var saltKey = function(key) {
		return kemo.encryption.saltEncKey(key);
	}

	// Simple wrapper over encryption part
	var encrypt = function(key, data) {
		return kemo.encryption.encrypt(saltKey(key), data);
	};

	// Simple wrapper over decryption part
	var decrypt = function(key, data) {
		var decryptedData = "";
		try {
			decryptedData = kemo.encryption.decrypt(saltKey(key), data);
		} catch (err) {
			// Encryption fails for whatever reason.. just log it
			console.error("Unexpected error when decrypting received message.", err)
			// Report decryption error
			kemo.core.report_error({
				message : "Unexpected error when decrypting received message.",
				error : err
			});
		}
		return decryptedData;
	};

	// Resolves url
	var resolveUrl = function(key) {
		var host = "";
		if (location.protocol === "http:") {
			host = "ws://" + location.hostname + ":" + location.port;
		} else {
			host = "wss://" + location.hostname  + ":" + location.port;
		}
		return host + "/messaging/" + keyToAddress(key);
	};

	// Writes audit log messages into browser log
	var auditLog = function(code, messaging, msg) {
		if (kemo.client.comm.config.auditEnabled && console && console.log) {
			var messagingInfo = "null";
			if (messaging && messaging.ws) {
				messagingInfo = "state: ";
				if (messaging.ws.readyState === WebSocket.CONNECTING) {
					messagingInfo += "CONNECTING";
				} else if (messaging.ws.readyState === WebSocket.OPEN) {
					messagingInfo += "OPEN";
				} else if (messaging.ws.readyState === WebSocket.CLOSING) {
					messagingInfo += "CLOSING";
				} else if (messaging.ws.readyState === WebSocket.CLOSED) {
					messagingInfo += "CLOSED";
				} else {
					messagingInfo += "UNKNOWN";
				}
			}
			var addMsg = msg !== undefined ? " " + msg : "";
			console.log(new Date().toUTCString() + " " + (code !== undefined ? code : "UNKNOWN") + " " + messagingInfo + addMsg);
		}
	};

	// Object representing messaging API
	kemo.client.comm.Messaging = function(key) {
		var self = this;
		// Messaging key
		self.key = key;
		// WebSocket reference
		self.ws = null;

		// Functions executed on changed connection state
		self.onReadyStateFns = [];

		var _executeOnFunctions = function(execFns, readyState){
		    if(execFns && Array.isArray(execFns)){
                for(var key in execFns){
                    var fnObj = execFns[key];
                    if(typeof(fnObj) === 'function'){
                        try{
                            fnObj(readyState);
                        }catch(err){
                            // Simple drown error and continue
                        }
                    }
                }
		    }
		};

		// Connect API to server websocket
		self.connect = function(key, afterOnOpen) {
			auditLog("CONNECT", self);
			try {
				self.key = key;
				// Close existing web socket connection
				if (self.ws && self.ws.readyState !== WebSocket.CLOSED) {
					auditLog("CONNECT_CLOSING", self);
					self.ws.onmessage = function() {
					};
					if (self.ws.readyState === WebSocket.OPEN) {
						try {
							self.ws.close();
						} catch (err) {
							// Let closing silently fail
							auditLog("CLOSING_ERROR", self);
						}
					} else if (self.ws.readyState === WebSocket.CONNECTING) {
						// Plan delayed close in future for current websocket
						var closeWs = self.ws;
						setTimeout(function() {
							if (self.ws.readyState !== WebSocket.CLOSING && self.ws.readyState !== WebSocket.CLOSED) {
								try {
									closeWs.close();
								} catch (err) {
									// Let closing silently fail
									auditLog("DLY_CLOSING_ERROR", self);
								}
							}
						}, 5000);
					}
				}
				// Open new connection with given key
				self.ws = new WebSocket(resolveUrl(key));
				auditLog("CONNECT_NEWSOCKET", self);
				self.ws.onopen = function() {
					auditLog("CONNECT_NEWSOCKET_OPEN", self);
					self.ws.onmessage = function(event) {
						self.onmessage(decrypt(self.key, event.data));
					};
					if (afterOnOpen) {
						afterOnOpen();
					}
					_executeOnFunctions(self.onReadyStateFns, self.ws.readyState);
				};
				// On error perform reconnect
				self.ws.onerror = function(err) {
					auditLog("ONERROR", self);
					setTimeout(self.reconnect, kemo.client.comm.config.onErrorReconnect);
					// Report decryption error
					kemo.core.report_error({
						message : "WebSocket connection error.",
						error : err
					});
					_executeOnFunctions(self.onReadyStateFns, self.ws.readyState);
				};
			} catch (err) {
				// Do not propagate up... silently fails.
				auditLog("UNEXPECTEDERROR", self, err);
				// Report decryption error
				kemo.core.report_error({
					message : "Unexpected error when connecting to server.",
					error : err
				});
			}
		};

		// Called when message is received and decrypted
		self.onmessage = function(message) {
		};

		// Sends message
		self.send = function(key, message) {
			if (self.ws === null || self.ws.readyState !== WebSocket.OPEN || self.key !== key) {
				auditLog("SEND_NEEDCONNECT", self);
				self.connect(key, function() {
					self.ws.send(encrypt(key, message));
				});
			} else {
				auditLog("SEND", self);
				self.ws.send(encrypt(key, message));
			}
		};

		// Checks connection and reconnects client when needed
		self.connectionCheck = function() {
			auditLog("CHECK", self);
			if (self.ws && (self.ws.readyState !== WebSocket.CONNECTING && self.ws.readyState !== WebSocket.OPEN)) {
				self.reconnect();
			}
			// Plan next connection check round
			setTimeout(self.connectionCheck, kemo.client.comm.config.checkTimeout);
		};

		// Disconnect current connection if exists and creates new one.
		self.reconnect = function() {
			auditLog("RECONNECT", self);
			// Check that client exists
			if (self.ws) {
				if (self.ws.readyState === WebSocket.CONNECTING || self.ws.readyState === WebSocket.OPEN) {
					auditLog("RECONNECT_OPEN/CONNECTING", self);
					// Unset message event handler
					self.ws.onmessage = function() {
					};
					// Try to close current connection
					self.ws.close();
					// On close try to connect to server again
					self.ws.onclose = function() {
					    _executeOnFunctions(self.onReadyStateFns, self.ws.readyState);
						self.connect(self.key, null);
					};
				} else if (self.ws.readyState === WebSocket.CLOSING) {
					auditLog("RECONNECT_CLOSING", self);
					// Unset message event handler
					self.ws.onmessage = function() {
					};
					// On close try to connect to server again
					self.ws.onclose = function() {
					    _executeOnFunctions(self.onReadyStateFns, self.ws.readyState);
						self.connect(self.key, null);
					};
				} else if (self.ws.readyState === WebSocket.CLOSED) {
					auditLog("RECONNECT_CLOSED", self);
					_executeOnFunctions(self.onDisconnectFns);
					// Ok just create new connection
					self.connect(self.key, null);
				}
			} else {
				auditLog("RECONNECT_NEWCONNECTION", self);
				self.connect(self.key, null);
			}
		};

		return self;
	}

	return kemo;
}(kemo || {});
// Kemo simple messaging client
var kemo = function(kemo) {
	kemo.client = kemo.client || {};
	kemo.client.view = kemo.client.view || {};
	kemo.client.view.util = kemo.client.view.util || {};

	var view = kemo.client.view;
	var comm = kemo.client.comm;
	var data = kemo.client.data;
	var util = kemo.client.view.util;

	// Mounts kemo chat UI component
	view.mountKemoChat = function(selector, page) {
		return riot.mount(selector, {
			page : page
		})[0];
	};

	riot.tag2('kemo-chat', '\
			<div>\
				<div class="row">\
					<input type="password" kemo-type="key" value={key} onchange="{onKeyChanged}" onblur="{onKeyChanged}" class="key-field"/>\
				</div>\
				<div class="row">\
					<div kemo-type="messages" class="messages-box">\
						<chat-line each={messages.items}></chat-line>\
					</div>\
				</div>\
				<div class="row">\
					<div class="ten columns">\
						<input type="text" kemo-type="message" class="message-box" onkeypress="{onMessageKeyPress}"/>\
					</div>\
					<div class="two columns">\
						<button class="send-btn button-primary" kemo-type="send-btn" onclick="{onSendMessage}">Send</button>\
					</div>\
				</div>\
			\</div>', '', '', function(opts) {
		var self = this;
		self.page = opts.page;
		// Element id
		self.id = self.root.getAttribute("id");
		// Default encryption key
		self.key = self.root.getAttribute("key");
		// Max messages on screen
		self.max_messages = 15;
		// Messages queue
		self.messages = new util.MiniQueue(self.max_messages, {
			empty : true
		});
		// Send messages markers
		self.sentMessages = new util.MiniQueue(self.max_messages);

		// Handle on mount event to get info from rendered component
		self.on('mount', function() {
			// Get references to chat window elements
			self.keyInp = self.root.querySelectorAll('input[kemo-type="key"]')[0];
			self.messagesDiv = self.root.querySelectorAll('div[kemo-type="messages"]')[0];
			self.messageInp = self.root.querySelectorAll('input[kemo-type="message"]')[0];
			self.sendBtn = self.root.querySelectorAll('button[kemo-type="send-btn"]')[0];
			// Try to connection when UI is ready
			self.page.trigger('messaging-connect', newConnectEvent());
		});

		// Creates new send event
		var newSendEvent = function() {
			return {
				source : self,
				key : self.keyInp.value,
				message : self.messageInp.value
			};
		}

		// Creates required new connection event
		var newConnectEvent = function() {
			return {
				source : self,
				key : self.keyInp.value
			};
		};

		// Fills given content with given content
		var fillWith = function(array, count, content) {
			for (var i = 0; i < count; i++) {
				array.add(content);
			}
		};

		// Returns true when message was resolved as sent
		var checkIsSent = function(message) {
			var isSent = false;
			if (message) {
				var idx = self.sentMessages.items.indexOf(message);
				if (idx > -1) {
					isSent = true;
					self.sentMessages.items.splice(idx, 1);
				}
			}
			return isSent;
		};

		// Marks message as sent
		var markSent = function(message) {
			self.sentMessages.add(message);
		};

		// Performs message send operation
		var doSendMessage = function() {
			// Prepare and publish event
			var sendEvent = newSendEvent();
			self.page.trigger('messaging-send', sendEvent);
			// Mark message content as sent
			markSent(sendEvent.message);
			// Clear message input value
			self.messageInp.value = "";
		};

		// Called when clicked on Send button
		self.onSendMessage = function(e) {
			doSendMessage();
		}.bind(this);

		// Called when pressed button within message input
		self.onKeyChanged = function(e) {
			self.page.trigger('ui-key-changed', self.keyInp.value);
			return true;
		}.bind(this);

		// Called when pressed button within message input
		self.onMessageKeyPress = function(e) {
			var key = e.which || e.keyCode;
			if (key === 13) {
				doSendMessage();
			}
			return true;
		}.bind(this);

		// Called on received message
		self.on('ui-received', function(e) {
			// Resolve if received message was sent by us
			var isSentMessage = checkIsSent(e);
			// Update data model
			self.messages.add({
				sent : isSentMessage,
				message : e
			});
			
			// Update component
			self.update();

			// Scroll to messages box bottom
			self.messagesDiv.scrollTop = self.messagesDiv.scrollHeight;
			
			// Notify page that new received message was displayed
			if(!isSentMessage){
				self.page.trigger('ui-received-new');
			}
		});

	}, '{ }');

	riot.tag('chat-line', '\
			<div if={!empty} class="{sent: sent}">{message}</div>\
			<div if={empty} >&nbsp;</div>\
			', '', '', function(opts) {
	}, '{ }');

	// UTILS
	// Simple data queue
	util.MiniQueue = function(size, defaultValue) {
		this.size = size || 20;
		this.items = [];
		if (defaultValue) {
			for (var i = 0; i < size; i++) {
				this.items.push({
					empty : true
				});
			}
		}
		return this;
	};

	util.MiniQueue.prototype.add = function(item) {
		this.items.push(item);
		if (this.items.length > this.size) {
			this.items.shift();
		}
		return this;
	};

	// Helper search by hash function
	var containsWithHash = function(items, hash) {

	};

	return kemo;
}(kemo || {});
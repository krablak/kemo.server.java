// Kemo simple messaging client
var kemo = function(kemo) {
	// Kemo client module
	kemo.client = kemo.client || {};

	// 'Import' of related submodules
	var view = kemo.client.view;
	var comm = kemo.client.comm;
	var ext = kemo.client.ext;

	// Sends event about chat focus to parent window if exists
	var focusOnParent = function() {
		// In case that exist parent window propagate event up
		if (window.parent) {
			window.parent.postMessage('ui-focus', "*");
		}
	};

	// Initializes kemo client UI
	var init = function() {
		// Make module observable to handle events from inner components
		riot.observable(kemo.client);

		// Get reference to chat component
		var chatUi = view.mountKemoChat("#public-chat", kemo.client);
		// Create communication API instance
		var messaging = new comm.Messaging(chatUi.key);

		// Helper instance of last known readyState
		kemo.client.lastReadyState = -1

		// Add messaging events handlers
		messaging.onReadyStateFns.push(function(readyState){
		    var stateMsg = "[";
		    var curDate = new Date();
		    stateMsg = stateMsg +  curDate.getHours() + ":" + curDate.getMinutes() + ":" + curDate.getSeconds() + "]";
		    var msgEvent = { type:"info", message:"" };
		    if(readyState === 0){
                msgEvent.message = stateMsg + " Connecting...";
		    }else if(readyState === 1){
		        msgEvent.message = stateMsg + " Connected!";
		    }else if(readyState === 2){
		        msgEvent.type = "warn";
                msgEvent.message = stateMsg + " Ups! Closing connection...";
            }else if(readyState === 3){
                msgEvent.type = "warn";
                msgEvent.message = stateMsg + " Doh! Connection closed :(";
            }
            if(kemo.client.lastReadyState !== readyState){
                chatUi.trigger('ui-system-received', msgEvent);
            }
            kemo.client.lastReadyState = readyState;
		});

		// System notification extension
        var sysNotifExt = new ext.SystemNotification();

		// Title notification extension
   		var titleNotifExt = new ext.TitleNotification();

		// Handle actions between communication and UI
		messaging.connect(chatUi.key, function() {
			chatUi.trigger('update');
		});
		messaging.onmessage = function(message) {
			chatUi.trigger('ui-received', message);
		};
		kemo.client.on('messaging-send', function(e) {
			messaging.send(e.key, e.message);
		});
		kemo.client.on('ui-key-changed', function(key) {
			messaging.connect(key);
		});
		kemo.client.on('messaging-connect', function(e) {
			messaging.connect(e.key);
		});
		kemo.client.on('ui-received-new', function(e) {
			var propEvent = {
				code : 'ui-received-new',
				data : e
			};
			titleNotifExt.trigger('ui-received-new', propEvent);
			sysNotifExt.trigger('ui-received-new', e.data);
		});
		// On window focus inform parent window
		window.addEventListener('focus', focusOnParent);
		window.addEventListener('blur', focusOnParent);

		// Plan next connection check
		setTimeout(messaging.connectionCheck, 5000);

		// Publish references to client module
		kemo.client.chatUi = chatUi;
		kemo.client.messaging = messaging;
	};

	// Run client module initialization on page load
	kemo.core.ready(init);

	return kemo;
}(kemo || {});
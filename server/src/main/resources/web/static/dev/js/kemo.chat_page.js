/**
 * Provides functions related to kemo.rocks chat page.
 */
var kemo = function(kemo) {
	kemo.core = kemo.core || {};
	kemo.core.chat_page = kemo.core.chat_page || {};
	var ext = kemo.client.ext;
	var page = kemo.core.chat_page;

	// Initializes kemo.rocks welcome page UI
	var init = function() {
		// Title notification extension
		var titleNotifExt = new ext.TitleNotification();
		// Listen to event propagated up from kemo chat iframe
		window.addEventListener('message', function(e) {
			if ('ui-received-new' === e.data) {
				titleNotifExt.trigger('ui-received-new');
			}
		}, false);
	};

	// Run client module initialization on page load
	kemo.core.ready(init);

	return kemo;
}(kemo || {});

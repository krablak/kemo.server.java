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
		// System notification extension
		var sysNotifExt = new ext.SystemNotification();
		// Title notification extension
		var titleNotifExt = new ext.TitleNotification();
		// Listen to event propagated up from kemo chat iframe
		window.addEventListener('message', function(e) {
			if ('ui-received-new' === e.data.code) {
				titleNotifExt.trigger('ui-received-new');
				sysNotifExt.trigger('ui-received-new', e.data);
			}
		}, false);
		// Warn user on page leave
		window.onbeforeunload = function() {
			return 'Are you sure you want to leave? All settings and conversation will be lost.';
		};
	};

	// Run client module initialization on page load
	kemo.core.ready(init);

	return kemo;
}(kemo || {});

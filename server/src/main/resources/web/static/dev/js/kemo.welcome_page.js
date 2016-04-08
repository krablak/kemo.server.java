/**
 * Provides functions relate to kemo.rocks welcome page.
 */
var kemo = function(kemo) {
	kemo.core = kemo.core || {};
	kemo.core.welcome_page = kemo.core.welcome_page || {};
	var ext = kemo.client.ext;
	var page = kemo.core.welcome_page;

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

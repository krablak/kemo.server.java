//  Kemo core support functions
var kemo = function(kemo) {
	kemo.core = kemo.core || {};

	kemo.core.ready = function(fn) {
		if (document.readyState != 'loading') {
			fn();
		} else {
			document.addEventListener('DOMContentLoaded', fn);
		}
	}
	
	return kemo;
}(kemo || {});
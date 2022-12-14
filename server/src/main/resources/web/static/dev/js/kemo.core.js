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

	kemo.core.base64_encode = function(str) {
		return window.btoa(unescape(encodeURIComponent(str)));
	}

	var allowedBase64Chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

	kemo.core.base64_decode = function(str) {
		try {
			return decodeURIComponent(escape(window.atob(str)));
		} catch (e) {
			// Try fix(recreate) string and decode again to remove base64 not
			// supported chars
			var replacedStr = "";
			for (var i = 0; i < str.length; i++) {
				var curByte = str[i];
				if (allowedBase64Chars.indexOf(curByte) !== -1) {
					replacedStr = replacedStr + curByte
				}
			}
			return decodeURIComponent(escape(window.atob(replacedStr)));
			;
		}
	}

	// Send error report to kemo server
	kemo.core.report_error = function(details) {
		var report = {
			details : details
		};
		for ( var k in navigator) {
			report[k] = navigator[k];
		}
		var http = new XMLHttpRequest();
		var url = "/error/report-agent";
		http.open("POST", url, true);
		http.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');
		http.send(JSON.stringify(report));
	};

	return kemo;
}(kemo || {});
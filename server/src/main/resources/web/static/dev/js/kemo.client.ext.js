// Various kemo client extensions
var kemo = function(kemo) {
	kemo.client = kemo.client || {};
	kemo.client.ext = kemo.client.ext || {};
	var ext = kemo.client.ext;

	ext.SystemNotification = function() {
		var self = this;
		// Check that notification are available in browser
		if ("Notification" in window) {
			// Make extension observable
			riot.observable(self);

			// Possible notification titles
			var ntfTitles = [ 'Hello!', 'Hey!', 'Hallo!', 'Hullo!', 'Halloo!', 'Hollo!', 'Psst!', 'Dude!' ];

			// Request notification
			Notification.requestPermission();

			// Handle event about received message
			self.on('ui-received-new', function(e) {
				var ntfData = {
					body : 'New message received',
					icon : '/static/icons/Icon-76@2x.png'
				};
				var randomTitle = ntfTitles[Math.floor(Math.random() * ntfTitles.length)];
				var notification = new Notification(randomTitle, ntfData);
				setTimeout(notification.close.bind(notification), 5000);
			});
		}

		return self;
	};

    // Fills random nick into chat nick field
	ext.RandomNick = function() {
	    var self = this;
        self.generate = function(){
                var nicks = [ 'Greg', 'Gergely', 'Dave', 'Robin', 'Sasha', 'Zim', 'Dude','Kim','Chucpe','Jesus','Hugo','uu5','UMFIoTRCA','Bilbo','Frodo','Gandalf','Smaug','Elrond','Gollum','Thorin','Fili','Kili','Balin','Dwalin','Oin','Gloin','Dori','Nori','Bifur','Bofur','Bombur','Beorn','Bard','Bert','Bolg','Golfimbul','Dain'];
                var randomNick = nicks[Math.floor(Math.random() * nicks.length)];
                var usrNameFld = document.querySelector("input[kemo-type='username']");
                if(usrNameFld){
                    usrNameFld.value = randomNick;
                }
        };
	    return self;
    };


	// Extension adding star to the title on received message
	ext.TitleNotification = function() {
		var self = this;
		// Make extension observable
		riot.observable(self);
		// Notification text signs
		var NOTIF_SIGN_0 = " (-.-)";
		var NOTIF_SIGN_1 = " (o.o)";
		var switchMode = true;
		// Time out when title notification will be hidden
		var TIMEOUT = 10 * 60 * 1000;
		// Get title element
		self.titleElem = document.getElementsByTagName("title")[0];
		// Received notification requests count
		self.notifCount = 0;

		// In case of switch mode insert default notification mark
		if (switchMode) {
			self.titleElem.text = self.titleElem.text + NOTIF_SIGN_0;
		}

		// Handle event about received message
		self.on('ui-received-new', function() {
			// Increase active notification count
			self.notifCount++;
			if (switchMode) {
				// Add notification sign when it's not already switched
				if (!self.titleElem.text.endsWith(NOTIF_SIGN_1)) {
					self.titleElem.text = self.titleElem.text.replace(NOTIF_SIGN_0, NOTIF_SIGN_1);
				}
			} else {
				// Add notification sign when it's not already resent
				if (!self.titleElem.text.endsWith(NOTIF_SIGN_0)) {
					self.titleElem.text = self.titleElem.text + NOTIF_SIGN_0;
				}
			}
			// Plan notification hide
			setTimeout(self.tryHide, TIMEOUT);
		});

		// Timed notification hide function
		self.tryHide = function() {
			self.notifCount--;
			if (self.notifCount === 0) {
				self.hide();
			}
		};

		// Hides notification sign from title
		self.hide = function() {
			if (switchMode) {
				if (self.titleElem.text.endsWith(NOTIF_SIGN_1)) {
					self.titleElem.text = self.titleElem.text.replace(NOTIF_SIGN_1, NOTIF_SIGN_0);
				}
			} else {
				if (self.titleElem.text.endsWith(NOTIF_SIGN_0)) {
					self.titleElem.text = self.titleElem.text.substring(0, (self.titleElem.text.length) - NOTIF_SIGN_0.length);
				}
			}
		}

		// Add listener on window/tab activation to hide notification sign
		window.addEventListener('focus', self.hide);
		window.addEventListener('blur', self.hide);

		// Hide notification when child chat is focused
		window.addEventListener('message', function(e) {
			if ('ui-focus' === e.data) {
				self.hide();
			}
		}, false);

		return self;
	};

	return kemo;
}(kemo || {});
jQuery(document).ready(function($) {
	console.log("ready");
	$('.view-faq .views-field-title').each(function() {
		var self = $(this);
		var answer = self.next();
		answer.show().css('height','auto');
		//answer.slideUp();
		//var state = false;
		console.log(self);
		self.click(function() {
			//console.log("click");
			//console.log(self);
			//console.log(self.parent());
			//state = !state;
			answer.slideToggle();
			self.parent().toggleClass('active');
		});
	});
});
;

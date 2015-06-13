var sortable = $('#${componentId} > ul');
sortable.sortable({
  handle: ".panel-heading",
  update: function( event, ui ) {
	  var serialized = sortable.children("li").map(function(i, e){return e.id}).toArray();
	  ${callBackScript}
  },
  disabled: ${disabled},
  placeholder: "dashboard-placeholder",
  forcePlaceholderSize: true
});
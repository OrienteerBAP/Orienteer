
'use strict';

var currentZindex = 1000;
var maxZindex     = currentZindex + 100000;

function showTab(id) {
    $("#" + id).tab('show');
}

function enableDraggable(id) {
    var draggable = $('#' + id).draggable({
        cursor: "move",
        containment: "document",
        zIndex: maxZindex,
        stop: function(event, ui) {
            if (currentZindex === maxZindex) currentZindex = 1000;
            else currentZindex++;
            draggable.css("z-index", currentZindex);
        }
    });
    var currentPosition = $(window).width() - draggable.width();
    if (!(currentPosition > draggable.offset().left)) {
        draggable.offset({
            left: currentPosition - 15
        });
    }
}
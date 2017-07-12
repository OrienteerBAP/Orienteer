
'use strict';

function showTab(id) {
    $("#" + id).tab('show');
}

function enableDraggable(id) {
    $('#' + id).draggable({
        cursor: "move",
        containment: "document",
        zIndex: 100000
    });
}
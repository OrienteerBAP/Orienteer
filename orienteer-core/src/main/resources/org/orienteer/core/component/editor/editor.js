
function editorInit(id, handleId, config) {
    var textArea = $('#' + id).get(0);
    var cm = CodeMirror.fromTextArea(textArea, config);
    if (!config.readOnly) {
        cm.on('change', function (codeMirror, obj) {
            codeMirror.save();
        });
    }
    enableChangeHeight(handleId, cm);
}

function enableChangeHeight(handleId, cm) {
    var MIN_HEIGHT = 200;
    var handle = $('#' + handleId);

    var mouseStartY;
    var cmCurrentHeight;

    var onDrag = function (e) {
        cm.setSize(null, Math.max(MIN_HEIGHT, cmCurrentHeight + e.pageY - mouseStartY));
    };

    var onRelease = function (e) {
        $(document).off('mousemove', onDrag);
        $(window).off('mouseup', onRelease);
    };

    handle.on('mousedown', function (e) {
        mouseStartY = e.pageY;
        cmCurrentHeight = cm.getWrapperElement().offsetHeight;
        $(document).on('mousemove', onDrag);
        $(window).on('mouseup', onRelease);
    });
}

function switchFullScreen(cm) {
    var enable = !cm.getOption('fullScreen');
    if (enable) {
        enableFullScreen(cm);
    } else disableFullscreen(cm);
}

function enableFullScreen(cm) {
    $('.metismenu').hide();
    $('.sidebar-search').hide();
    $('.navbar.navbar-default.navbar-fixed-top').hide();
    cm.setOption('fullScreen', true);
}

function disableFullscreen(cm) {
    $('.metismenu').show();
    $('.sidebar-search').show();
    $('.navbar.navbar-default.navbar-fixed-top').show();
    cm.setOption('fullScreen', false);
}
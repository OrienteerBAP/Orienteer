
function editorInit(id, config) {
    var textArea = $('#' + id).get(0);
    var cm = CodeMirror.fromTextArea(textArea, config);
    if (!config.readOnly) {
        cm.on('change', function (codeMirror, obj) {
            codeMirror.save();
        });
    }
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
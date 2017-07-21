
function editorInit(id, config) {
    var textArea = $('#' + id).get(0);
    var cm = CodeMirror.fromTextArea(textArea, config);
    if (config.edit) {
        cm.on('change', function (codeMirror, obj) {
            codeMirror.save();
        });
    }
}
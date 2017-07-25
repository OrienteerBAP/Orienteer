function initJQDatepicker(inputId, options) {
    $('#' + inputId).datepicker(options);
}

function initDateMarkup(id) {
    $('#' + id + " span:first")
        .css('display', 'inline-flex')
        .css('flex-wrap', 'nowrap');
}
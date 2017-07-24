function initJQDatepicker(inputId, language, dateFormat) {
    $.fn.datepicker.defaults.format = dateFormat;
    $.fn.datepicker.defaults.autoclose = true;
    $.fn.datepicker.defaults.language = language;
    $.fn.datepicker.defaults.orientation = 'bottom';
    $.fn.datepicker.weekStart = 1;
    var datepicker = $('#' + inputId).datepicker();
    var zIndex;
    datepicker.on('show', function () {
        zIndex = datepicker.css('z-index');
        datepicker.css('z-index', '99999');
    });
    datepicker.on('hide', function () {
       if (zIndex !== undefined)
           datepicker.css('z-index', zIndex);
    });
}

function initDateMarkup(id) {
    $('#' + id + " span:first")
        .css('display', 'inline-flex')
        .css('flex-wrap', 'nowrap');
}
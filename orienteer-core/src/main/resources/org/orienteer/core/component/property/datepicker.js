function initJQDatepicker(inputId, language, dateFormat) {
    // $.fn.datepicker.defaults.format = dateFormat;
    $.fn.datepicker.defaults.autoclose = true;
    $.fn.datepicker.defaults.language = language;
    $.fn.datepicker.weekStart = 1;
    $('#' + inputId).datepicker();
}
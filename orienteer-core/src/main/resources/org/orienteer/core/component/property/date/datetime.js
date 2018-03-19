'use strict';

function initJQDatepicker(inputId, options) {
    console.dir(options);
    const $picker = $('#' + inputId);
    $picker.datepicker(options);
    $('#' + inputId + '+.input-group-addon').click(() => $picker.datepicker('show'));
}
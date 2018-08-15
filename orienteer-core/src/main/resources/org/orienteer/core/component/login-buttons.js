function loginOnEnter(id) {
    $(document).keypress(function (e) {
        if (e.keyCode === 13) {
            $('#' + id).click();
        }
    });
}
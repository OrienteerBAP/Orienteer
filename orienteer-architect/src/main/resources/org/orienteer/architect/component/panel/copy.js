function addAutoCopyOnElement(inputId, buttonId) {
    $('#' + buttonId).click(function(e) {
        e.preventDefault();
        var area = $('#' + inputId).get(0);
        copyToClipboard(area.value);
    });

    function copyToClipboard(str) {
        var el = document.createElement('textarea');
        el.value = str;
        el.setAttribute('readonly', '');
        el.style.position = 'absolute';
        el.style.left = '-9999px';
        document.body.appendChild(el);
        el.select();
        document.execCommand('copy');
        document.body.removeChild(el);
    }
}
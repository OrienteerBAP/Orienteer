
var OArchitectOType = {
    types: ['BOOLEAN', 'INTEGER', 'SHORT', 'LONG', 'FLOAT', 'DOUBLE', 'DATETIME',
        'STRING', 'BINARY', 'EMBEDDED', 'EMBEDDEDLIST', 'EMBEDDEDSET', 'EMBEDDEDMAP',
        'LINK', 'LINKLIST', 'LINKSET', 'LINKMAP', 'BYTE', 'TRANSIENT', 'DATE', 'CUSTOM',
        'DECIMAL', 'LINKBAG', 'ANY'],
    linkTypes: ['LINK', 'LINKLIST', 'LINKSET', 'LINKMAP', 'LINKBAG'],

    get: function (index) {
        return this.types[index];
    },

    getAsPrettyString: function (index) {
        var result = this.types[index];
        if (result) {
            var start = result.charAt(0);
            result = result.toLowerCase();
            result = start + result.slice(1);
        }
        return result;
    },

    contains: function (type) {
        return this.types.indexOf(type.toUpperCase()) > -1;
    },

    getIndexByValue: function (value) {
        return this.types.indexOf(value.toUpperCase());
    },

    size: function () {
        return this.types.length;
    },

    isLink: function (type) {
        return this.linkTypes.indexOf(type) > -1;
    },

    clone: function () {
        return mxUtils.clone(this);
    }
};
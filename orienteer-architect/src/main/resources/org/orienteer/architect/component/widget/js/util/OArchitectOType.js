/**
 * Utility class which represents OType from OrientDB in {@link OArchitectEditor}
 */
var OArchitectOType = {
    types: ['BOOLEAN', 'INTEGER', 'SHORT', 'LONG', 'FLOAT', 'DOUBLE', 'DATETIME',
        'STRING', 'BINARY', 'EMBEDDED', 'EMBEDDEDLIST', 'EMBEDDEDSET', 'EMBEDDEDMAP',
        'LINK', 'LINKLIST', 'LINKSET', 'LINKMAP', 'BYTE', 'TRANSIENT', 'DATE', 'CUSTOM',
        'DECIMAL', 'LINKBAG', 'ANY'],
    linkTypes: ['LINK', 'LINKLIST', 'LINKSET', 'LINKMAP', 'LINKBAG'],

    BOOLEAN: 'BOOLEAN',
    INTEGER: 'INTEGER',
    SHORT: 'SHORT',
    LONG: 'LONG',
    FLOAT: 'FLOAT',
    DOUBLE: 'DOUBLE',
    DATETIME: 'DATETIME',
    STRING: 'STRING',
    BINARY: 'BINARY',
    EMBEDDED: 'EMBEDDED',
    EMBEDDEDLIST: 'EMBEDDEDLIST',
    EMBEDDEDSET: 'EMBEDDEDSET',
    EMBEDDEDMAP: 'EMBEDDEDMAP',
    LINK: 'LINK',
    LINKLIST: 'LINKLIST',
    LINKSET: 'LINKSET',
    LINKMAP: 'LINKMAP',
    BYTE: 'BYTE',
    TRANSIENT: 'TRANSIENT',
    DATE: 'DATE',
    CUSTOM: 'CUSTOM',
    DECIMAL: 'DECIMAL',
    LINKBAG: 'LINKBAG',
    ANY: 'ANY',

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

    isMultiValue: function (type) {
        var result = false;
        if (this.contains(type)) {
            result = type === this.LINKLIST || type === this.LINKMAP || type === this.LINKSET || type === this.LINKBAG
            || type === this.EMBEDDEDLIST || type === this.EMBEDDEDMAP || type === this.EMBEDDEDSET;
        }
        return result;
    },

    clone: function () {
        return mxUtils.clone(this);
    }
};
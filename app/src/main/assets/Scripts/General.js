function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");

    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"), results = regex.exec(location.search);

    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function getReferalParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");

    var regex = new RegExp("[\\?&]" + name + "=([^#]*)"), results = regex.exec(location.search);

    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function bin2String(array) {
    var result = "";
    for (var i = 0; i < array.length; i++) {
        result += String.fromCharCode(parseInt(array[i], 2));
    }
    return result;
}

function _arrayBufferToBase64(buffer) {
    var binary = '';
    var bytes = new Uint8Array(buffer);
    var len = bytes.byteLength;
    for (var i = 0; i < len; i++) {
        binary += String.fromCharCode(bytes[i]);
    }
    return window.btoa(binary);
}

function restrictStringLength(Value, MaxLength)
{
    if (Value.length > MaxLength) {
        return Value.substring(0, MaxLength) + '...';
    }
    else
    {
        return Value;
    }
}

function FillSelect(sources, target) {
    var Select = document.getElementById(target);

    var Option;

    for (i = 0; i < sources.length; i++) {
        Option = document.createElement("option");
        Option.appendChild(document.createTextNode(sources[i].Text));
        Option.value = sources[i].Value;
        Select.appendChild(Option);
    }
}

function FillSelectAndSetDefault(sources, target, DefaultValue) {
    var Select = document.getElementById(target);

    var Option;

    for (i = 0; i < sources.length; i++) {
        Option = document.createElement("option");
        Option.appendChild(document.createTextNode(sources[i].Text));
        Option.value = sources[i].Value;

        if (DefaultValue == sources[i].Value) {
            Option.selected = true;
        }

        Select.appendChild(Option);
    }
}

function FillSelectAndSetDefaultText(sources, target, DefaultText) {
    var Select = document.getElementById(target);

    var Option;

    for (i = 0; i < sources.length; i++) {
        Option = document.createElement("option");
        Option.appendChild(document.createTextNode(sources[i].Text));
        Option.value = sources[i].Value;

        if (DefaultText == sources[i].Text) {
            Option.selected = true;
        }

        Select.appendChild(Option);
    }
}

function ClearSelect(selectbox) {
    var i;

    for (i = selectbox.options.length - 1; i >= 0; i--) {
        selectbox.remove(i);
    }
}

function BuildSelect(sources, target, DefaultValue) {

    var Option;

    for (z = 0; z < sources.length; z++) {
        Option = document.createElement("option");
        Option.appendChild(document.createTextNode(sources[z].Text));
        Option.value = sources[z].Value;

        if (DefaultValue == sources[z].Value) {
            Option.selected = true;
        }

        target.appendChild(Option);
    }
}

function CreateOption(target, text, value) {
    var Option = document.createElement("option");

    Option.appendChild(document.createTextNode(text));
    Option.value = value;

    target.appendChild(Option);
}

function round(value, decimals) {
    return Number(Math.round(value + 'e' + decimals) + 'e-' + decimals);
}

function Exists(src) {
    var returnValue;

    try {
        returnValue = src.value;
    }
    catch (ex) {
        returnValue = '';
    }

    return returnValue;
}

function DateIsValid(dateToCheck) {
    var returnValue;


    if (dateToCheck != '') {
        try {
            returnValue = new Date(moment(dateToCheck, ["DD/MM/YYYY HH:mm"]));
        }
        catch (ex) {
            returnValue = new Date(moment('01/01/1900', ["DD/MM/YYYY HH:mm"]));
        }
    }
    else {
        returnValue = new Date(moment('01/01/1901', ["DD/MM/YYYY HH:mm"]));
    }

    return returnValue;
}
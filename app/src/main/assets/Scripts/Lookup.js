function LookupPart()
{
    var EditHUD = document.createElement("div");

    EditHUD.className = "LookupPart";
    EditHUD.setAttribute('id', 'LookupHUD');
    //EditHUD.setAttribute('style', 'height: 300px; margin-top: -180px;');

    var ItemDescription = document.createElement("div");

    ItemDescription.className = "Title";
    ItemDescription.appendChild(document.createTextNode("Lookup Part"));

    EditHUD.appendChild(ItemDescription);

    var CategoryContainer = document.createElement("div");
    CategoryContainer.className = "Categories";
    CategoryContainer.id = "Categories";
    //TemplateContainer.setAttribute('style', 'height: 185px; margin-top: 10px;');

    //Dropdown Lists

    //Category's

    var Categories = document.createElement("div");
    Categories.setAttribute('id', 'CategorySelection');

    ItemInput = document.createElement("select");
    ItemInput.setAttribute('id', 'ItemCategory_1');
    ItemInput.setAttribute('Position', '1');
    ItemInput.setAttribute('style', 'width: 390px; margin-bottom: 10px;');
    ItemInput.className = "TextBox";
    ItemInput.setAttribute('onchange', 'GetSubCategories(this.value, this);');
    Categories.appendChild(ItemInput);

    CategoryContainer.appendChild(Categories);

    //Product Detail

    var ProductDetail = document.createElement("div");
    ProductDetail.className = "ProductDetails";
    ProductDetail.id = "ProductProperties";
    ProductDetail.appendChild(document.createTextNode("Select a product from the list to see more information"));

    CategoryContainer.appendChild(ProductDetail);


    EditHUD.appendChild(CategoryContainer);



    var ProductContainer = document.createElement("div");
    ProductContainer.className = "Products";
    ProductContainer.id = "Products";
    //TemplateContainer.setAttribute('style', 'height: 185px; margin-top: 10px;');

    EditHUD.appendChild(ProductContainer);


    //Buttons

    var ItemButtons = document.createElement("div");

    ItemButtons.className = "Buttons";
    
    ItemButton = document.createElement("button");
    ItemButton.className = "ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only";
    ItemButton.setAttribute('onClick', 'SetLookupData();');
    ItemButton.setAttribute('style', 'margin-right: 10px;');

    ItemButtonText = document.createElement("span");
    ItemButtonText.className = "ui-button-text";
    ItemButtonText.appendChild(document.createTextNode("OK"));
    ItemButton.appendChild(ItemButtonText);

    ItemButtons.appendChild(ItemButton);



    ItemButton = document.createElement("button");
    ItemButton.className = "ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only";
    ItemButton.setAttribute('onClick', 'CancelLookup();');

    ItemButtonText = document.createElement("span");
    ItemButtonText.className = "ui-button-text";
    ItemButtonText.appendChild(document.createTextNode("Cancel"));
    ItemButton.appendChild(ItemButtonText);

    ItemButtons.appendChild(ItemButton);


    EditHUD.appendChild(ItemButtons);

    document.body.appendChild(EditHUD);
    
    PageMethods.GetProductCategories(0, function (result) {
        FillSelect(result, 'ItemCategory_1');

        //Does it have Sub-Categories

        GetSubCategories(parseInt(document.getElementById("ItemCategory_1").value), document.getElementById("ItemCategory_1"));

        //Get Products


    });

}

function CancelLookup() {
    document.body.removeChild(document.getElementById("LookupHUD"));
}

function GetSubCategories(CategoryID, Source) {
    var Elements = document.getElementsByTagName('select');

    if (Source) {

        var MyPosition = parseInt(Source.getAttribute("Position"));

        for (var i = 0, n = Elements.length; i < n; i++) {
            if (Elements[i].getAttribute("Position")) {
                if (parseInt(Elements[i].getAttribute("Position")) > MyPosition) {
                    Elements[i].setAttribute("RemoveMe", "True");
                }
            }
        }

        $('select[RemoveMe="True"]').remove();
    }

    PageMethods.GetProductCategories(CategoryID, function (result) {

        if (result.length > 0) {

            CategorySelection = document.getElementById("CategorySelection");

            ItemInput = document.createElement("select");
            ItemInput.setAttribute('id', 'ItemCategory_' + CategoryID);
            ItemInput.setAttribute('Position', Elements.length + 1);
            ItemInput.setAttribute('style', 'width: 390px; margin-bottom: 10px;');
            ItemInput.className = "TextBox";
            ItemInput.setAttribute('onchange', 'GetSubCategories(this.value, this);');

            CategorySelection.appendChild(ItemInput);

            BuildSelect(result, document.getElementById("ItemCategory_" + CategoryID), 0);

            GetSubCategories(parseInt(document.getElementById("ItemCategory_" + CategoryID).value), document.getElementById("ItemCategory_" + CategoryID));
        }

    });

    var Elements = document.getElementsByTagName('select');

    var CategoryID = parseInt($('select[Position="' + Elements.length + '"]').val())

    GetProducts(CategoryID);
}

function GetProducts(CategoryID)
{
    document.getElementById("Products").innerHTML = '';

    PageMethods.GetProducts(CategoryID, function (result) {

        if (result.length > 0) {

            for (r = 0; r < result.length; r++) {

                var productItem = document.createElement("div");

                productItem.className = "Suggestion";
                productItem.setAttribute('ProductID', result[r]['ProductID']);
                productItem.setAttribute('ProductReference', result[r]['ProductReference']);
                productItem.setAttribute('onclick', 'GetProductProperties(' + result[r]['ProductID'] + ')');
                productItem.appendChild(document.createTextNode(result[r]['ProductName']));

                document.getElementById("Products").appendChild(productItem);
            }
        }
    });
}

function GetProductProperties(ProductID) {

    document.getElementById("ProductProperties").innerHTML = '';

    PageMethods.GetProductProperties(ProductID, function (properties) {

        if (properties.length > 0) {

            var ProductDetails = document.createElement("div");

            ProductDetails.setAttribute('ProductID', ProductID);
            ProductDetails.appendChild(document.createTextNode(properties[0]['Text']));

            //Value is custom fields

            var CustomFields = properties[0]['Value'].split(';');

            for (c = 0; c < CustomFields.length; c++) {

                var customField = document.createElement("div");

                customField.className = "Suggestion";
                customField.appendChild(document.createTextNode(CustomFields[c]));

                ProductDetails.appendChild(customField);
            }

            document.getElementById("ProductProperties").appendChild(ProductDetails);
        }
    });
}

function SetLookupData() {

    var ifr = document.getElementById("TemplateEditor");
    var ifrDoc = ifr.contentDocument || ifr.contentWindow.document;
    var theForm = ifrDoc.getElementById("TheForm");

    if (document.getElementById("ProductProperties").innerHTML != '')
    {
        if (document.getElementById("ProductProperties").childNodes[0].getAttribute('ProductID')) {

            var ProductID = document.getElementById("ProductProperties").childNodes[0].getAttribute('ProductID');

            PageMethods.GetProductProperties(ProductID, function (properties) {

                if (properties.length > 0) {

                    var CustomFields = properties[0]['Value'].split(';');

                    for (c = 0; c < CustomFields.length; c++) {

                        if ($('[LookupKey="' + CustomFields[c].substr(0, CustomFields[c].indexOf(':')) + '"]', theForm)) {

                            $('[LookupKey="' + CustomFields[c].substr(0, CustomFields[c].indexOf(':')) + '"]', theForm).val(CustomFields[c].substr(CustomFields[c].indexOf(':') + 1));
                            //$('#' + CustomFields[c].substr(0, CustomFields[c].indexOf(':')), theForm).val = CustomFields[c].substr(CustomFields[c].indexOf(':') + 1);
                        }

                    }
                }

                document.body.removeChild(document.getElementById("LookupHUD"));

            });
        }
    }
}
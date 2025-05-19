function AddDocumentFromTemplates()
{
    var EditHUD = document.createElement("div");

    EditHUD.className = "DocumentTemplates";
    EditHUD.setAttribute('id', 'DocumentTemplatesHUD');
    //EditHUD.setAttribute('style', 'height: 300px; margin-top: -180px;');

    var ItemDescription = document.createElement("div");

    ItemDescription.className = "Title";
    ItemDescription.appendChild(document.createTextNode("New Form"));

    EditHUD.appendChild(ItemDescription);

    var TemplateContainer = document.createElement("div");
    TemplateContainer.className = "Templates";
    TemplateContainer.id = "AvailableTemplates";
    //TemplateContainer.setAttribute('style', 'height: 185px; margin-top: 10px;');

    EditHUD.appendChild(TemplateContainer);

    //Buttons

    var ItemButtons = document.createElement("div");

    ItemButtons.className = "Buttons";

    ItemButton = document.createElement("button");
    ItemButton.className = "ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only";
    ItemButton.setAttribute('onClick', 'CancelAddDocument();');

    ItemButtonText = document.createElement("span");
    ItemButtonText.className = "ui-button-text";
    ItemButtonText.appendChild(document.createTextNode("Cancel"));
    ItemButton.appendChild(ItemButtonText);

    ItemButtons.appendChild(ItemButton);

    EditHUD.appendChild(ItemButtons);

    document.body.appendChild(EditHUD);

    GetAvailableTemplates();
}

function CancelAddDocument() {


    document.body.removeChild(document.getElementById("DocumentTemplatesHUD"));
}

function GetAvailableTemplates()
{
    PageMethods.GetDocumentTemplates(function (result) {

        if (result.length > 0) {

            for (r = 0; r < result.length; r++) {

                var newItem = document.createElement("div");

                newItem.innerText = result[r]['Text'].replace('.html', '');
                newItem.className = "TemplateItem";
                newItem.setAttribute('onclick', 'CreateDocument("' + result[r]['Text'] + '")')

                document.getElementById("AvailableTemplates").appendChild(newItem);
            }
        }
    });
}

function CreateDocument(TemplateName)
{
    window.location = 'EditDocument.aspx?Template=' + encodeURI(TemplateName) + '&IsNew=true';
}
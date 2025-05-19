package uk.co.perspective.app.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity
public class OpportunityForm {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @SerializedName("localOpportunityID")
    @ColumnInfo(name = "localOpportunityID")
    private Integer localOpportunityID;

    @SerializedName("opportunityFormID")
    @ColumnInfo(name = "opportunityFormID")
    private Integer opportunityFormID;

    @SerializedName("opportunityID")
    @ColumnInfo(name = "opportunityID")
    private Integer opportunityID;

    @SerializedName("formName")
    @ColumnInfo(name = "formName")
    private String formName;

    @SerializedName("formTemplate")
    @ColumnInfo(name = "formTemplate")
    private String formTemplate;

    @SerializedName("formID")
    @ColumnInfo(name = "formID")
    private Integer formID;

    @SerializedName("formData")
    @ColumnInfo(name = "formData")
    private String formData;

    @SerializedName("description")
    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "isNew")
    private boolean isNew;

    @ColumnInfo(name = "hasData")
    private boolean hasData;

    @ColumnInfo(name = "isComplete")
    private boolean isComplete;

    public OpportunityForm() {

    }

    @Ignore
    public OpportunityForm(int id, String formName)
    {
        this.id = id;
        this.formName = formName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getOpportunityFormID() {
        return opportunityFormID;
    }

    public void setOpportunityFormID(Integer opportunityFormID) {
        this.opportunityFormID = opportunityFormID;
    }

    public Integer getOpportunityID() {
        return opportunityID;
    }

    public void setOpportunityID(Integer opportunityID) {
        this.opportunityID = opportunityID;
    }

    public OpportunityForm withOpportunityID(Integer opportunityID) {
        this.opportunityID = opportunityID;
        return this;
    }

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public OpportunityForm withFormName(String formTemplate) {
        this.formName = formName;
        return this;
    }

    public String getFormTemplate() {
        return formTemplate;
    }

    public void setFormTemplate(String formTemplate) {
        this.formTemplate = formTemplate;
    }

    public OpportunityForm withFormTemplate(String formTemplate) {
        this.formTemplate = formTemplate;
        return this;
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    public OpportunityForm withFormData(String formData) {
        this.formData = formData;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OpportunityForm withDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean getHasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public boolean getIsComplete() {
        return isComplete;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public Integer getLocalOpportunityID() {
        return localOpportunityID;
    }

    public void setLocalOpportunityID(Integer localOpportunityID) {
        this.localOpportunityID = localOpportunityID;
    }

    public Integer getFormID() {
        return formID;
    }

    public void setFormID(Integer formID) {
        this.formID = formID;
    }
}

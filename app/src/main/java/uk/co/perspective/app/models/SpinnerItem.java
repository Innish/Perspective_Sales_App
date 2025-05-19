package uk.co.perspective.app.models;

public class SpinnerItem {

    private int value;
    private String text;

    public SpinnerItem(int value, String text) {
        super();
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof SpinnerItem){
            SpinnerItem a = (SpinnerItem )obj;
            if(a.getText().equals(text) && a.getValue()==value ) return true;
        }

        return false;
    }
}

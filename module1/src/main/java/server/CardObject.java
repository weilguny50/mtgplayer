package server;

public class CardObject {
    String setName;
    String setNumber;

    public CardObject(String name, String number){
        name=name.trim();
        this.setName=name;
        number=number.trim();
        this.setNumber=number;
    }

    public String getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(String setNumber) {
        this.setNumber = setNumber;
    }

    public String getSetName() {
        return setName;
    }

    public void setSetName(String setName) {
        this.setName = setName;
    }
}

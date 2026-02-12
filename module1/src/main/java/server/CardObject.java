package server;

import java.io.File;

public class CardObject {
    String setName;
    String setNumber;

    File frontFace = null;
    File backFace = null;
    public CardObject(String name, String number){
        name=name.trim();
        this.setName=name;
        number=number.trim();
        this.setNumber=number;
    }
    public CardObject(){
    }

    public void setFrontAndBack(File front,File back){
        frontFace=front;
        backFace=back;
    }

    public File getFrontFace() {
        return frontFace;
    }

    public void setFrontFace(File frontFace) {
        this.frontFace = frontFace;
    }

    public File getBackFace() {
        return backFace;
    }

    public void setBackFace(File backFace) {
        this.backFace = backFace;
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

package az.seyidkanan.savingobjecttutorial.model;


import java.io.Serializable;

/**
 * Created by Kanan on 3/22/2018.
 */

public class UserModel implements Serializable {

    private String name;
    private String surname;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}

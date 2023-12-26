package br.com.dias.apiRest.data.dto.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.Column;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;

@JsonPropertyOrder({ "id", "firstName", "lastName", "address", "gender" })
public class PersonDTO extends RepresentationModel<PersonDTO> implements Serializable {

    @JsonProperty("id")
    private Long identity;
    private String firstName;
    private String lastName;
    private String address;
    private String gender;
    private Boolean enabled;

    public PersonDTO() {
    }

    public Long getIdentity() {
        return identity;
    }

    public void setIdentity(Long identity) {
        this.identity = identity;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String addres) {
        this.address = addres;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonDTO)) return false;
        if (!super.equals(o)) return false;

        PersonDTO personDTO = (PersonDTO) o;

        if (!identity.equals(personDTO.identity)) return false;
        if (!firstName.equals(personDTO.firstName)) return false;
        if (!lastName.equals(personDTO.lastName)) return false;
        if (!address.equals(personDTO.address)) return false;
        if (!gender.equals(personDTO.gender)) return false;
        return enabled.equals(personDTO.enabled);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + identity.hashCode();
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + address.hashCode();
        result = 31 * result + gender.hashCode();
        result = 31 * result + enabled.hashCode();
        return result;
    }
}

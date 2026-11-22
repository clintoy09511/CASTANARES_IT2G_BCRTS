package com.scc.model;

import java.time.LocalDate;

public class Resident {
    private int residentId;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate birthDate;
    private String gender;
    private String address;
    private String contactNumber;
    private String civilStatus;
    private String status;

    public Resident() {
    }

    public Resident(int residentId, String firstName, String lastName, String middleName,
                    LocalDate birthDate, String gender, String address, String contactNumber,
                    String civilStatus, String status) {
        this.residentId = residentId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
        this.contactNumber = contactNumber;
        this.civilStatus = civilStatus;
        this.status = status;
    }

    public int getResidentId() { return residentId; }
    public void setResidentId(int residentId) { this.residentId = residentId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }

    public String getCivilStatus() { return civilStatus; }
    public void setCivilStatus(String civilStatus) { this.civilStatus = civilStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Resident{residentId=" + residentId + ", name='" + firstName + " " + lastName +
                "', status='" + status + "'}";
    }
}
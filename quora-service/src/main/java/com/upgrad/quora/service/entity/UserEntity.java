package com.upgrad.quora.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "USERS",uniqueConstraints = @UniqueConstraint(columnNames = "email")) //public schema
@NamedQueries({
        @NamedQuery(name="findByUsername", query = "select u from UserEntity u where u.username=:userByUserName"),
        @NamedQuery(name="findByEmail",query="select u from UserEntity u where u.email=:userByEmail"),
        @NamedQuery(name = "userByUuid", query = "select u from UserEntity u where u.uuid = :uuid")
})
public class UserEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // @Size()
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    // @Size()
    private String uuid;

    @Column(name = "firstname")
    @NotNull
    //@Size()
    private String firstName;

    @Column(name = "lastname")
    @NotNull
    //@Size()
    private String lastName;

    @Column(name = "username")
    @NotNull
    //@Size()
    private String username;

    @Column(name = "email")
    @NotNull
    //@Size()

    private String email;

    @Column(name = "password")
    @NotNull
    //@Size()
    private String password;

    @Column(name = "salt")
    @NotNull
    //@Size()
    private String salt;

    @Column(name = "country")
    //@Size()
    private String country;

    @Column(name = "aboutme")
    //@Size()
    private String aboutme;

    @Column(name = "dob")
    //@Size()
    private String dob;

    @Column(name = "role")
    //@Size()
    private String role;

    @Column(name = "contactnumber")
    //@Size()
    private String contactNumber;

    @OneToMany(mappedBy = "users",fetch = FetchType.LAZY)
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<AnswerEntity> answersList;

    @OneToMany(mappedBy = "userEntity",fetch = FetchType.LAZY)
    @OnDelete(action=OnDeleteAction.CASCADE)
    private List<QuestionsEntity> questionsList;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAboutme() {
        return aboutme;
    }

    public void setAboutme(String aboutme) {
        this.aboutme = aboutme;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
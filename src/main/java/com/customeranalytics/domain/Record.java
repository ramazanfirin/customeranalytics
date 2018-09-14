package com.customeranalytics.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import com.customeranalytics.domain.enumeration.Gender;

/**
 * A Record.
 */
@Entity
@Table(name = "record")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Record implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "age")
    private Long age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "jhi_insert")
    private Instant insert;

    @Column(name = "afid")
    private String afid;

    @ManyToOne
    private Device device;

    @ManyToOne
    private Stuff stuff;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAge() {
        return age;
    }

    public Record age(Long age) {
        this.age = age;
        return this;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public Record gender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Instant getInsert() {
        return insert;
    }

    public Record insert(Instant insert) {
        this.insert = insert;
        return this;
    }

    public void setInsert(Instant insert) {
        this.insert = insert;
    }

    public String getAfid() {
        return afid;
    }

    public Record afid(String afid) {
        this.afid = afid;
        return this;
    }

    public void setAfid(String afid) {
        this.afid = afid;
    }

    public Device getDevice() {
        return device;
    }

    public Record device(Device device) {
        this.device = device;
        return this;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Stuff getStuff() {
        return stuff;
    }

    public Record stuff(Stuff stuff) {
        this.stuff = stuff;
        return this;
    }

    public void setStuff(Stuff stuff) {
        this.stuff = stuff;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Record record = (Record) o;
        if (record.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), record.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Record{" +
            "id=" + getId() +
            ", age=" + getAge() +
            ", gender='" + getGender() + "'" +
            ", insert='" + getInsert() + "'" +
            ", afid='" + getAfid() + "'" +
            "}";
    }
}

package com.cache.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.hibernate.annotations.Cache;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "hash_key_value")
@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},
        allowGetters = true)

public class ObjectKeyValue  {
    @Id
    @Column(name = "key_my")
    private String keyMy;

    @NotBlank
    @Column(name = "value")
    private String value;

    public String getKeyMy() {
        return keyMy;
    }

    public void setKeyMy(String keyMy) {
        this.keyMy = keyMy;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

package ru.android_studio.night_meet.retrofit.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Generated;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
        "uid",
        "first_name",
        "last_name",
        "photo_max_orig"
})
public class User {

    @JsonProperty("uid")
    private Integer uid;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("photo_max_orig")
    private String photoMaxOrig;

    @JsonProperty("photo_200")
    private String photo200;

    /**
     * @return The uid
     */
    @JsonProperty("uid")
    public Integer getUid() {
        return uid;
    }

    /**
     * @param uid The uid
     */
    @JsonProperty("uid")
    public void setUid(Integer uid) {
        this.uid = uid;
    }

    /**
     * @return The firstName
     */
    @JsonProperty("first_name")
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName The first_name
     */
    @JsonProperty("first_name")
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return The lastName
     */
    @JsonProperty("last_name")
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName The last_name
     */
    @JsonProperty("last_name")
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return The photoMaxOrig
     */
    @JsonProperty("photo_max_orig")
    public String getPhotoMaxOrig() {
        return photoMaxOrig;
    }

    /**
     * @param photoMaxOrig The photo_max_orig
     */
    @JsonProperty("photo_max_orig")
    public void setPhotoMaxOrig(String photoMaxOrig) {
        this.photoMaxOrig = photoMaxOrig;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public String getPhoto200() {
        return photo200;
    }

    public void setPhoto200(String photo200) {
        this.photo200 = photo200;
    }
}

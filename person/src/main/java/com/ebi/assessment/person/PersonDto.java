package com.ebi.assessment.person;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(builderClassName = "Builder", setterPrefix = "with")
@JsonDeserialize(builder = PersonDto.Builder.class)
@ToString
@EqualsAndHashCode
@ApiModel
public class PersonDto {
    @ApiModelProperty("Resource Id - Autogenerated")
    private Integer id;
    @ApiModelProperty("First name")
    private final String firstName;
    @ApiModelProperty("Last name")
    private final String lastName;
    @ApiModelProperty("Age")
    private final Integer age;
    @ApiModelProperty("Favorite Color")
    private final String favoriteColor;
}

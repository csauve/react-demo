package api

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import org.hibernate.validator.constraints.Email
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.NotBlank

import javax.validation.Valid
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class UserAccount implements ContextProvider {
  static interface NorthAmerican extends Context {}

  static enum UserType {
    ADMIN,
    PUBLIC
  }

  @JsonProperty
  @NotBlank
  @Length(max = 20)
  @Email
  String email

  @JsonProperty
  @NotNull
  @Immutable
  UserType type = UserType.PUBLIC

  @JsonProperty
  @NotNull
  @Valid
  Address address

  @JsonProperty
  @NotBlank(groups = NorthAmerican)
  @Applicable(groups = NorthAmerican)
  String phoneNumber

  @Override
  Map<Class<? extends Context>, Boolean> determineContexts() {[
      (NorthAmerican): address?.countryIso3 ? address.countryIso3 in ["USA", "CAN"] : null
  ]}

  static class Address implements ContextProvider {
    static interface ZipRequired extends Context {}

    @JsonPropertyDescription("An ISO 3 code")
    @JsonProperty
    @NotBlank
    @Size(min = 3, max = 3)
    String countryIso3

    @JsonProperty
    @NotBlank
    @Size(max = 60)
    String addressLine1

    @JsonProperty
    @NotBlank(groups = ZipRequired)
    String zipCode

    @Override
    Map<Class<? extends Context>, Boolean> determineContexts() {[
        (ZipRequired): countryIso3 ? countryIso3 in ["USA"] : null
    ]}
  }
}

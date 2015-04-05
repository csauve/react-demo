package resources

import api.Applicable
import api.ContextProvider
import api.Immutable
import api.UserAccount
import com.fasterxml.jackson.annotation.JsonProperty

import javax.validation.Constraint
import javax.validation.constraints.Size
import javax.ws.rs.GET
import javax.ws.rs.OPTIONS
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import java.lang.annotation.Annotation
import java.lang.reflect.Field

@Path("/api/account")
@Produces(MediaType.APPLICATION_JSON)
class UserAccountResource {

  private final Map<Long, UserAccount> accounts = [
      1L: new UserAccount(
          address: new UserAccount.Address(
              countryIso3: "USA"
          )
      )
  ]

  @POST
  long createAccount() {
    def newId = accounts.isEmpty() ? 1L : accounts.keySet().max() + 1
    accounts.put(newId, new UserAccount())
    return newId
  }

  @GET
  @Path("/{id}")
  Response getAccount(@PathParam("id") long id) {
    if (!accounts[id]) return Response.status(404).build()
    return Response.status(200).entity(accounts[id]).build()
  }

  @OPTIONS
  @Path("/{id}")
  Response getOptions(@PathParam("id") long id) {
    def schema = generateSchema(UserAccount, accounts[id])
    return Response.status(200).entity(schema).build()
  }

  protected static <T> Object generateSchema(Class<T> type, T value, Collection<Class> groups = []) {
    fieldSchema(null, type, value, groups)
  }

  private static <T> Object fieldSchema(Field field, Class<T> type, T value, Collection<Class> groups) {
    if (value && value instanceof ContextProvider) {
      groups += (value as ContextProvider).determineContexts().findResults { it.value ? it.key : null }
    }

    def schema = [
        type: type.simpleName,
        constraints: findConstraints(field?.declaredAnnotations?.toList(), groups),
        properties: type.declaredFields.findAll {
          def applicableAnnotation = it.getAnnotation(Applicable)
          def fieldApplicable = !applicableAnnotation || annotationApplies(applicableAnnotation.groups().toList(), groups)
          return it.getAnnotation(JsonProperty) && fieldApplicable
        }.collectEntries { Field subField ->
          [subField.name, fieldSchema(subField, subField.type, !value ? null : value.properties[subField.name], groups)]
        }
    ]

    return schema.findResults {
      it.value ? it : null
    }.collectEntries {it}
  }

  private static Collection<Object> findConstraints(Collection<Annotation> annotations, Collection<Class> applicableGroups) {
    if (!annotations) return []
    annotations.findResults { Annotation ann ->
      def annotationType = ann.annotationType()

      //if the annotation is a javax.validation.Constraint, it will have groups() and message() at the least
      if (annotationType.getAnnotation(Constraint)) {
        def annotatedGroups = ann.groups().toList() as List<Class>
        def message = ann.message() as String

        if (annotationApplies(annotatedGroups, applicableGroups)) {
          def constraint = [type: annotationType.simpleName, message: message] as Map

          switch (annotationType) {
            case (Size):
              ann = ann as Size
              return constraint + [max: ann.max(), min: ann.min()]
            default:
              return constraint
          }
        }
      } else if (annotationType == Immutable) {
        ann = ann as Immutable
        if (annotationApplies(ann.groups().toList(), applicableGroups)) {
          return [type: annotationType.simpleName, message: ann.message()]
        }
      }
      return null
    }
  }

  private static boolean annotationApplies(Collection<Class> annotationGroups, Collection<Class> contextGroups) {
    !annotationGroups || !annotationGroups.disjoint(contextGroups)
  }
}

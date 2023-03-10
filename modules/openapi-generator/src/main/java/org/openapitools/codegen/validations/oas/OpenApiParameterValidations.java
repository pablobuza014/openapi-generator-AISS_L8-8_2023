// We define the package to which the OpenApiParameterValidations class belongs.
package org.openapitools.codegen.validations.oas;

//Next, we add the necessary imports for the class, including the OpenAPI class defined by Swagger,
//a StringUtils class from the Apache Commons library, and classes for generic rule validation with GenericValidator and ValidationRule.
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.apache.commons.lang3.StringUtils;
import org.openapitools.codegen.validation.GenericValidator;
import org.openapitools.codegen.validation.ValidationRule;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A standalone instance for evaluating rules and recommendations related to OAS {@link Parameter}
 */

/*
In the following code, the OpenApiParameterValidations class is defined, which inherits from GenericValidator with a specific type, ParameterWrapper.
The constructor accepts a rule configuration, which decides whether to enable recommendations or not.
Subsequently, it checks if recommendations are enabled and, if the underscore recommendation in apache or nginx is enabled, 
a new rule is added by calling ValidationRule.warn().
This rule is defined with the corresponding description and error message, as well as the method that will be used for validation or checking.
*/

class OpenApiParameterValidations extends GenericValidator<ParameterWrapper> { // Defines a class called "OpenApiParameterValidations" that extends the abstract class "GenericValidator" and works with objects of type "ParameterWrapper".
    OpenApiParameterValidations(RuleConfiguration ruleConfiguration) { // Defines a constructor for the class.
        super(new ArrayList<>()); // Calls the constructor of the superclass "GenericValidator" with an empty list as argument.
        if (ruleConfiguration.isEnableRecommendations()) { // If the rule configuration has recommendations enabled...
            if (ruleConfiguration.isEnableApacheNginxUnderscoreRecommendation()) { // If the rule configuration has the Apache/Nginx underscore recommendation enabled...
                rules.add(ValidationRule.warn( // Adds a new validation rule with a warning level to the list of rules.
                        ValidationConstants.ApacheNginxUnderscoreDescription, // description of the rule.
                        ValidationConstants.ApacheNginxUnderscoreFailureMessage, // failure message of the rule.
                        OpenApiParameterValidations::apacheNginxHeaderCheck // function that handles the validation.
                ));
            }
        }
    }

    /**
     * Apache and Nginx default to legacy CGI behavior in which header with underscore are ignored. Raise this for awareness to the user.
     *
     * @param parameter Any spec doc parameter. The method will handle {@link HeaderParameter} evaluation.
     * @return {@link ValidationRule.Pass} if the check succeeds, otherwise {@link ValidationRule.Fail} with details "[key] contains an underscore."
     */
    
    /*
    In the following code, the private method apacheNginxHeaderCheck() is defined.
    This method returns an instance of ValidationRule.Result.
    The method is responsible for checking that header (or parameter) names follow some conventions used by Apache and Nginx.
    If the parameter name contains an underscore, it returns an instance of ValidationRule.Fail with details indicating that
    the specified header name contains an underscore. Otherwise, it returns an instance of ValidationRule.Pass.
    */
    
    private static ValidationRule.Result apacheNginxHeaderCheck(ParameterWrapper parameterWrapper) { //Defines a private static method called "apacheNginxHeaderCheck" that takes a "ParameterWrapper" object as an argument and returns a "ValidationRule.Result" object.
        Parameter parameter = parameterWrapper.getParameter(); // Gets the "Parameter" object from the "ParameterWrapper" object received as an argument.
        if (parameter == null || !"header".equals(parameter.getIn())) return ValidationRule.Pass.empty(); // If the "Parameter" object is null or its "in" attribute is not equal to "header", returns an empty "ValidationRule.Pass" object.
        ValidationRule.Result result = ValidationRule.Pass.empty(); // Initializes an empty "ValidationRule.Result" object.
        String headerName = parameter.getName(); // Gets the header name from the "Parameter" object.
        if (StringUtils.isNotEmpty(headerName) && StringUtils.contains(headerName, '_')) { // If the header name is not empty and contains an underscore...
            result = new ValidationRule.Fail(); // Creates a new "ValidationRule.Fail" object.
            result.setDetails(String.format(Locale.ROOT, "%s contains an underscore.", headerName)); // Sets a message indicating that the specified header name contains an underscore.
        }
        return result; // Returns the updated "ValidationRule.Result" object.
    }
}

package com.sap.cloud.cmp.ord.service.controller;

import com.sap.cloud.cmp.ord.service.repository.SpecRepository;
import com.sap.cloud.cmp.ord.service.storage.model.SpecificationEntity;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Controller
public class SpecificationsController extends com.sap.cloud.cmp.ord.service.controller.Controller {

    private static final String MEDIA_TYPE_YAML_VALUE = "text/yaml";

    private final String NOT_FOUND_MESSAGE = "Not Found";
    private final String INVALID_TENANT_ID_ERROR_MESSAGE = "Missing or invalid tenantID";

    private static final String UNAUTHORIZED_MSG = "{\n" +
            "  \"error\": {\n" +
            "    \"code\": 401,\n" +
            "    \"status\": \"Unauthorized\",\n" +
            "    \"request\": \"8ed540a8-b4c6-49e3-a5ca-8dc29ba94318\",\n" +
            "    \"message\": \"The request could not be authorized\"\n" +
            "  }\n" +
            "}";

    @Autowired
    private SpecRepository specRepository;

    @RequestMapping(value = "/${static.request_mapping_path}/api/{apiId}/specification/{specId}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE, MEDIA_TYPE_YAML_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @ResponseBody
    @ApiImplicitParam(name = "Tenant", value = "Tenant GUID", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = UUID.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = @ExampleObject(name = "example", value = "<api specification in appropriate standard>"), schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Missing or invalid tenantID", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = @ExampleObject(name = "example", value = "Missing or invalid tenantID"), schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "example", value = UNAUTHORIZED_MSG), schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = @ExampleObject(name = "example", value = "Not Found"), schema = @Schema(implementation = String.class)))
    })
    public void getApiSpec(HttpServletRequest request, HttpServletResponse response, @PathVariable final String apiId, @PathVariable final String specId) throws IOException {
        Pair<String, String> tenantIDs = super.extractTenantsFromIDToken(request);
        if (tenantIDs == null) {
            respond(response, HttpServletResponse.SC_BAD_REQUEST, MediaType.TEXT_PLAIN_VALUE, INVALID_TENANT_ID_ERROR_MESSAGE);
            return;
        }

        String tenantID = tenantIDs.getFirst();
        String providerTenantID = tenantIDs.getSecond();
        if (tenantID == null || providerTenantID == null || tenantID.isEmpty() || providerTenantID.isEmpty()) {
            respond(response, HttpServletResponse.SC_BAD_REQUEST, MediaType.TEXT_PLAIN_VALUE, INVALID_TENANT_ID_ERROR_MESSAGE);
            return;
        }

        try {
            SpecificationEntity apiSpec = specRepository.getBySpecIdAndApiDefinitionIdAndTenantAndProviderTenant(UUID.fromString(specId), UUID.fromString(apiId), UUID.fromString(tenantID), UUID.fromString(providerTenantID));
            if (apiSpec == null) {
                respond(response, HttpServletResponse.SC_NOT_FOUND, MediaType.TEXT_PLAIN_VALUE, NOT_FOUND_MESSAGE);
                return;
            }

            respond(response, HttpServletResponse.SC_OK, apiSpec.getApiSpecFormat(), apiSpec.getSpecData());
        } catch (IllegalArgumentException e) {
            respond(response, HttpServletResponse.SC_BAD_REQUEST, MediaType.TEXT_PLAIN_VALUE, e.getMessage());
        }
    }

    @RequestMapping(value = "/${static.request_mapping_path}/event/{eventId}/specification/{specId}", method = {RequestMethod.GET}, produces = {MediaType.APPLICATION_JSON_VALUE, MEDIA_TYPE_YAML_VALUE, MediaType.APPLICATION_XML_VALUE, MediaType.TEXT_PLAIN_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @ResponseBody
    @ApiImplicitParam(name = "Tenant", value = "Tenant GUID", required = true, allowEmptyValue = false, paramType = "header", dataTypeClass = UUID.class)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = @ExampleObject(name = "example", value = "<event specification in appropriate standard>"), schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Missing or invalid tenantID", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = @ExampleObject(name = "example", value = "Missing or invalid tenantID"), schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, examples = @ExampleObject(name = "example", value = UNAUTHORIZED_MSG), schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE, examples = @ExampleObject(name = "example", value = "Not Found"), schema = @Schema(implementation = String.class)))
    })
    public void getEventSpec(HttpServletRequest request, HttpServletResponse response, @PathVariable final String eventId, @PathVariable final String specId) throws IOException {
        Pair<String, String> tenantIDs = super.extractTenantsFromIDToken(request);
        if (tenantIDs == null) {
            respond(response, HttpServletResponse.SC_BAD_REQUEST, MediaType.TEXT_PLAIN_VALUE, INVALID_TENANT_ID_ERROR_MESSAGE);
            return;
        }

        String tenantID = tenantIDs.getFirst();
        String providerTenantID = tenantIDs.getSecond();
        if (tenantID == null || providerTenantID == null || tenantID.isEmpty() || providerTenantID.isEmpty()) {
            respond(response, HttpServletResponse.SC_BAD_REQUEST, MediaType.TEXT_PLAIN_VALUE, INVALID_TENANT_ID_ERROR_MESSAGE);
            return;
        }

        try {
            SpecificationEntity eventSpec = specRepository.getBySpecIdAndEventDefinitionIdAndTenantAndProviderTenant(UUID.fromString(specId), UUID.fromString(eventId), UUID.fromString(tenantID), UUID.fromString(providerTenantID));
            if (eventSpec == null) {
                respond(response, HttpServletResponse.SC_NOT_FOUND, MediaType.TEXT_PLAIN_VALUE, NOT_FOUND_MESSAGE);
                return;
            }

            respond(response, HttpServletResponse.SC_OK, eventSpec.getEventSpecFormat(), eventSpec.getSpecData());
        } catch (IllegalArgumentException e) {
            respond(response, HttpServletResponse.SC_BAD_REQUEST, MediaType.TEXT_PLAIN_VALUE, e.getMessage());
        }
    }

    private void respond(HttpServletResponse response, int statusCode, String contentType, String body) throws IOException {
        response.setStatus(statusCode);
        response.setContentType(contentType);

        response.getWriter().print(body);
    }
}


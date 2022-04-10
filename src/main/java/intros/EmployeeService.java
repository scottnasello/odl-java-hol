/*
 * Copyright (c) 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package intros;

import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * The Employee service endpoint.
 * http://localhost:8080/employees Get all employees
 * http://localhost:8080/employees/{id} Get all employees by id
 */
public class EmployeeService implements Service {
    private final EmployeeRepository employees;
    private static final Logger LOGGER = Logger.getLogger(EmployeeService.class.getName());

    EmployeeService() {
        employees = new EmployeeRepository();
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.get("/", this::getAll)
                .get("/lastname/{lastname}", this::getByLastName)
                .get("/hometownzip/{hometownzipName}", this::getByHometownzip)
                .get("/track/{track}", this::getByTrack)
                .get("/{id}", this::getEmployeesById);
    }


    private void getAll(final ServerRequest request, final ServerResponse response) {
        LOGGER.fine("getAll");

        List<Employee> allEmployees = this.employees.getAll();
        if (allEmployees.size() > 0) {
            response.send(allEmployees);
        } else Util.sendError(response, 400, "getAll - no employee found!?");

    }

    private void getByLastName(final ServerRequest request, final ServerResponse response) {
        LOGGER.fine("getByLastName");

        var lastname = request.path().param("lastname").trim();
        if (Util.isValidQueryStr(response, lastname)) {
            var match = this.employees.getByLastName(lastname);
            if (match.size() > 0) response.send(match);
            else Util.sendError(response, 400, "getByLastName - not found: " + lastname);
        } else {
            Util.sendError(response, 500, "Internal error! getByLastName");
        }

    }


    private void getByTrack(final ServerRequest request, final ServerResponse response) {
        LOGGER.fine("getByTrack");

        try {
            var trackName = request.path().param("track").trim();
            Track track = Track.valueOf(trackName.toUpperCase());
            var match = this.employees.getByTrack(track);
            if (match.size() > 0) response.send(match);
            else Util.sendError(response, 400, "getByTrack - not found: " + trackName);
        } catch (Exception e) {
            Util.sendError(response, 500, "Internal error! getByTrack: " + e.getMessage());
        }

    }


    private void getByHometownzip(final ServerRequest request, final ServerResponse response) {
        LOGGER.fine("getByHometownzip");

        try {
            var hometownzipName = request.path().param("hometownzipName").trim();
            if (Util.isValidQueryStr(response, hometownzipName)) {
                var match = this.employees.getByHometownzip(hometownzipName);
                if (match.size() > 0) response.send(match);
                else Util.sendError(response, 400, "getByHometownzip - not found: " + hometownzipName);
            }
        } catch (Exception e) {
            Util.sendError(response, 500, "Internal error! getByHometownzip: " + e.getMessage());
        }

    }

    private void getEmployeesById(ServerRequest request, ServerResponse response) {
        LOGGER.fine("getEmployeesById");

        String id = request.path().param("id").trim();

        try {
            if (Util.isValidQueryStr(response, id)) {
                var match = this.employees.getById(id);
                if (match.isPresent()) {
                } else Util.sendError(response, 400, "getEmployeesById - not found: " + id);
            }
        } catch (Exception e) {
            Util.sendError(response, 500, "Internal error! getEmployeesById : " + e.getMessage());
        }
    }

}

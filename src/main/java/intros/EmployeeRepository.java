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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public final class EmployeeRepository {

    private final CopyOnWriteArrayList<Employee> employees = new CopyOnWriteArrayList<>();

    public EmployeeRepository() {
        List<Employee> employeesList = new ArrayList<>();
        InputStream csvFile = EmployeeRepository.class.getResourceAsStream("/employees.csv");

        try (BufferedReader csvReader = new BufferedReader(new InputStreamReader(csvFile))) {
            String csvRow;
            while ((csvRow = csvReader.readLine()) != null) {
                String[] col = csvRow.split("\\|");
                employeesList.add(new Employee(col[0], col[1], col[2], col[3], col[4], Track.valueOf(col[5].toUpperCase())));
            }
            this.employees.addAll(employeesList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Employee> getByLastName(String name) {
        List<Employee> matchList = employees.stream().filter((e) -> (e.getLastName().toLowerCase().contains(name.toLowerCase())))
                .collect(Collectors.toList());
        return matchList;
    }


    public List<Employee> getByTrack(Track track) {

        List<Employee> matchList = employees.stream().filter((e) -> (e.getTrack() == track))
                .collect(Collectors.toList());
        return matchList;
    }


    public List<Employee> getByHometownzip(String hometownzip) {

        List<Employee> matchList = employees.stream()
                .filter((e) -> (e.getHometownzip().toLowerCase().contains(hometownzip.toLowerCase())))
                .collect(Collectors.toList());
        return matchList;
    }


    public List<Employee> getAll() {

        List<Employee> allEmployees = employees.stream()
                .sorted(Comparator.comparing(Employee::getLastName))
                .collect(Collectors.toList());
        return allEmployees;
    }


    public Optional<Employee> getById(String id) {

        Optional<Employee> employee = employees.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
        return employee;
    }

}

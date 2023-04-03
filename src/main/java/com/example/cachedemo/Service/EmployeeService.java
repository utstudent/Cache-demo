package com.example.cachedemo.Service;

import com.example.cachedemo.Exceptions.ResourceNotFoundException;
import com.example.cachedemo.Model.Employee;
import com.example.cachedemo.Repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    CacheManager cacheManager;

    @Cacheable(value = "employees", key = "#employeeId", sync = true)
    public Employee getEmployee(Integer employeeId) {

        fakeDelay("Fetching employee from the database...");

        return employeeRepository.findById(employeeId).orElseThrow(
                () -> new ResourceNotFoundException("Employee not found: " + employeeId));
    }

    @Cacheable(value = "employees")
    public List<Employee> getAllEmployees() {
        fakeDelay("Fetching all employees from the database...");
        return employeeRepository.findAll();
    }

    @CachePut(value = "employees", key = "#employee.id")
    public Employee saveEmployee(Employee employee) {
        System.out.println("Saving user to the database and updating cache");
        return employeeRepository.save(employee);
    }

    @CacheEvict(value = "employees", allEntries = true)
    public void deleteEmployee(Integer employeeId) {
        System.out.println("Removing all users from the database and cache");
        employeeRepository.deleteById(employeeId);
    }

    public void fakeDelay(String message){
        for (int i = 0; i < 5; i++){
            System.out.println(message);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

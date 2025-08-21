package com.resume.matcher.service;


import com.resume.matcher.models.Role;
import com.resume.matcher.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    //Create Role
    public Role createRole(Role role)
    {
        return roleRepository.save(role);
    }

    //Get Role
    public List<Role> getAll()
    {
        return roleRepository.findAll();
    }

    //Get Role by Id
    public Role getById(Long id)
    {

        return roleRepository.findById(id).orElseThrow(()->new RuntimeException("Role not found"));

    }

    //Update
    public Role updateRole(Role role)
    {
        return roleRepository.save(role);
    }

    //Delete
    public String  deleteById(Long id)
    {
        roleRepository.deleteById(id);
        return "Role deleted";
    }
}

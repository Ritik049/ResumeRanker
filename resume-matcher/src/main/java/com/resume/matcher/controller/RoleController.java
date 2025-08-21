package com.resume.matcher.controller;


import com.resume.matcher.models.Role;
import com.resume.matcher.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleService roleService;

    @GetMapping("/all")
    public List<Role> getAllRoles()
    {
        return  roleService.getAll();
    }

    @GetMapping("/{id}")
    public Role getById(@RequestParam("id") Long id)
    {
        return roleService.getById(id);
    }

    @PostMapping("/create")
    public Role createRole(@RequestBody Role role)
    {
        return roleService.createRole(role);
    }

    @PutMapping("/update")
    public Role updateRole(@RequestBody Role role)
    {
        return roleService.updateRole(role);
    }

    @DeleteMapping("/{id}")
    public String deleteRole(@RequestParam("id") Long id)
    {
        return roleService.deleteById(id);
    }
}

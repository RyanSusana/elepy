package com.elepy.auth;

import com.elepy.annotations.Inject;
import com.elepy.annotations.PredefinedRole;
import com.elepy.dao.Crud;
import com.elepy.evaluators.DefaultIntegrityEvaluator;
import com.elepy.evaluators.EvaluationType;
import com.elepy.exceptions.ElepyException;

import java.util.*;
import java.util.stream.Collectors;

import static com.elepy.dao.Filters.search;

public class Policy  {

    @Inject
    private Crud<User> users;

    @Inject
    private Crud<CustomRole> customRoles;

    private List<Role> predefinedRoles = new ArrayList<>();
    private Set<String> availablePermissions = new TreeSet<>();



    public void registerPredefinedRole(PredefinedRole predefinedRole) {
        final var role = new Role();

        role.setId(predefinedRole.name());
        role.setName(predefinedRole.name());
        role.setPermissions(List.of(predefinedRole.permissions()));
        role.setDescription(predefinedRole.description());
        predefinedRoles.add(role);
    }

    public void createCustomRole(CustomRole customRole) {
        if (predefinedRoles.stream().anyMatch(role -> role.getId().equals(customRole.getId())
                || role.getName().equals(customRole.getName()))) {
            throw new ElepyException("This role already exists as a predefined role");
        }

        new DefaultIntegrityEvaluator<>(customRoles).evaluate(customRole, EvaluationType.CREATE);

        customRoles.create(customRole);
    }

    public List<Role> getAllRoles() {
        final var roles = new ArrayList<>(predefinedRoles);

        roles.addAll(this.customRoles.find(search("")));
        return roles;
    }


    public boolean userHasRole(User user, String role) {
        return user.getRoles().contains(role);
    }

    public List<String> getPermissionsForUser(User user) {

        return user.getRoles().stream()
                .map(this::getRole)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Role::getPermissions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

    }

    public Optional<Role> getRole(String id) {
        return predefinedRoles.stream()
                .filter(role -> id.equals(role.getId()))
                .findFirst()
                .or(() -> customRoles.getById(id));
    }


} 

package com.elepy.auth;

import com.elepy.auth.permissions.DefaultPermissions;
import com.elepy.auth.permissions.Permissions;
import jakarta.inject.Inject;
import com.elepy.annotations.PredefinedRole;
import com.elepy.dao.Crud;
import com.elepy.exceptions.ElepyException;

import java.util.*;
import java.util.stream.Collectors;

import static com.elepy.dao.Filters.search;

public class Policy {


    @Inject
    private Crud<Role> customRoles;

    private final List<Role> predefinedRoles = new ArrayList<>();
    private final Set<String> availablePermissions = new TreeSet<>();


    public void registerPredefinedRole(PredefinedRole predefinedRole) {
        final var role = new Role();

        role.setId(predefinedRole.id());
        role.setName(predefinedRole.name());
        role.setPermissions(List.of(predefinedRole.permissions()));
        role.setDescription(predefinedRole.description());
        predefinedRoles.add(role);
    }

    public void assureNotPredefinedRole(String id) {
        if (existsAsPredefinedRole(id)) {
            throw ElepyException.translated("{elepy.models.roles.exceptions.predefinedRole}");
        }
    }

    public boolean existsAsPredefinedRole(String id) {
        return predefinedRoles.stream().anyMatch(role -> role.getId().equals(id)
                || role.getName().equals(id));

    }

    public List<Role> getPredefinedRoles() {
        return predefinedRoles;
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
        final var collect = user.getRoles().stream()
                .map(this::getRole)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Role::getPermissions)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        collect.add(DefaultPermissions.AUTHENTICATED);
        return collect;

    }

    public Optional<Role> getRole(String id) {
        return predefinedRoles.stream()
                .filter(role -> id.equals(role.getId()))
                .findFirst()
                .or(() -> customRoles.getById(id));
    }


}

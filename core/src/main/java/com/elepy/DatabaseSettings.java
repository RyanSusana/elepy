package com.elepy;

import com.elepy.dao.CrudFactory;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import lombok.Setter;

@ApplicationScoped
@Getter
@Setter
public class DatabaseSettings {
    private CrudFactory crudFactory;


}

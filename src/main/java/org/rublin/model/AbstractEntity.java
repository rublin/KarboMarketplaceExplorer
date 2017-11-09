package org.rublin.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public abstract class AbstractEntity {

    @Id
    private String id;
}

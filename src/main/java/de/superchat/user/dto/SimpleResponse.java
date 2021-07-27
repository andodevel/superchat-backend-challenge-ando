package de.superchat.user.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimpleResponse<T extends Serializable> {

    /**
     * Wrap simple result in JSON
     */
    private T result;

}

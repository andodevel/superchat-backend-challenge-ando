package de.superchat.user.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleResponse<T extends Serializable> {

    /**
     * Wrap simple result in JSON
     */
    private T result;

}

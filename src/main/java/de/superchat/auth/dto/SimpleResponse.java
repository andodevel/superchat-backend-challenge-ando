package de.superchat.auth.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleResponse {

    /**
     * Wrap simple result in JSON
     */
    private Serializable result;

}

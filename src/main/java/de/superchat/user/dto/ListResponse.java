package de.superchat.user.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListResponse<T extends Serializable> {

    private int total;
    private int pageIndex;
    private int pageSize;
    private List<T> results;


}
